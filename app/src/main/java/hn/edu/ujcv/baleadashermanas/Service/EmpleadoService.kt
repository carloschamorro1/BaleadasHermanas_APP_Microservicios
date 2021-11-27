package hn.edu.ujcv.baleadashermanas.Service

import hn.edu.ujcv.baleadashermanas.DataCollection.EmpleadosDataCollectionItem
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface EmpleadoService {

    @GET("empleado")
    fun listEmpleados(): Call<List<EmpleadosDataCollectionItem>>
    @GET("empleado/id/{id}")
    fun getEmpleadoById(@Path("id") id: Long): Call<EmpleadosDataCollectionItem>

    @GET("empleado/dni/{dni}")
    fun getEmpleadoByDNI(@Path("dni") dni: Long): Call<EmpleadosDataCollectionItem>

    @GET("empleado/login/{usuario}/{pass}")
    fun login(@Path("usuario")usuario: String, @Path("pass")pass: String): Call<EmpleadosDataCollectionItem>

    @Headers("Content-Type: application/json")
    @POST("empleado/addempleado")
    fun addEmpleado(@Body empleadoData: EmpleadosDataCollectionItem): Call<EmpleadosDataCollectionItem>



    @Headers("Content-Type: application/json")
    @PUT("empleado")
    fun updateEmpleado(@Body empleadoData: EmpleadosDataCollectionItem): Call<EmpleadosDataCollectionItem>
    @DELETE("empleado/delete/{id}")
    fun deleteEmpleado(@Path("id") id: Long): Call<ResponseBody>
}