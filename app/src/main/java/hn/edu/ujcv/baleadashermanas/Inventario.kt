package hn.edu.ujcv.baleadashermanas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_inventario.*
import java.util.ArrayList

class Inventario : AppCompatActivity() {
    var tipoMovimiento = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventario)
        llenarSpinner()

    }

    private fun llenarSpinner(){
        val arrayAdapter: ArrayAdapter<*>
        tipoMovimiento.add("Seleccione el tipo de movimiento")
        tipoMovimiento.add("Ingreso")
        tipoMovimiento.add("Retiro")
        arrayAdapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,tipoMovimiento)
        spi_tipoMovimiento.adapter = arrayAdapter
    }
}