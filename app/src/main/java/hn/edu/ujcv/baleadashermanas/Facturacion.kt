package hn.edu.ujcv.baleadashermanas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import hn.edu.ujcv.baleadashermanas.DataCollection.ClienteDataCollectionItem
import hn.edu.ujcv.baleadashermanas.Service.ClienteService
import hn.edu.ujcv.baleadashermanas.Service.RestEngine
import kotlinx.android.synthetic.main.activity_facturacion.*
import kotlinx.android.synthetic.main.activity_inventario.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class Facturacion : AppCompatActivity() {
    var nombreUsuario = ""
    var clientes = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_facturacion)
        inicializar()
        llenarSpinnerCliente()
        llenarSpinnerMetodoPago()
        btn_menuPrincipalFacturacion.setOnClickListener {
            val intent = Intent(this, Principal::class.java)
            intent.putExtra("nombreUsuario", nombreUsuario)
            startActivity(intent)
        }
    }

    private fun llenarSpinnerCliente() {
        val personService:ClienteService = RestEngine.buildService().create(ClienteService::class.java)
        var result: Call<List<ClienteDataCollectionItem>> = personService.listClientes()

        result.enqueue(object : Callback<List<ClienteDataCollectionItem>> {
            override fun onFailure(call: Call<List<ClienteDataCollectionItem>>, t: Throwable) {
                Toast.makeText(this@Facturacion,"Error", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<List<ClienteDataCollectionItem>>,
                response: Response<List<ClienteDataCollectionItem>>
            ) {
                for(i in 0..(response.body()!!.size-1)){
                    clientes.add(response.body()!!.get(i).idcliente.toString() + "|" + response.body()!!.get(i).primer_nombre_cliente + " "+ response.body()!!.get(i).primer_apellido_cliente)
                    val arrayAdapter: ArrayAdapter<*>
                    arrayAdapter = ArrayAdapter(this@Facturacion,android.R.layout.simple_list_item_1,clientes)
                    spi_nombreCliente.adapter = arrayAdapter
                }

            }
        })
    }

    private fun llenarSpinnerMetodoPago(){
        val arrayAdapter: ArrayAdapter<*>
        var metodoPago = ArrayList<String>()
        metodoPago.add("Seleccione el método de pago")
        metodoPago.add("Efectivo")
        metodoPago.add("Tarjeta")
        arrayAdapter = ArrayAdapter(this@Facturacion,android.R.layout.simple_list_item_1,metodoPago)
        spi_metodoPago.adapter = arrayAdapter
    }



    private fun inicializar(){
        var intent = intent
        nombreUsuario = intent.getSerializableExtra("nombreUsuario") as String
        txv_labelNombreUsuarioFacturacion.setText(nombreUsuario)
    }



}