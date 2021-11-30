package hn.edu.ujcv.baleadashermanas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.gson.Gson
import hn.edu.ujcv.baleadashermanas.DataCollection.EmpleadosDataCollectionItem
import hn.edu.ujcv.baleadashermanas.DataCollection.InventarioDataCollectionItem
import hn.edu.ujcv.baleadashermanas.DataCollection.RestApiError
import hn.edu.ujcv.baleadashermanas.Service.EmpleadoService
import hn.edu.ujcv.baleadashermanas.Service.InventarioService
import hn.edu.ujcv.baleadashermanas.Service.RestEngine
import kotlinx.android.synthetic.main.activity_inventario.*
import java.util.*

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Inventario : AppCompatActivity() {
    var tipoMovimiento = ArrayList<String>()
    var nombreUsuario: String = ""
    var idEmpleado = ""
    var idProducto = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventario)
        llenarSpinner()
        inicializar()
        callServiceGetEmpleadoByUsuario(nombreUsuario)
        btn_menuPrincipalInventario.setOnClickListener {
            val intent = Intent(this, Principal::class.java)
            intent.putExtra("nombreUsuario", nombreUsuario)
            startActivity(intent)
        }

        btn_guardarProducto.setOnClickListener {
            v-> callServicePostProducto()
        }

        btn_actualizarProducto.setOnClickListener {
                v -> callServicePutProducto()
        }
        btn_buscarProducto.setOnClickListener {
                v-> buscarProducto()
        }
        btn_borrarProducto.setOnClickListener {
                v-> callServiceDeleteProducto()
        }

    }


    private fun llenarSpinner(){
        val arrayAdapter: ArrayAdapter<*>
        tipoMovimiento.add("Seleccione el tipo de movimiento")
        tipoMovimiento.add("Ingreso")
        tipoMovimiento.add("Retiro")
        arrayAdapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,tipoMovimiento)
        spi_tipoMovimiento.adapter = arrayAdapter
    }

    private fun inicializar(){
        var intent = intent
        nombreUsuario = intent.getSerializableExtra("nombreUsuario") as String
        txv_labelNombreUsuarioInventario.setText(nombreUsuario)
    }


        private fun accionesBuscar(){
        btn_guardarProducto.isEnabled = false
        btn_actualizarProducto.isEnabled = true
        btn_borrarProducto.isEnabled = true
    }

    private fun callServiceGetEmpleadoByUsuario(usuario: String){
        val empleadoService: EmpleadoService = RestEngine.buildService().create(EmpleadoService::class.java)
        var result: Call<EmpleadosDataCollectionItem> = empleadoService.getEmpleadoByUsuario(usuario)

        result.enqueue(object :  Callback<EmpleadosDataCollectionItem> {
            override fun onFailure(call: Call<EmpleadosDataCollectionItem>, t: Throwable) {
                Toast.makeText(this@Inventario,"Error",Toast.LENGTH_LONG).show()
            }
            override fun onResponse(
                call: Call<EmpleadosDataCollectionItem>,
                response: Response<EmpleadosDataCollectionItem>
            ) {

                if(response!!.body() == null){
                    Toast.makeText(this@Inventario, "El empleado no existe", Toast.LENGTH_LONG).show()
                    return
                }
                idEmpleado = response.body()?.idempleado.toString()
            }

        }
        )
    }

    private fun callServicePostProducto() {
        if(estaVacio()){
            return
        }

        val f: Calendar
        f = Calendar.getInstance()
        val d: Int = f.get(Calendar.DATE)
        val mes: Int = 1 + f.get(Calendar.MONTH)
        val año: Int = f.get(Calendar.YEAR)
        val fecha = "$año-$mes-$d"

        val productoInfo = InventarioDataCollectionItem(
            idproducto = 0, // Este se pone asi porque es automatico
            nombreproducto = txt_nombreProducto.text.toString(),
            idempleado= idEmpleado.toLong(),
            cantidadstock= txt_cantidadProducto.text.toString(),
            fechaintroduccion = fecha,
            tipomovimiento = spi_tipoMovimiento.selectedItem.toString().substring(0,1).lowercase(),
            precio = txt_precioProducto.text.toString()
        )
        addProducto(productoInfo) {
            if (it?.idproducto != null) {
                Toast.makeText(this,"Producto añadido exitosamente", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this,"Error", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun addProducto(clienteData: InventarioDataCollectionItem, onResult: (InventarioDataCollectionItem?) -> Unit){
        if(estaVacio()){
            return
        }
        val retrofit = RestEngine.buildService().create(InventarioService::class.java)
        var result: Call<InventarioDataCollectionItem> = retrofit.addProducto(clienteData)
        result.enqueue(object : Callback<InventarioDataCollectionItem> {
            override fun onFailure(call: Call<InventarioDataCollectionItem>, t: Throwable) {
                onResult(null)
            }

            override fun onResponse(call: Call<InventarioDataCollectionItem>,
                                    response: Response<InventarioDataCollectionItem>
            ) {
                if (response.isSuccessful) {
                    val addedCasoEmpleado = response.body()!!
                    onResult(addedCasoEmpleado)
                    accionesPorDefecto()
                    limpiar()
                }
                else if (response.code() == 401){
                    Toast.makeText(this@Inventario,"Sesion expirada",Toast.LENGTH_LONG).show()
                }
                else if (response.code() == 500){
                    //val gson = Gson()
                    //val type = object : TypeToken<RestApiError>() {}.type
                    //var errorResponse1: RestApiError? = gson.fromJson(response.errorBody()!!.charStream(), type)
                    val errorResponse = Gson().fromJson(response.errorBody()!!.string()!!, RestApiError::class.java)


                    Toast.makeText(this@Inventario,errorResponse.errorDetails, Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this@Inventario,"Fallo al traer el item", Toast.LENGTH_LONG).show()
                }
            }

        }
        )
    }

    private fun accionesPorDefecto(){
        btn_guardarProducto.isEnabled = true
        btn_actualizarProducto.isEnabled = false
        btn_borrarProducto.isEnabled = false
        btn_buscarProducto.isEnabled = true
    }

    private fun limpiar(){
        txt_nombreProducto.setText("")
        txt_cantidadProducto.setText("")
        txt_precioProducto.setText("")
        spi_tipoMovimiento.setSelection(0)
    }

    private fun estaVacio():Boolean{
        if(txt_nombreProducto.text.isEmpty()) {
            txt_nombreProducto.error = "Debe rellenar el nombre del producto"
            return true

        }else if(txt_cantidadProducto.text.toString().isEmpty()){
            txt_cantidadProducto.error = "Debe rellenar la cantidad de producto"
            return true
        }
        else if(spi_tipoMovimiento.selectedItem.equals("Seleccione el tipo de movimiento")) {
            Toast.makeText(this,"Debe seleccionar un tipo de movimiento",Toast.LENGTH_SHORT).show()
            return true
        }
        return false
    }


    private fun callServicePutProducto() {

        val f: Calendar
        f = Calendar.getInstance()
        val d: Int = f.get(Calendar.DATE)
        val mes: Int = 1 + f.get(Calendar.MONTH)
        val año: Int = f.get(Calendar.YEAR)
        val fecha = "$año-$mes-$d"

        val productoInfo = InventarioDataCollectionItem(
            idproducto = idProducto.toLong(), // Este se pone asi porque es automatico
            nombreproducto = txt_nombreProducto.text.toString(),
            idempleado= idEmpleado.toLong(),
            cantidadstock= txt_cantidadProducto.text.toString(),
            fechaintroduccion = fecha,
            tipomovimiento = spi_tipoMovimiento.selectedItem.toString().substring(0,1).lowercase(),
            precio = txt_precioProducto.text.toString()
        )

        val retrofit = RestEngine.buildService().create(InventarioService::class.java)
        var result: Call<InventarioDataCollectionItem> = retrofit.updateProducto(productoInfo)
        result.enqueue(object : Callback<InventarioDataCollectionItem> {
            override fun onFailure(call: Call<InventarioDataCollectionItem>, t: Throwable) {
                Toast.makeText(this@Inventario,"Error", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<InventarioDataCollectionItem>,
                                    response: Response<InventarioDataCollectionItem>
            ) {
                if (response.isSuccessful) {
                    val updatedPerson = response.body()!!
                    Toast.makeText(this@Inventario,"Actualizado correctamente", Toast.LENGTH_LONG).show()
                    accionesPorDefecto()
                    limpiar()
                }
                else if (response.code() == 401){
                    Toast.makeText(this@Inventario,"Sesion expirada", Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this@Inventario,"Fallo al traer el item", Toast.LENGTH_LONG).show()
                }
            }

        })
    }


    fun buscarProducto(){
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle("Ingrese el nombre del producto")
        builder.setMessage("Por favor ingrese el nombre del producto a buscar, en caso de querer verificar nuevamente presione el boton \"Cancelar\"")
        val dialogLayout = inflater.inflate(R.layout.alert_dialog_producto, null)
        val editText  = dialogLayout.findViewById<EditText>(R.id.editText)
        builder.setView(dialogLayout)
        builder.setPositiveButton("Enviar") { dialogInterface, i ->
            if(editText.text.toString() == ""){
                Toast.makeText(this, "No puede dejar el nombre vacío", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }
            callServiceGetProductobyNombre(editText.text.toString())
        }
        builder.setNegativeButton("Cancelar"){dialogInterface, i -> return@setNegativeButton}
        builder.show()
    }

    private fun callServiceGetProductobyNombre(nombreProducto: String){
        val inventarioService: InventarioService = RestEngine.buildService().create(InventarioService::class.java)
        var result: Call<InventarioDataCollectionItem> = inventarioService.getProductobyNombre(nombreProducto)

        result.enqueue(object : Callback<InventarioDataCollectionItem> {
            override fun onFailure(call: Call<InventarioDataCollectionItem>, t: Throwable) {
                Toast.makeText(this@Inventario,"Error", Toast.LENGTH_LONG).show()
            }
            override fun onResponse(
                call: Call<InventarioDataCollectionItem>,
                response: Response<InventarioDataCollectionItem>
            ) {

                if(response!!.body() == null){
                    Toast.makeText(this@Inventario, "El producto no existe", Toast.LENGTH_LONG).show()
                    return
                }
                txt_nombreProducto.setText(response.body()?.nombreproducto)
                txt_cantidadProducto.setText(response.body()?.cantidadstock)
                txt_precioProducto.setText(response.body()?.precio)
                if(response.body()?.tipomovimiento.equals("i")){
                    spi_tipoMovimiento.setSelection(1)
                }else if(response.body()?.tipomovimiento.equals("r")){
                    spi_tipoMovimiento.setSelection(2)
                }
                idProducto = response.body()?.idproducto.toString()
                accionesBuscar()
                Toast.makeText(this@Inventario,"Producto recuperado exitosamente", Toast.LENGTH_LONG).show()
            }

        }
        )
    }

    private fun callServiceDeleteProducto() {
        val inventarioService: InventarioService =
            RestEngine.buildService().create(InventarioService::class.java)
        var result: Call<ResponseBody> = inventarioService.deleteProducto(idProducto.toLong())

        result.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@Inventario, "Error", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(this@Inventario, "Eliminado correctamente", Toast.LENGTH_LONG).show()
                    accionesPorDefecto()
                    limpiar()
                } else if (response.code() == 401) {
                    Toast.makeText(this@Inventario, "Sesion expirada", Toast.LENGTH_LONG)
                        .show()
                } else {
                    Toast.makeText(
                        this@Inventario,
                        "Fallo al traer el item",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
    }
}