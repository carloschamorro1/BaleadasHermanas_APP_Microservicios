package hn.edu.ujcv.baleadashermanas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_principal.*

class Principal : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)
        btn_empleados.setOnClickListener {
            val intent = Intent(this, Empleados::class.java)
            startActivity(intent)
        }

        btn_clientes.setOnClickListener {
            val intent = Intent(this, Clientes::class.java)
            startActivity(intent)
        }


        imv_cerrarSesion.setOnClickListener {
            val intent = Intent(this, Acceso::class.java)
            startActivity(intent)
        }
    }




}