package hn.edu.ujcv.baleadashermanas.DataCollection

class FacturaEncabezadoDataCollection : ArrayList<FacturaEncabezadoDataCollectionItem>()

data class FacturaEncabezadoDataCollectionItem(
    val idfactura:Long,
    val cai: String,
    val idempleado: String,
    val totalfactura: String,
    val idcliente: Long,
    val fecha_factura: String,
    val cambio_factura: String,
    val pago_factura: String
)