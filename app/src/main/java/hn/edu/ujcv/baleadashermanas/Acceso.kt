package hn.edu.ujcv.baleadashermanas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import hn.edu.ujcv.baleadashermanas.DataCollection.EmpleadosDataCollectionItem
import hn.edu.ujcv.baleadashermanas.Service.EmpleadoService
import hn.edu.ujcv.baleadashermanas.Service.RestEngine
import kotlinx.android.synthetic.main.activity_acceso.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import org.apache.commons.codec.digest.DigestUtils

class Acceso : AppCompatActivity() {
    var loginSatisfactorio = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acceso)
        btn_ingresar.setOnClickListener {
           //v -> callServiceGetCredenciales()
            val intent = Intent(this@Acceso, Principal::class.java)
            startActivity(intent)
        }
    }


    private fun callServiceGetCredenciales() {
        val personService: EmpleadoService =
            RestEngine.buildService().create(EmpleadoService::class.java)
            val usuario = txt_usuario.text.toString()
            val contraseña = txt_contraseña2.text.toString()
            val contraseñaEncriptada = DigestUtils.md5Hex(contraseña)
            val result: Call<EmpleadosDataCollectionItem> =
                personService.loginEmpleado(usuario, contraseñaEncriptada)

            result.enqueue(object : Callback<EmpleadosDataCollectionItem> {
                override fun onResponse(
                    call: Call<EmpleadosDataCollectionItem>,
                    response: Response<EmpleadosDataCollectionItem>
                ) {
                    loginSatisfactorio = false
                    Toast.makeText(this@Acceso, "bueno", Toast.LENGTH_LONG).show()
                }

                override fun onFailure(call: Call<EmpleadosDataCollectionItem>, t: Throwable) {
                    /*loginSatisfactorio = true
                    Toast.makeText(this@Acceso, "Inicio", Toast.LENGTH_LONG).show()
                    */
                    Toast.makeText(this@Acceso, "malo", Toast.LENGTH_LONG).show()
                }
            }
            )
    }


}