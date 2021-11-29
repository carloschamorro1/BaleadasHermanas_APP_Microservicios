package hn.edu.ujcv.baleadashermanas.DataCollection

class InventarioDataCollection : ArrayList<InventarioDataCollectionItem>()

data class InventarioDataCollectionItem(
    val idproducto: Long,
    val nombreproducto: String,
    val idempleado: Long,
    val cantidadstock: String,
    val fechaintroduccion :String,
    val tipomovimiento : String,
    val precio :String
)