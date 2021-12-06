package hn.edu.ujcv.baleadashermanas

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfWriter

import hn.edu.ujcv.baleadashermanas.DataCollection.*
import hn.edu.ujcv.baleadashermanas.Service.*
import kotlinx.android.synthetic.main.activity_facturacion.*
import kotlinx.android.synthetic.main.activity_inventario.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.FileOutputStream
import java.lang.Exception
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import com.itextpdf.text.pdf.PdfPCell

import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.Paragraph










class Facturacion : AppCompatActivity() {
    var nombreUsuario = ""
    var clientes = ArrayList<String>()
    var idEmpleado = ""
    var idCliente = ""
    var idDetalle = ""
    var idFactura = ""
    var idProducto = ""
    val cai = "35BD6A-0195F4-B34BAA-8B7D13-37791A-2D"
    var nombreProducto = ""
    var productos = ArrayList<String>()
    var subtotal = 0.0
    var contadorSpinnerMetodoPago = 0
    var numero = 0
    var totalPrecioOrden = 0.0
    var nombreCliente = ""
    var rtnCliente = ""


    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_facturacion)
        inicializar()
        llenarSpinnerCliente()
        llenarSpinnerMetodoPago()
        callServiceGetEmpleadoByUsuario(nombreUsuario)
        spi_metodoPago.isEnabled = false
        lsv_productos.isEnabled = false
        btn_menuPrincipalFacturacion.setOnClickListener {
                val intent = Intent(this, Principal::class.java)
                intent.putExtra("nombreUsuario", nombreUsuario)
                startActivity(intent)
        }
        btn_iniciarFactura.setOnClickListener {
                v-> callServicePostFacturaEncabezado()
        }

        btn_producto1.setOnClickListener {
                v -> llenarTabla(btn_producto1.text.toString())
        }

        btn_producto2.setOnClickListener {
                v-> llenarTabla(btn_producto2.text.toString())
        }

        btn_producto3.setOnClickListener {
                v-> llenarTabla(btn_producto3.text.toString())
        }

        btn_producto4.setOnClickListener {
                v-> llenarTabla(btn_producto4.text.toString())
        }

        btn_producto5.setOnClickListener {
                v-> llenarTabla(btn_producto5.text.toString())
        }

        btn_producto6.setOnClickListener {
                v-> llenarTabla(btn_producto6.text.toString())
        }

        btn_producto7.setOnClickListener {
                v-> llenarTabla(btn_producto7.text.toString())
        }

        btn_producto8.setOnClickListener {
                v-> llenarTabla(btn_producto8.text.toString())
        }

        btn_producto9.setOnClickListener {
                v-> llenarTabla(btn_producto9.text.toString())
        }

        btn_cancelar.setOnClickListener {
                v-> cancelarFactura(idFactura)

        }

        btn_pagar.setOnClickListener {
                v-> guardarFactura()
        }

        spi_metodoPago.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                contadorSpinnerMetodoPago++
                if(contadorSpinnerMetodoPago == 1){
                    return
                }
                    if(validarSpinnerMetodoPagoVacio()){
                        contadorSpinnerMetodoPago++
                        txt_pago.isEnabled = false
                        txt_pago.setText("")
                        return
                    }
                    else{
                        txt_pago.isEnabled = true
                    }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        lsv_productos.onItemClickListener = object :AdapterView.OnItemClickListener{
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if(position == 0){
                    return
                }else{
                    var texto = lsv_productos.getItemAtPosition(position).toString()
                    var lista = texto.split("|")
                    var idDetalleABorrar = lista[3]
                    eliminarProducto(idDetalleABorrar, position)
                }
            }
        }

        txt_pago.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                cambio()
            }
        })
    }

    private fun imprimirFactura(){
            savePDF(productos)
    }

    private fun validarPermisos(){
            if (!checkPermission()){
                showPermissionDialog();
            }
    }

    private fun showPermissionDialog() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data = Uri.parse(
                    String.format(
                        "package:%s", *arrayOf<Any>(
                            applicationContext.packageName
                        )
                    )
                )
                startActivityForResult(intent, 2000)
            } catch (e: Exception) {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                startActivityForResult(intent, 2000)
            }
        } else ActivityCompat.requestPermissions(
            this@Facturacion,
            arrayOf<String>(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE),
            333
        )
    }

    private fun checkPermission(): Boolean {
        return if (SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val write: Int = ContextCompat.checkSelfPermission(
                applicationContext,
                WRITE_EXTERNAL_STORAGE
            )
            val read: Int = ContextCompat.checkSelfPermission(
                applicationContext,
                READ_EXTERNAL_STORAGE
            )
            write == PackageManager.PERMISSION_GRANTED &&
                    read == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        @NonNull permissions: Array<String?>,
        @NonNull grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 333) {
            if (grantResults.size > 0) {
                val write = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val read = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (read && write) {
                } else {
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2000) {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                } else {
                }
            }
        }
    }

    private fun savePDF(array: ArrayList<String>){
        val mDoc = Document()
        val mFileName = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            .format(System.currentTimeMillis())
        val mFilePath = Environment.getExternalStorageDirectory().toString() + "/" + mFileName + ".pdf"
        try{

            val f: Calendar
            f = Calendar.getInstance()
            val d: Int = f.get(Calendar.DATE)
            val mes: Int = 1 + f.get(Calendar.MONTH)
            val año: Int = f.get(Calendar.YEAR)
            val fechaActual = "$d/$mes/$año"
            val locale = Locale("es", "HN")
            val currencyFormatter: NumberFormat = NumberFormat.getCurrencyInstance(locale)

            PdfWriter.getInstance(mDoc,FileOutputStream(mFilePath))
            mDoc.open()
            mDoc.addAuthor("Carlos Chamorro")

            val titulo = Paragraph(
                "Baleadas Hermanas",
                Font(Font.FontFamily.HELVETICA, 50.0f, Font.BOLD, BaseColor.BLACK)
            )
            mDoc.add(titulo)
            val caiData = Paragraph(
                "CAI : $cai",
            )
            capturarICliente()
            val espacio = Paragraph("\n")
            mDoc.add(espacio)
            mDoc.add(caiData)
            var usuario = txv_labelNombreUsuarioFacturacion.text.toString()
            val numeroFacturaData = Paragraph("Número de la factura: $idFactura")
            val nombreEmpleado = Paragraph("Nombre del empleado: $usuario")
            val cliente = Paragraph("Nombre del cliente: $nombreCliente")
            val rtn = Paragraph("RTN del cliente: $rtnCliente")
            val fecha = Paragraph("Fecha: $fechaActual")
            mDoc.add(numeroFacturaData)
            mDoc.add(nombreEmpleado)
            mDoc.add(cliente)
            mDoc.add(rtn)
            mDoc.add(fecha)
            mDoc.add(espacio)

            for (i in 0..(productos.size-1)){
                val lista = productos.get(i).split("|")

                val columna1 = lista[0]
                val columna2 = lista[1]
                var columna3 = lista[2]

                if(i>0){
                    columna3 = currencyFormatter.format(lista[2].toDouble())
                }

                val table = PdfPTable(3)
                var cell = PdfPCell(Phrase(columna1))
                cell.horizontalAlignment = Element.ALIGN_CENTER
                cell.verticalAlignment = Element.ALIGN_CENTER
                table.addCell(cell)

                cell = PdfPCell(Phrase(columna2))
                cell.setPadding(5f)
                cell.isUseAscender = true
                cell.isUseDescender = true
                cell.horizontalAlignment = Element.ALIGN_CENTER
                table.addCell(cell)

                cell = PdfPCell()
                cell.setPadding(5f)
                cell.isUseAscender = true
                cell.isUseDescender = true
                val p = Paragraph(columna3)
                p.alignment = Element.ALIGN_CENTER
                cell.addElement(p)
                table.addCell(cell)
                mDoc.add(table)
            }
            mDoc.add(espacio)
            mDoc.add(espacio)
            val totalCobrado = txt_total.text.toString()
            val totalPagado = txt_pago.text.toString()
            val cambioTotal = txt_cambio.text.toString()
            val totalFinal = currencyFormatter.format(totalCobrado.toDouble())
            val pagoFinal = currencyFormatter.format(totalPagado.toDouble())
            val cambiolFinal = currencyFormatter.format(cambioTotal.substring(1).toDouble())
            var total = Paragraph("Total $totalFinal")
            var pago = Paragraph("Pago $pagoFinal")
            var cambio = Paragraph("Cambio $cambiolFinal")
            mDoc.add(pago)
            mDoc.add(cambio)
            mDoc.add(total)
            mDoc.close()
        }catch(e: Exception){
            Toast.makeText(this, e.message,Toast.LENGTH_SHORT).show()
        }
    }

    fun actualizarTotal() {
        val isv: Double = totalPrecioOrden * 0.15
        val subtotal: Double = totalPrecioOrden - isv
        txt_total.setText(java.lang.String.valueOf(totalPrecioOrden))
        txt_isv.setText(isv.toString())
        txt_subtotal.setText(subtotal.toString())
    }

    private fun eliminarProducto(idDetalle:String, position: Int){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("¿Desea eliminar este producto?")
        builder.setMessage("¿Está seguro que desea eliminar el producto?")
        builder.setPositiveButton("Sí") { dialogInterface: DialogInterface, i: Int ->
            callServiceDeleteDetalle(idDetalle, position)
        }
        builder.setNegativeButton("Cancelar") { dialogInterface: DialogInterface, i: Int ->
            return@setNegativeButton
        }
        builder.show()
    }

    private fun cancelarFactura(idFactura:String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("¿Desea cancelar ésta factura?")
        builder.setMessage("¿Está seguro que desea eliminar la facura?")
        builder.setPositiveButton("Sí") { dialogInterface: DialogInterface, i: Int ->
            callServiceDeleteAllDetalles(idFactura)
        }
        builder.setNegativeButton("Cancelar") { dialogInterface: DialogInterface, i: Int ->
            return@setNegativeButton
        }
        builder.show()
    }

    private fun callServiceDeleteAllDetalles(id:String) {
        val facturaDetalleService: FacturaDetalleService =
            RestEngine.buildService().create(FacturaDetalleService::class.java)
        var result: Call<ResponseBody> = facturaDetalleService.deleteAllDetalles(id.toLong())

        result.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@Facturacion, "Error", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    callServiceDeleteFactura(idFactura)
                } else if (response.code() == 401) {
                    Toast.makeText(this@Facturacion, "Sesion expirada", Toast.LENGTH_LONG)
                        .show()
                } else {
                    Toast.makeText(
                        this@Facturacion,
                        "Fallo al traer el item",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
    }


    private fun callServiceDeleteFactura(idFactura:String) {
        val facturaEncabezadoService: FacturaEncabezadoService =
            RestEngine.buildService().create(FacturaEncabezadoService::class.java)
        var result: Call<ResponseBody> = facturaEncabezadoService.deleteFactura(idFactura.toLong())

        result.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@Facturacion, "Error", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(this@Facturacion, "Factura cancelada exitosamente", Toast.LENGTH_LONG).show()
                    accionesCancelar()
                } else if (response.code() == 401) {
                    Toast.makeText(this@Facturacion, "Sesion expirada", Toast.LENGTH_LONG)
                        .show()
                } else {
                    Toast.makeText(
                        this@Facturacion,
                        "Fallo al traer el item",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
    }

    fun accionesCancelar() {
        nombreProducto = ""
        idFactura = ""
        idCliente = ""
        idProducto = ""
        idDetalle = ""
        contadorSpinnerMetodoPago = 0
        txt_subtotal.setText("0")
        txt_isv.setText("0")
        txt_total.setText("0")
        txt_pago.setText("")
        txt_cambio.setText("")
        spi_metodoPago.setSelection(0)
        spi_nombreCliente.isEnabled = true
        spi_nombreCliente.setSelection(0)
        habilitarProductos(false)
        botonesPorDefecto()
        productos.clear()
        productos.add("Nombre del Producto|Cantidad|Precio|ID")
        val arrayAdapter: ArrayAdapter<*>
        arrayAdapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,productos)
        lsv_productos.adapter = arrayAdapter
        txt_subtotal.isEnabled = false
        txt_isv.isEnabled = false
        txt_total.isEnabled = false
        lsv_productos.isEnabled = false
        spi_metodoPago.isEnabled = false
    }

    private fun callServiceDeleteDetalle(idDetalle:String, position: Int) {
        val facturaDetalleService: FacturaDetalleService =
            RestEngine.buildService().create(FacturaDetalleService::class.java)
        var result: Call<ResponseBody> = facturaDetalleService.deleteDetalle(idDetalle.toLong())

        result.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@Facturacion, "Error", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(this@Facturacion, "Eliminado correctamente", Toast.LENGTH_LONG).show()
                } else if (response.code() == 401) {
                    Toast.makeText(this@Facturacion, "Sesion expirada", Toast.LENGTH_LONG)
                        .show()
                } else {
                    Toast.makeText(
                        this@Facturacion,
                        "Fallo al traer el item",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
        var texto = lsv_productos.getItemAtPosition(position)
        productos.remove(texto)
        val arrayAdapter: ArrayAdapter<*>
        arrayAdapter = ArrayAdapter(this@Facturacion,android.R.layout.simple_list_item_1,productos)
        lsv_productos.adapter = arrayAdapter
        totalPrecioOrden = 0.0
        for(i in 1..productos.size - 1){
            val lista = productos.get(i).split("|")
            totalPrecioOrden += lista[2].toDouble()
        }
        actualizarTotal()
    }

    private fun cambio() {
        if(txt_pago.text.isEmpty()){
            txt_cambio.setText("")
            txt_cambio.isEnabled = false
            return
        }
        val locale = Locale("es", "HN")
        val currencyFormatter: NumberFormat = NumberFormat.getCurrencyInstance(locale)
        val pagoCliente: String = txt_pago.text.toString()
        val pago = pagoCliente.toDouble()
        val total: Double = txt_total.text.toString().toDouble()
        val cambio = pago - total
        val cambioTotal: String = currencyFormatter.format(cambio)
        txt_cambio.setText(cambioTotal)
        txt_cambio.isEnabled = true
    }

    private fun validarSpinnerMetodoPagoVacio(): Boolean{
        if(spi_metodoPago.selectedItem.equals("Seleccione el método de pago")) {
            Toast.makeText(this, "Debe seleccionar un método de pago", Toast.LENGTH_LONG).show()
            return true
        }
        return false
    }

    private fun llenarSpinnerCliente() {
        val personService:ClienteService = RestEngine.buildService().create(ClienteService::class.java)
        var result: Call<List<ClienteDataCollectionItem>> = personService.listClientes()

        result.enqueue(object : Callback<List<ClienteDataCollectionItem>> {
            override fun onFailure(call: Call<List<ClienteDataCollectionItem>>, t: Throwable) {
                Toast.makeText(this@Facturacion,"Error", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<List<ClienteDataCollectionItem>>,
                response: Response<List<ClienteDataCollectionItem>>
            ) {
                for(i in 0..(response.body()!!.size-1)){
                    clientes.add(response.body()!!.get(i).idcliente.toString() + "." + response.body()!!.get(i).primer_nombre_cliente + " "+ response.body()!!.get(i).primer_apellido_cliente)
                    val arrayAdapter: ArrayAdapter<*>
                    arrayAdapter = ArrayAdapter(this@Facturacion,android.R.layout.simple_list_item_1,clientes)
                    spi_nombreCliente.adapter = arrayAdapter
                    rtnCliente = response.body()!!.get(i).rtncliente
                }

            }
        })
    }



    private fun llenarSpinnerMetodoPago(){
        val arrayAdapter: ArrayAdapter<*>
        var metodoPago = ArrayList<String>()
        metodoPago.add("Seleccione el método de pago")
        metodoPago.add("Efectivo")
        metodoPago.add("Tarjeta")
        arrayAdapter = ArrayAdapter(this@Facturacion,android.R.layout.simple_list_item_1,metodoPago)
        spi_metodoPago.adapter = arrayAdapter
    }

    private fun llenarTabla(nombre :String){
        nombreProducto = nombre
        callServiceGetProductoByNombre(nombreProducto)
    }


    private fun capturarPrecio(texto: String){
        val lista = texto.split("|")
        val precio = lista[2]
        val precioSubtotal = precio.toDouble()
        subtotal = precioSubtotal
    }

    private fun calcularTotal(){
        val totalAnterior: Double = txt_total.text.toString().toDouble()
        val totalNuevo: Double = totalAnterior + subtotal
        val isv = totalNuevo * 0.15
        val subtotalConImpuesto = totalNuevo - isv
        val total = subtotalConImpuesto + isv
        val subtotalFinal = subtotalConImpuesto.toString()
        val isvFinal = isv.toString()
        val totalFinal = total.toString()
        txt_subtotal.setText(subtotalFinal)
        txt_isv.setText(isvFinal)
        txt_total.setText(totalFinal)
        callServicePostFacturaDetalle()
    }

    private fun callServiceGetProductoByNombre(producto: String){
        val inventarioService: InventarioService = RestEngine.buildService().create(InventarioService::class.java)
        var result: Call<InventarioDataCollectionItem> = inventarioService.getProductobyNombre(producto)

        result.enqueue(object :  Callback<InventarioDataCollectionItem> {
            override fun onFailure(call: Call<InventarioDataCollectionItem>, t: Throwable) {
                Toast.makeText(this@Facturacion,"Error",Toast.LENGTH_LONG).show()
            }
            override fun onResponse(
                call: Call<InventarioDataCollectionItem>,
                response: Response<InventarioDataCollectionItem>
            ) {
                if(response!!.body() == null){
                    Toast.makeText(this@Facturacion, "El producto no existe", Toast.LENGTH_LONG).show()
                    return
                }
                nombreProducto = response.body()?.nombreproducto.toString()
                productos.add(response.body()!!.nombreproducto + "|" + "1" + "|" + response.body()!!.precio)
                //Toast.makeText(this@Facturacion, array.get((array.size - 1)), Toast.LENGTH_LONG).show()
                idProducto = response.body()!!.idproducto.toString()
                txv_idProducto.setText(idProducto)
                numero++
                capturarPrecio(productos.get((productos.size)-1))
                val arrayAdapter: ArrayAdapter<*>
                arrayAdapter = ArrayAdapter(this@Facturacion,android.R.layout.simple_list_item_1,productos)
                lsv_productos.adapter = arrayAdapter
                calcularTotal()
                habilitarTextos()

            }
        }
        )
    }

    private fun habilitarTextos(){
        txt_total.isEnabled = true
        txt_subtotal.isEnabled = true
        txt_isv.isEnabled = true
    }


    private fun inicializar(){
        var intent = intent
        nombreUsuario = intent.getSerializableExtra("nombreUsuario") as String
        txv_labelNombreUsuarioFacturacion.setText(nombreUsuario)
        productos.add("Nombre del Producto|Cantidad|Precio|ID")
        val arrayAdapter: ArrayAdapter<*>
        arrayAdapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,productos)
        lsv_productos.adapter = arrayAdapter
        validarPermisos()
    }


    private fun capturarICliente(){
        val string = spi_nombreCliente.selectedItem.toString()
        val lista = string.split(".")
        idCliente = lista[0]
        nombreCliente = lista[1]
    }



    private fun callServiceGetEmpleadoByUsuario(usuario: String){
        val empleadoService: EmpleadoService = RestEngine.buildService().create(EmpleadoService::class.java)
        var result: Call<EmpleadosDataCollectionItem> = empleadoService.getEmpleadoByUsuario(usuario)

        result.enqueue(object :  Callback<EmpleadosDataCollectionItem> {
            override fun onFailure(call: Call<EmpleadosDataCollectionItem>, t: Throwable) {
                Toast.makeText(this@Facturacion,"Error",Toast.LENGTH_LONG).show()
            }
            override fun onResponse(
                call: Call<EmpleadosDataCollectionItem>,
                response: Response<EmpleadosDataCollectionItem>
            ) {

                if(response!!.body() == null){
                    Toast.makeText(this@Facturacion, "El empleado no existe", Toast.LENGTH_LONG).show()
                    return
                }
                idEmpleado = response.body()?.idempleado.toString()
            }

        }
        )
    }


    private fun callServicePostFacturaEncabezado() {
        val f: Calendar
        f = Calendar.getInstance()
        val d: Int = f.get(Calendar.DATE)
        val mes: Int = 1 + f.get(Calendar.MONTH)
        val año: Int = f.get(Calendar.YEAR)
        val fecha = "$año-$mes-$d"

        capturarICliente()

        val facturaInfo = FacturaEncabezadoDataCollectionItem(
            idfactura = 0, // Este se pone asi porque es automatico
            cai = cai,
            idempleado = idEmpleado,
            totalfactura = "0",
            idcliente = idCliente.toLong(),
            fecha_factura = fecha,
            cambio_factura = "0",
            pago_factura = "0"
        )
        addFacturaEncabezado(facturaInfo) {
            if (it?.idfactura != null) {
                Toast.makeText(this,"Factura iniciada exitosamente", Toast.LENGTH_LONG).show()
                habilitarProductos(true)
                accionesIniciar()
                capturarIdFactura()
            } else {
                Toast.makeText(this,"Error", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun validacionPago() {
        if (spi_metodoPago.getSelectedItem().equals("Seleccione el método")) {
            Toast.makeText(this@Facturacion,"Por favor seleccione el método de pago",Toast.LENGTH_SHORT).show()
            return
        }
        if (txt_pago.text.equals("")) {
            txt_pago.error = "Por favor ingrese el pago"
            return
        }
        if (txt_cambio.text.equals("")) {
            txt_cambio.error = "Por favor ingrese el pago"
            return
        }
        val total: Double = txt_total.text.toString().toDouble()
        val pago: Double = txt_pago.text.toString().toDouble()
        if (total > pago) {
            val locale = Locale("es", "HN")
            val currencyFormatter = NumberFormat.getCurrencyInstance(locale)
            val resto = total - pago
            val restoTotal = currencyFormatter.format(resto)
            Toast.makeText(this,"Pago insuficiente, faltan: L $restoTotal ", Toast.LENGTH_SHORT).show()
            return
        }
    }

    private fun guardarFactura() {

        validacionPago()
        val f: Calendar
        f = Calendar.getInstance()
        val d: Int = f.get(Calendar.DATE)
        val mes: Int = 1 + f.get(Calendar.MONTH)
        val año: Int = f.get(Calendar.YEAR)
        val fecha = "$año-$mes-$d"

        capturarICliente()
        var cambio = txt_cambio.text.toString().substring(1)

        val facturaInfo = FacturaEncabezadoDataCollectionItem(
            idfactura = idFactura.toLong(), // Este se pone asi porque es automatico
            cai = cai,
            idempleado = idEmpleado,
            totalfactura = txt_total.text.toString(),
            idcliente = idCliente.toLong(),
            fecha_factura = fecha,
            cambio_factura = cambio,
            pago_factura = txt_pago.text.toString()
        )
        addFacturaEncabezado(facturaInfo) {
            if (it?.idfactura != null) {
                Toast.makeText(this,"Factura pagada exitosamente", Toast.LENGTH_LONG).show()
                imprimirFactura()
                habilitarProductos(true)
                accionesCancelar()
            } else {
                Toast.makeText(this,"Error", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun addFacturaEncabezado(clienteData: FacturaEncabezadoDataCollectionItem, onResult: (FacturaEncabezadoDataCollectionItem?) -> Unit){
        val retrofit = RestEngine.buildService().create(FacturaEncabezadoService::class.java)
        var result: Call<FacturaEncabezadoDataCollectionItem> = retrofit.addFactura(clienteData)
        result.enqueue(object : Callback<FacturaEncabezadoDataCollectionItem> {
            override fun onFailure(call: Call<FacturaEncabezadoDataCollectionItem>, t: Throwable) {
                onResult(null)
            }

            override fun onResponse(call: Call<FacturaEncabezadoDataCollectionItem>,
                                    response: Response<FacturaEncabezadoDataCollectionItem>
            ) {
                if (response.isSuccessful) {
                    val addedCasoEmpleado = response.body()!!
                    onResult(addedCasoEmpleado)
                    //habilitarBotones()
                }
                else if (response.code() == 401){
                    Toast.makeText(this@Facturacion,"Sesion expirada",Toast.LENGTH_LONG).show()
                }
                else if (response.code() == 500){
                    //val gson = Gson()
                    //val type = object : TypeToken<RestApiError>() {}.type
                    //var errorResponse1: RestApiError? = gson.fromJson(response.errorBody()!!.charStream(), type)
                    val errorResponse = Gson().fromJson(response.errorBody()!!.string()!!, RestApiError::class.java)


                    Toast.makeText(this@Facturacion,errorResponse.errorDetails, Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this@Facturacion,"Fallo al traer el item", Toast.LENGTH_LONG).show()
                }
            }

        }
        )
    }

    private fun capturarIdFactura(){
        val facturaEncabezadoService:FacturaEncabezadoService = RestEngine.buildService().create(FacturaEncabezadoService::class.java)
        var result: Call<List<FacturaEncabezadoDataCollectionItem>> = facturaEncabezadoService.listFacturas()

        result.enqueue(object : Callback<List<FacturaEncabezadoDataCollectionItem>> {
            override fun onFailure(call: Call<List<FacturaEncabezadoDataCollectionItem>>, t: Throwable) {
                Toast.makeText(this@Facturacion,"Error", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<List<FacturaEncabezadoDataCollectionItem>>,
                response: Response<List<FacturaEncabezadoDataCollectionItem>>
            ) {
                idFactura = response.body()!!.get(response.body()!!.size-1).idfactura.toString()
            }
        })
    }

    private fun capturarIdDetalle(){
        val facturaDetalleService:FacturaDetalleService = RestEngine.buildService().create(FacturaDetalleService::class.java)
        var result: Call<List<FacturaDetalleCollectionItem>> = facturaDetalleService.listDetalles()
        val arrayAdapter: ArrayAdapter<*>
        arrayAdapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,productos)
        result.enqueue(object : Callback<List<FacturaDetalleCollectionItem>> {
            override fun onFailure(call: Call<List<FacturaDetalleCollectionItem>>, t: Throwable) {
                Toast.makeText(this@Facturacion,"Error", Toast.LENGTH_LONG).show()
            }
            override fun onResponse(
                call: Call<List<FacturaDetalleCollectionItem>>,
                response: Response<List<FacturaDetalleCollectionItem>>
            ) {
                idDetalle = response.body()!!.get(response.body()!!.size-1).iddetalle.toString()
                var filaAnterior = productos.get(productos.size - 1)
                productos.set(productos.size - 1,filaAnterior + "|" + idDetalle)
                lsv_productos.adapter = arrayAdapter
            }
        })
    }

    private fun callServicePostFacturaDetalle() {
        var cantidad = ""
        var precioProducto = ""
        var lista = (productos.get(productos.size- 1 )).split("|") // 1 cantidad 2 precio
        cantidad = lista[1]
        precioProducto = lista[2]
        idProducto = txv_idProducto.text.toString()
        val facturaInfo = FacturaDetalleCollectionItem(
            iddetalle = 0, // es automatico
            idfactura = idFactura.toLong(),
            idproducto = idProducto.toLong(),
            cantidadfactura = cantidad,
            precio = precioProducto
        )
        addFacturaDetalle(facturaInfo) {
            if (it?.iddetalle != null) {
                Toast.makeText(this,"Agregado producto", Toast.LENGTH_SHORT).show()
                capturarIdDetalle()
            } else {
                Toast.makeText(this,"Error", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun addFacturaDetalle(facturaData: FacturaDetalleCollectionItem, onResult: (FacturaDetalleCollectionItem?) -> Unit){
        val retrofit = RestEngine.buildService().create(FacturaDetalleService::class.java)
        var result: Call<FacturaDetalleCollectionItem> = retrofit.addDetalle(facturaData)
        result.enqueue(object : Callback<FacturaDetalleCollectionItem> {
            override fun onFailure(call: Call<FacturaDetalleCollectionItem>, t: Throwable) {
                onResult(null)
            }

            override fun onResponse(call: Call<FacturaDetalleCollectionItem>,
                                    response: Response<FacturaDetalleCollectionItem>
            ) {
                if (response.isSuccessful) {
                    val addedCasoEmpleado = response.body()!!
                    onResult(addedCasoEmpleado)
                    //habilitarBotones()
                }
                else if (response.code() == 401){
                    Toast.makeText(this@Facturacion,"Sesion expirada",Toast.LENGTH_LONG).show()
                }
                else if (response.code() == 500){
                    //val gson = Gson()
                    //val type = object : TypeToken<RestApiError>() {}.type
                    //var errorResponse1: RestApiError? = gson.fromJson(response.errorBody()!!.charStream(), type)
                    val errorResponse = Gson().fromJson(response.errorBody()!!.string()!!, RestApiError::class.java)


                    Toast.makeText(this@Facturacion,errorResponse.errorDetails, Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this@Facturacion,"Fallo al traer el item", Toast.LENGTH_LONG).show()
                }
            }

        }
        )
    }

    fun habilitarProductos(accion: Boolean) {
        btn_producto1.isEnabled = accion
        btn_producto2.isEnabled = accion
        btn_producto3.isEnabled = accion
        btn_producto4.isEnabled = accion
        btn_producto5.isEnabled = accion
        btn_producto6.isEnabled = accion
        btn_producto7.isEnabled = accion
        btn_producto8.isEnabled = accion
        btn_producto9.isEnabled = accion
    }

    fun accionesIniciar() {
        btn_iniciarFactura.isEnabled = false
        btn_pagar.isEnabled = true
        btn_cancelar.isEnabled = true
        spi_nombreCliente.isEnabled = false
        spi_metodoPago.setEnabled(true)
        lsv_productos.isEnabled = true
    }

    fun botonesPorDefecto() {
        btn_iniciarFactura.isEnabled = true
        btn_cancelar.setEnabled(false)
        btn_pagar.isEnabled = false
    }
}