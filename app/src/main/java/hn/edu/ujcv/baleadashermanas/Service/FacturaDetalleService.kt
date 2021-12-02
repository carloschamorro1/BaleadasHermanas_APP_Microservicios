package hn.edu.ujcv.baleadashermanas.Service

import hn.edu.ujcv.baleadashermanas.DataCollection.FacturaDetalleCollectionItem
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface FacturaDetalleService {

    @GET("facturadetalle")
    fun listDetalles(): Call<List<FacturaDetalleCollectionItem>>

    @GET("facturadetalle/allDetalles/{id}")
    fun getAllDetalles(@Path("id") idFactura: Long): Call<FacturaDetalleCollectionItem>

    @GET("facturadetalle/id/{id}")
    fun getDetalleById(@Path("id") id: Long): Call<FacturaDetalleCollectionItem>

    @Headers("Content-Type: application/json")
    @POST("facturadetalle/addfacturadetalle")
    fun addDetalle(@Body facturaData: FacturaDetalleCollectionItem): Call<FacturaDetalleCollectionItem>


    @Headers("Content-Type: application/json")
    @PUT("facturadetalle")
    fun updateDetalle(@Body facturaData: FacturaDetalleCollectionItem): Call<FacturaDetalleCollectionItem>

    @DELETE("facturadetalle/delete/{id}")
    fun deleteDetalle(@Path("id") id: Long): Call<ResponseBody>


    @DELETE("facturadetalle/deleteAll/{id}")
    fun deleteAllDetalles(@Path("id") idFactura: Long): Call<ResponseBody>
}