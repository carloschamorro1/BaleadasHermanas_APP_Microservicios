package hn.edu.ujcv.baleadashermanas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.gson.Gson
import hn.edu.ujcv.baleadashermanas.DataCollection.EmpleadosDataCollectionItem
import hn.edu.ujcv.baleadashermanas.DataCollection.RestApiError
import hn.edu.ujcv.baleadashermanas.Service.EmpleadoService
import hn.edu.ujcv.baleadashermanas.Service.RestEngine
import kotlinx.android.synthetic.main.activity_empleados.*
import kotlinx.android.synthetic.main.activity_principal.*
import okhttp3.ResponseBody
import org.apache.commons.codec.digest.DigestUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Empleados : AppCompatActivity() {
    var nombreUsuario: String = ""
    var idEmpleado = ""
    var dniEmpleado = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_empleados)
        inicializar()
        btn_menuPrincipalEmpleados.setOnClickListener {
            val intent = Intent(this, Principal::class.java)
            intent.putExtra("nombreUsuario", nombreUsuario)
            startActivity(intent)
        }

        btn_GuardarEmpleado.setOnClickListener {
            v -> callServicePostEmpleado()
        }

        btn_buscarEmpleado.setOnClickListener {
            v -> buscarEmpleado()
        }

        btn_ActualizarEmpleado.setOnClickListener {
            callServicePutCasoEmpleado()
        }

        btn_BorrarEmpleado.setOnClickListener {
            callServiceDeleteCasoEmpleado()
        }


    }

    private fun inicializar(){
        var intent = intent
        nombreUsuario = intent.getSerializableExtra("nombreUsuario") as String
        txv_labelNombreUsuarioEmpleados.setText(nombreUsuario)
    }

    private fun accionesBuscar(){
        btn_GuardarEmpleado.isEnabled = false
        btn_ActualizarEmpleado.isEnabled = true
        btn_BorrarEmpleado.isEnabled = true
    }

    private fun callServicePostEmpleado() {
        if(estaVacio()){
            return
        }
        if(noLoSuficienteLargo()){
            return
        }

        val contraseñaEncriptada = DigestUtils.md5Hex(txt_contraseñaEmpleado.text.toString())

        val empleadoInfo = EmpleadosDataCollectionItem(
            idempleado = 0, // Este se pone asi porque es automatico
            primer_nombre_empleado = txt_primerNombreEmpleado.text.toString(),
            segundo_nombre_empleado = txt_segundoNombreEmpleado.text.toString(),
            primer_apellido_empleado = txt_primerApellidoEmpleado.text.toString(),
            segundo_apellido_empleado = txt_segundoApellidoEmpleado.text.toString(),
            telefono_empleado = txt_telefonoEmpleado.text.toString(),
            email_empleado= txt_emailEmpleado.text.toString(),
            dniempleado= txt_dniEmpledo.text.toString(),
            usuario= txt_usuarioEmpleado.text.toString() ,
            contraseña = contraseñaEncriptada
            )
        addCasoEmpleado(empleadoInfo) {
            if (it?.idempleado != null) {
                Toast.makeText(this,"Empleado añadido exitosamente", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this,"Error", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun addCasoEmpleado(empleadoData: EmpleadosDataCollectionItem, onResult: (EmpleadosDataCollectionItem?) -> Unit){
        if(estaVacio()){
            return
        }
        if(noLoSuficienteLargo()){
            return
        }
        val retrofit = RestEngine.buildService().create(EmpleadoService::class.java)
        var result: Call<EmpleadosDataCollectionItem> = retrofit.addEmpleado(empleadoData)
        result.enqueue(object : Callback<EmpleadosDataCollectionItem> {
            override fun onFailure(call: Call<EmpleadosDataCollectionItem>, t: Throwable) {
                onResult(null)
            }

            override fun onResponse(call: Call<EmpleadosDataCollectionItem>,
                                    response: Response<EmpleadosDataCollectionItem>
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


                    Toast.makeText(this@Empleados,errorResponse.errorDetails, Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this@Empleados,"Fallo al traer el item", Toast.LENGTH_LONG).show()
                }
            }

        }
        )
    }

    private fun accionesPorDefecto(){
        btn_GuardarEmpleado.isEnabled = true
        btn_ActualizarEmpleado.isEnabled = false
        btn_BorrarEmpleado.isEnabled = false
        btn_buscarEmpleado.isEnabled = true
    }

    private fun limpiar(){
        txt_primerNombreEmpleado.setText("")
        txt_segundoNombreEmpleado.setText("")
        txt_primerApellidoEmpleado.setText("")
        txt_segundoApellidoEmpleado.setText("")
        txt_telefonoEmpleado.setText("")
        txt_emailEmpleado.setText("")
        txt_dniEmpledo.setText("")
        txt_usuarioEmpleado.setText("")
        txt_contraseñaEmpleado.setText("")

    }

    private fun estaVacio():Boolean{
        if(txt_primerNombreEmpleado.text.isEmpty()) {
            txt_primerNombreEmpleado.error = "Debe rellenar el primer nombre"
            return true

        }else if(txt_segundoNombreEmpleado.text.toString().isEmpty()){
            txt_segundoNombreEmpleado.error = "Debe rellenar el segundo nombre"
            return true
        }
        else if(txt_primerApellidoEmpleado.text.toString().isEmpty()) {
            txt_primerApellidoEmpleado.error ="Debe rellenar el primer apellido"
            return true
        }
        else if(txt_segundoApellidoEmpleado.text.toString().isEmpty()) {
            txt_segundoApellidoEmpleado.error ="Debe rellenar el segundo apellido"
            return true
        }
        else if(txt_telefonoEmpleado.text.toString().isEmpty()) {
            txt_telefonoEmpleado.error ="Debe rellenar el teléfono"
            return true
        }
        else if(txt_emailEmpleado.text.toString().isEmpty()) {
            txt_emailEmpleado.error ="Debe rellenar el E-Mail"
            return true
        }
        else if(txt_dniEmpledo.text.toString().isEmpty()) {
            txt_dniEmpledo.error ="Debe rellenar el DNI"
            return true
        }
        else if(txt_usuarioEmpleado.text.toString().isEmpty()) {
            txt_usuarioEmpleado.error ="Debe rellenar el usuario"
            return true
        }
        else if(txt_contraseñaEmpleado.text.toString().isEmpty()) {
            txt_contraseñaEmpleado.error ="Debe rellenar la contraseña"
            return true
        }

        return false
    }



    private fun noLoSuficienteLargo():Boolean{
        if(txt_telefonoEmpleado.text.toString().length != 8) {
            txt_telefonoEmpleado.error ="El número de teléfono no puede ser distino a 8 dígitos"
            return true
        }
        if(txt_dniEmpledo.text.toString().length != 13){
            txt_dniEmpledo.error = "El DNI no puede ser distino a 13 dígitos"
            return true
        }
        if(txt_contraseñaEmpleado.text.toString().length < 8 ) {
            txt_contraseñaEmpleado.error ="La contraseña no puede ser menor a 8 caracteres"
            return true
        }

        return false
    }

    private fun callServicePutCasoEmpleado() {

        var contraseñaEncriptada = ""
        if(txt_contraseñaEmpleado.text.toString().equals("")){
            contraseñaEncriptada = ""
        }else{
            contraseñaEncriptada  = DigestUtils.md5Hex(txt_contraseñaEmpleado.text.toString())
        }

        val casoempleadoInfo = EmpleadosDataCollectionItem(
            idempleado = idEmpleado.toLong(), // Este se pone asi porque es automatico
            primer_nombre_empleado = txt_primerNombreEmpleado.text.toString(),
            segundo_nombre_empleado = txt_segundoNombreEmpleado.text.toString(),
            primer_apellido_empleado = txt_primerApellidoEmpleado.text.toString(),
            segundo_apellido_empleado = txt_segundoApellidoEmpleado.text.toString(),
            telefono_empleado = txt_telefonoEmpleado.text.toString(),
            email_empleado= txt_emailEmpleado.text.toString(),
            dniempleado= txt_dniEmpledo.text.toString(),
            usuario= txt_usuarioEmpleado.text.toString() ,
            contraseña = contraseñaEncriptada
        )

        val retrofit = RestEngine.buildService().create(EmpleadoService::class.java)
        var result: Call<EmpleadosDataCollectionItem> = retrofit.updateEmpleado(casoempleadoInfo)
        result.enqueue(object : Callback<EmpleadosDataCollectionItem> {
            override fun onFailure(call: Call<EmpleadosDataCollectionItem>, t: Throwable) {
                Toast.makeText(this@Empleados,"Error", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<EmpleadosDataCollectionItem>,
                                    response: Response<EmpleadosDataCollectionItem>
            ) {
                if (response.isSuccessful) {
                    val updatedPerson = response.body()!!
                    Toast.makeText(this@Empleados,"Actualizado correctamente", Toast.LENGTH_LONG).show()
                    accionesPorDefecto()
                    limpiar()
                }
                else if (response.code() == 401){
                    Toast.makeText(this@Empleados,"Sesion expirada", Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this@Empleados,"Fallo al traer el item", Toast.LENGTH_LONG).show()
                }
            }

        })
    }


    fun buscarEmpleado(){
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle("Ingrese el DNI del empleado")
        builder.setMessage("Por favor ingrese el DNI del empleado a buscar, en caso de querer verificar nuevamente presione el boton \"Cancelar\"")
        val dialogLayout = inflater.inflate(R.layout.alert_dialog_empleado, null)
        val editText  = dialogLayout.findViewById<EditText>(R.id.editText)
        builder.setView(dialogLayout)
        builder.setPositiveButton("Enviar") { dialogInterface, i ->
            if(editText.text.toString() == ""){
                Toast.makeText(this, "No puede dejar el DNI vacío", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }
            callServiceGetEmpleadobyDNI(editText.text.toString())
        }
        builder.setNegativeButton("Cancelar"){dialogInterface, i -> return@setNegativeButton}
        builder.show()
    }

    private fun callServiceGetEmpleadobyDNI(dni: String){
        val empleadoService: EmpleadoService = RestEngine.buildService().create(EmpleadoService::class.java)
        var result: Call<EmpleadosDataCollectionItem> = empleadoService.getEmpleadoByDNI(dni)

        result.enqueue(object :  Callback<EmpleadosDataCollectionItem> {
            override fun onFailure(call: Call<EmpleadosDataCollectionItem>, t: Throwable) {
                Toast.makeText(this@Empleados,"Error",Toast.LENGTH_LONG).show()
            }
            override fun onResponse(
                call: Call<EmpleadosDataCollectionItem>,
                response: Response<EmpleadosDataCollectionItem>
            ) {

                if(response!!.body() == null){
                    Toast.makeText(this@Empleados, "El empleado no existe", Toast.LENGTH_LONG).show()
                    return
                }

                txt_primerNombreEmpleado.setText(response.body()?.primer_nombre_empleado)
                txt_segundoNombreEmpleado.setText(response.body()?.segundo_nombre_empleado)
                txt_primerApellidoEmpleado.setText(response.body()?.primer_apellido_empleado)
                txt_segundoApellidoEmpleado.setText(response.body()?.segundo_apellido_empleado)
                txt_telefonoEmpleado.setText(response.body()?.telefono_empleado)
                txt_emailEmpleado.setText(response.body()?.email_empleado)
                txt_dniEmpledo.setText(response.body()?.dniempleado)
                txt_usuarioEmpleado.setText(response.body()?.usuario)
                idEmpleado = response.body()?.idempleado.toString()
                dniEmpleado = response.body()?.dniempleado.toString()
                accionesBuscar()
                Toast.makeText(this@Empleados,"Empleado recuperado exitosamente",Toast.LENGTH_LONG).show()
            }

        }
        )
    }


    //
    private fun callServiceDeleteCasoEmpleado() {
        val casoempleadoService: EmpleadoService =
            RestEngine.buildService().create(EmpleadoService::class.java)
        var result: Call<ResponseBody> = casoempleadoService.deleteEmpleado(idEmpleado.toLong())

        result.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@Empleados, "Error", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(this@Empleados, "Eliminado correctamente", Toast.LENGTH_LONG).show()
                    accionesPorDefecto()
                    limpiar()
                } else if (response.code() == 401) {
                    Toast.makeText(this@Empleados, "Sesion expirada", Toast.LENGTH_LONG)
                        .show()
                } else {
                    Toast.makeText(
                        this@Empleados,
                        "Fallo al traer el item",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
    }

}