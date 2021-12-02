package hn.edu.ujcv.baleadashermanas.Service

import hn.edu.ujcv.baleadashermanas.DataCollection.FacturaEncabezadoDataCollectionItem
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface FacturaEncabezadoService {

    @GET("facturaencabezado")
    fun listFacturas(): Call<List<FacturaEncabezadoDataCollectionItem>>
    @GET("facturaencabezado/id/{id}")
    fun getFacturaById(@Path("id") id: Long): Call<FacturaEncabezadoDataCollectionItem>

    @Headers("Content-Type: application/json")
    @POST("facturaencabezado/addfacturaencabezado")
    fun addFactura(@Body facturaData: FacturaEncabezadoDataCollectionItem): Call<FacturaEncabezadoDataCollectionItem>


    @Headers("Content-Type: application/json")
    @PUT("facturaencabezado")
    fun updateFactura(@Body facturaData: FacturaEncabezadoDataCollectionItem): Call<FacturaEncabezadoDataCollectionItem>

    @DELETE("facturaencabezado/delete/{id}")
    fun deleteFactura(@Path("id") id: Long): Call<ResponseBody>
}