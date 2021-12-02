package hn.edu.ujcv.baleadashermanas.DataCollection

class FacturaDetalleCollection : ArrayList<FacturaDetalleCollectionItem>()

data class FacturaDetalleCollectionItem(
    var iddetalle:Long,
    var idfactura: Long,
    var idproducto: Long,
    var cantidadfactura: String,
    var precio: String
)