package hn.edu.ujcv.baleadashermanas.Service

import hn.edu.ujcv.baleadashermanas.DataCollection.InventarioDataCollectionItem
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface FacturaEncabezadoService {

    @GET("facturaencabezado")
    fun listFacturas(): Call<List<InventarioDataCollectionItem>>
    @GET("facturaencabezado/id/{id}")
    fun getFacturaById(@Path("id") id: Long): Call<InventarioDataCollectionItem>

    @Headers("Content-Type: application/json")
    @POST("facturaencabezado/addfacturaencabezado")
    fun addFactura(@Body facturaData: InventarioDataCollectionItem): Call<InventarioDataCollectionItem>


    @Headers("Content-Type: application/json")
    @PUT("facturaencabezado")
    fun updateFactura(@Body facturaData: InventarioDataCollectionItem): Call<InventarioDataCollectionItem>

    @DELETE("facturaencabezado/delete/{id}")
    fun deleteFactura(@Path("id") id: Long): Call<ResponseBody>
}