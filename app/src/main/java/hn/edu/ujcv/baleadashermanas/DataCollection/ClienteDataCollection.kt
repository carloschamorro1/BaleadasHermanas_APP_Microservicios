package hn.edu.ujcv.baleadashermanas.DataCollection

class ClienteDataCollection : ArrayList<ClienteDataCollectionItem>()

data class ClienteDataCollectionItem(
    val idcliente: Long?,
    val primer_nombre_cliente:String,
    val segundo_nombre_cliente:String,
    val primer_apellido_cliente:String,
    val segundo_apellido_cliente: String,
    val telefono_cliente:String,
    val email_cliente:String,
    val dnicliente:String,
    val rtncliente:String
)