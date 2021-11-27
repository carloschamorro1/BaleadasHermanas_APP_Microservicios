package hn.edu.ujcv.baleadashermanas

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_principal.*

class Principal : AppCompatActivity() {
    var nombreUsuario: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)
        inicializar()
        btn_empleados.setOnClickListener {
            val intent = Intent(this, Empleados::class.java)
            startActivity(intent)
        }

        btn_clientes.setOnClickListener {
            val intent = Intent(this, Clientes::class.java)
            startActivity(intent)
        }

        btn_inventario.setOnClickListener {
            val intent = Intent(this, Inventario::class.java)
            startActivity(intent)
        }

        btn_facturacion.setOnClickListener {
            val intent = Intent(this, Facturacion::class.java)
            startActivity(intent)
        }

        imv_cerrarSesion.setOnClickListener {
            cerrarSesion()
        }
    }

    private fun inicializar(){
        var intent = intent
        nombreUsuario = intent.getSerializableExtra("nombreUsuario") as String
        txv_labelNombreUsuarioPrincipal.setText(nombreUsuario)
    }

    private fun cerrarSesion(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("¿Desea cerrar sesión?")
        builder.setMessage("¿Está seguro que desea cerrar sesión?")
        builder.setPositiveButton("Sí") { dialogInterface: DialogInterface, i: Int ->
            Toast.makeText(this,"Gracias por utilizar el programa",Toast.LENGTH_SHORT).show()
            val intent = Intent(this, Acceso::class.java)
            startActivity(intent)
        }
        builder.setNegativeButton("Cancelar") { dialogInterface: DialogInterface, i: Int ->
            return@setNegativeButton
        }
        builder.show()
    }

}