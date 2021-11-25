package hn.edu.ujcv.baleadashermanas.DataCollection

class EmpleadosDataCollection : ArrayList<EmpleadosDataCollectionItem>()

data class EmpleadosDataCollectionItem(
    val idempleado: Long?,
    val primer_nombre_empleado:String,
    val segundo_nombre_empleado:String,
    val primer_apellido_empleado:String,
    val segundo_apellido_empleado: String,
    val telefono_empleado:String,
    val email_empleado:String,
    val dniempleado:String,
    val usuario:String,
    val contraseña:String
)