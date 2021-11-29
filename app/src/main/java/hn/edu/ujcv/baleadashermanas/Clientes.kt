package hn.edu.ujcv.baleadashermanas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.gson.Gson
import hn.edu.ujcv.baleadashermanas.DataCollection.ClienteDataCollectionItem
import hn.edu.ujcv.baleadashermanas.DataCollection.RestApiError
import hn.edu.ujcv.baleadashermanas.Service.ClienteService
import hn.edu.ujcv.baleadashermanas.Service.RestEngine
import kotlinx.android.synthetic.main.activity_clientes.*
import kotlinx.android.synthetic.main.activity_empleados.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Clientes : AppCompatActivity() {
    var nombreUsuario: String = ""
    var idCliente = ""
    var dniCliente = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clientes)
        inicializar()
        btn_menuPrincipalClientes.setOnClickListener {
            val intent = Intent(this, Principal::class.java)
            intent.putExtra("nombreUsuario", nombreUsuario)
            startActivity(intent)
        }

        btn_guardarCliente.setOnClickListener {
            v -> callServicePostCliente()
        }

        btn_actualizarCliente.setOnClickListener {
            v -> callServicePutCliente()
        }

        btn_buscarCliente.setOnClickListener {
            v-> buscarCliente()
        }

        btn_borrarCliente.setOnClickListener {
            v-> callServiceDeleteCliente()
        }
    }

    private fun inicializar(){
        var intent = intent
        nombreUsuario = intent.getSerializableExtra("nombreUsuario") as String
        txv_labelNombreUsuarioClientes.setText(nombreUsuario)
    }

    private fun accionesBuscar(){
        btn_guardarCliente.isEnabled = false
        btn_actualizarCliente.isEnabled = true
        btn_borrarCliente.isEnabled = true
    }

    private fun callServicePostCliente() {
        if(estaVacio()){
            return
        }
        if(noLoSuficienteLargo()){
            return
        }


        val clienteInfo = ClienteDataCollectionItem(
            idcliente = 0, // Este se pone asi porque es automatico
            primer_nombre_cliente = txt_primerNombreCliente.text.toString(),
            segundo_nombre_cliente= txt_segundoNombreCliente.text.toString(),
            primer_apellido_cliente= txt_primerNombreCliente.text.toString(),
            segundo_apellido_cliente= txt_segundoApellidoCliente.text.toString(),
            telefono_cliente= txt_telefonoCliente.text.toString(),
            email_cliente= txt_emailCliente.text.toString(),
            dnicliente= txt_dniCliente.text.toString(),
            rtncliente= txt_rtnCliente.text.toString()
        )
        addCliente(clienteInfo) {
            if (it?.idcliente != null) {
                Toast.makeText(this,"Cliente añadido exitosamente", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this,"Error", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun addCliente(clienteData: ClienteDataCollectionItem, onResult: (ClienteDataCollectionItem?) -> Unit){
        if(estaVacio()){
            return
        }
        if(noLoSuficienteLargo()){
            return
        }
        val retrofit = RestEngine.buildService().create(ClienteService::class.java)
        var result: Call<ClienteDataCollectionItem> = retrofit.addCliente(clienteData)
        result.enqueue(object : Callback<ClienteDataCollectionItem> {
            override fun onFailure(call: Call<ClienteDataCollectionItem>, t: Throwable) {
                onResult(null)
            }

            override fun onResponse(call: Call<ClienteDataCollectionItem>,
                                    response: Response<ClienteDataCollectionItem>
            ) {
                if (response.isSuccessful) {
                    val addedCasoEmpleado = response.body()!!
                    onResult(addedCasoEmpleado)
                    accionesPorDefecto()
                    limpiar()
                }
                /*else if (response.code() == 401){
                    Toast.makeText(this@MainActivity,"Sesion expirada",Toast.LENGTH_LONG).show()
                }*/
                else if (response.code() == 500){
                    //val gson = Gson()
                    //val type = object : TypeToken<RestApiError>() {}.type
                    //var errorResponse1: RestApiError? = gson.fromJson(response.errorBody()!!.charStream(), type)
                    val errorResponse = Gson().fromJson(response.errorBody()!!.string()!!, RestApiError::class.java)


                    Toast.makeText(this@Clientes,errorResponse.errorDetails, Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this@Clientes,"Fallo al traer el item", Toast.LENGTH_LONG).show()
                }
            }

        }
        )
    }

    private fun accionesPorDefecto(){
        btn_guardarCliente.isEnabled = true
        btn_actualizarCliente.isEnabled = false
        btn_borrarCliente.isEnabled = false
        btn_buscarCliente.isEnabled = true
    }

    private fun limpiar(){
        txt_primerNombreCliente.setText("")
        txt_segundoNombreCliente.setText("")
        txt_primerApellidoCliente.setText("")
        txt_segundoApellidoCliente.setText("")
        txt_telefonoCliente.setText("")
        txt_emailCliente.setText("")
        txt_dniCliente.setText("")
        txt_rtnCliente.setText("")
    }

    private fun estaVacio():Boolean{
        if(txt_primerNombreCliente.text.isEmpty()) {
            txt_primerNombreCliente.error = "Debe rellenar el primer nombre"
            return true

        }else if(txt_segundoNombreCliente.text.toString().isEmpty()){
            txt_segundoNombreCliente.error = "Debe rellenar el segundo nombre"
            return true
        }
        else if(txt_primerApellidoCliente.text.toString().isEmpty()) {
            txt_primerApellidoCliente.error ="Debe rellenar el primer apellido"
            return true
        }
        else if(txt_segundoApellidoCliente.text.toString().isEmpty()) {
            txt_segundoApellidoCliente.error ="Debe rellenar el segundo apellido"
            return true
        }
        else if(txt_telefonoCliente.text.toString().isEmpty()) {
            txt_telefonoCliente.error ="Debe rellenar el teléfono"
            return true
        }
        else if(txt_emailCliente.text.toString().isEmpty()) {
            txt_emailCliente.error ="Debe rellenar el E-Mail"
            return true
        }
        else if(txt_dniCliente.text.toString().isEmpty()) {
            txt_dniCliente.error ="Debe rellenar el DNI"
            return true
        }
        else if(txt_rtnCliente.text.toString().isEmpty()) {
            txt_rtnCliente.error ="Debe rellenar el RTN"
            return true
        }
        return false
    }



    private fun noLoSuficienteLargo():Boolean{
        if(txt_telefonoCliente.text.toString().length != 8) {
            txt_telefonoCliente.error ="El número de teléfono no puede ser distino a 8 dígitos"
            return true
        }
        if(txt_dniCliente.text.toString().length != 13){
            txt_dniCliente.error = "El DNI no puede ser distino a 13 dígitos"
            return true
        }
        return false
    }

    private fun callServicePutCliente() {

        val clienteInfo = ClienteDataCollectionItem(
            idcliente = idCliente.toLong(),
            primer_nombre_cliente = txt_primerNombreCliente.text.toString(),
            segundo_nombre_cliente= txt_segundoNombreCliente.text.toString(),
            primer_apellido_cliente= txt_primerNombreCliente.text.toString(),
            segundo_apellido_cliente= txt_segundoApellidoCliente.text.toString(),
            telefono_cliente= txt_telefonoCliente.text.toString(),
            email_cliente= txt_emailCliente.text.toString(),
            dnicliente= txt_dniCliente.text.toString(),
            rtncliente= txt_rtnCliente.text.toString()
        )

        val retrofit = RestEngine.buildService().create(ClienteService::class.java)
        var result: Call<ClienteDataCollectionItem> = retrofit.updateCliente(clienteInfo)
        result.enqueue(object : Callback<ClienteDataCollectionItem> {
            override fun onFailure(call: Call<ClienteDataCollectionItem>, t: Throwable) {
                Toast.makeText(this@Clientes,"Error", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<ClienteDataCollectionItem>,
                                    response: Response<ClienteDataCollectionItem>
            ) {
                if (response.isSuccessful) {
                    val updatedPerson = response.body()!!
                    Toast.makeText(this@Clientes,"Actualizado correctamente", Toast.LENGTH_LONG).show()
                    accionesPorDefecto()
                    limpiar()
                }
                else if (response.code() == 401){
                    Toast.makeText(this@Clientes,"Sesion expirada", Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this@Clientes,"Fallo al traer el item", Toast.LENGTH_LONG).show()
                }
            }

        })
    }


    fun buscarCliente(){
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle("Ingrese el DNI del cliente")
        builder.setMessage("Por favor ingrese el DNI del cliente a buscar, en caso de querer verificar nuevamente presione el boton \"Cancelar\"")
        val dialogLayout = inflater.inflate(R.layout.alert_dialog_cliente, null)
        val editText  = dialogLayout.findViewById<EditText>(R.id.editText)
        builder.setView(dialogLayout)
        builder.setPositiveButton("Enviar") { dialogInterface, i ->
            if(editText.text.toString() == ""){
                Toast.makeText(this, "No puede dejar el DNI vacío", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }
            callServiceGetClientebyDNI(editText.text.toString())
        }
        builder.setNegativeButton("Cancelar"){dialogInterface, i -> return@setNegativeButton}
        builder.show()
    }

    private fun callServiceGetClientebyDNI(dni: String){
        val clienteService: ClienteService = RestEngine.buildService().create(ClienteService::class.java)
        var result: Call<ClienteDataCollectionItem> = clienteService.getClienteByDNI(dni)

        result.enqueue(object : Callback<ClienteDataCollectionItem> {
            override fun onFailure(call: Call<ClienteDataCollectionItem>, t: Throwable) {
                Toast.makeText(this@Clientes,"Error", Toast.LENGTH_LONG).show()
            }
            override fun onResponse(
                call: Call<ClienteDataCollectionItem>,
                response: Response<ClienteDataCollectionItem>
            ) {

                if(response!!.body() == null){
                    Toast.makeText(this@Clientes, "El cliente no existe", Toast.LENGTH_LONG).show()
                    return
                }
                txt_primerNombreCliente.setText(response.body()?.primer_nombre_cliente)
                txt_segundoNombreCliente.setText(response.body()?.segundo_nombre_cliente)
                txt_primerApellidoCliente.setText(response.body()?.primer_apellido_cliente)
                txt_segundoApellidoCliente.setText(response.body()?.segundo_apellido_cliente)
                txt_telefonoCliente.setText(response.body()?.telefono_cliente)
                txt_emailCliente.setText(response.body()?.email_cliente)
                txt_dniCliente.setText(response.body()?.dnicliente)
                txt_rtnCliente.setText(response.body()?.rtncliente)
                idCliente = response.body()?.idcliente.toString()
                dniCliente = response.body()?.dnicliente.toString()
                accionesBuscar()
                Toast.makeText(this@Clientes,"Cliente recuperado exitosamente", Toast.LENGTH_LONG).show()
            }

        }
        )
    }

    private fun callServiceDeleteCliente() {
        val clienteService: ClienteService =
            RestEngine.buildService().create(ClienteService::class.java)
        var result: Call<ResponseBody> = clienteService.deleteCliente(idCliente.toLong())

        result.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@Clientes, "Error", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(this@Clientes, "Eliminado correctamente", Toast.LENGTH_LONG).show()
                    accionesPorDefecto()
                    limpiar()
                } else if (response.code() == 401) {
                    Toast.makeText(this@Clientes, "Sesion expirada", Toast.LENGTH_LONG)
                        .show()
                } else {
                    Toast.makeText(
                        this@Clientes,
                        "Fallo al traer el item",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
    }

}