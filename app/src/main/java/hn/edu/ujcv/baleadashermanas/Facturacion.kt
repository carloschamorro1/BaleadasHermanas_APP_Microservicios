package hn.edu.ujcv.baleadashermanas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_facturacion.*
import kotlinx.android.synthetic.main.activity_inventario.*

class Facturacion : AppCompatActivity() {
    var nombreUsuario = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_facturacion)
        inicializar()
        btn_menuPrincipalFacturacion.setOnClickListener {
            val intent = Intent(this, Principal::class.java)
            intent.putExtra("nombreUsuario", nombreUsuario)
            startActivity(intent)
        }
    }

    private fun inicializar(){
        var intent = intent
        nombreUsuario = intent.getSerializableExtra("nombreUsuario") as String
        txv_labelNombreUsuarioFacturacion.setText(nombreUsuario)
    }

}