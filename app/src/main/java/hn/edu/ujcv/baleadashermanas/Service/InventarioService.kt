package hn.edu.ujcv.baleadashermanas.Service

import hn.edu.ujcv.baleadashermanas.DataCollection.InventarioDataCollectionItem
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface InventarioService {

    @GET("inventario")
    fun listProductos(): Call<List<InventarioDataCollectionItem>>
    @GET("inventario/id/{id}")
    fun getProductoById(@Path("id") id: Long): Call<InventarioDataCollectionItem>

    @GET("inventario/nombreproducto/{nombreproducto}")
    fun getProductobyNombre(@Path("nombreproducto") nombreProducto: String): Call<InventarioDataCollectionItem>

    @Headers("Content-Type: application/json")
    @POST("inventario/addproducto")
    fun addProducto(@Body inventarioData: InventarioDataCollectionItem): Call<InventarioDataCollectionItem>


    @Headers("Content-Type: application/json")
    @PUT("inventario")
    fun updateProducto(@Body inventarioData: InventarioDataCollectionItem): Call<InventarioDataCollectionItem>

    @DELETE("inventario/delete/{id}")
    fun deleteProducto(@Path("id") id: Long): Call<ResponseBody>
}