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
    var nombreUsuario: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acceso)
        btn_ingresar.setOnClickListener {
           v -> callServiceGetCredenciales()
            /*val intent = Intent(this@Acceso, Principal::class.java)
            nombreUsuario = "juan.asdasdasda"
            intent.putExtra("nombreUsuario", nombreUsuario)
            startActivity(intent)*/
        }
    }


    private fun callServiceGetCredenciales() {
        val personService: EmpleadoService =
            RestEngine.buildService().create(EmpleadoService::class.java)
            val usuario = txt_usuario.text.toString()
            val contraseña = txt_contraseña2.text.toString()
            val contraseñaEncriptada = DigestUtils.md5Hex(contraseña)
            val result: Call<EmpleadosDataCollectionItem> =
                personService.login(usuario, contraseñaEncriptada)

            result.enqueue(object : Callback<EmpleadosDataCollectionItem> {
                override fun onResponse(
                    call: Call<EmpleadosDataCollectionItem>,
                    response: Response<EmpleadosDataCollectionItem>
                ) {
                    if(response!!.body() == null){
                        Toast.makeText(this@Acceso, "Contraseña o usuario incorrecto", Toast.LENGTH_LONG).show()
                    }
                    else{
                        nombreUsuario = response.body()!!.usuario
                        Toast.makeText(this@Acceso, "Bienvenido (a): ${nombreUsuario}", Toast.LENGTH_LONG).show()
                        val intent = Intent(this@Acceso,Principal::class.java)
                        intent.putExtra("nombreUsuario", nombreUsuario)
                        startActivity(intent)
                    }
                }

                override fun onFailure(call: Call<EmpleadosDataCollectionItem>, t: Throwable) {

                }
            }
            )
    }


}