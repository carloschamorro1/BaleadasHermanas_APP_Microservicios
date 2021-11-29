package hn.edu.ujcv.baleadashermanas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_clientes.*
import kotlinx.android.synthetic.main.activity_empleados.*

class Clientes : AppCompatActivity() {
    var nombreUsuario: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clientes)
        inicializar()
        btn_menuPrincipalClientes.setOnClickListener {
            val intent = Intent(this, Principal::class.java)
            intent.putExtra("nombreUsuario", nombreUsuario)
            startActivity(intent)
        }
    }

    private fun inicializar(){
        var intent = intent
        nombreUsuario = intent.getSerializableExtra("nombreUsuario") as String
        txv_labelNombreUsuarioClientes.setText(nombreUsuario)
    }

}