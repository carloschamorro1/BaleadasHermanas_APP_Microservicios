package hn.edu.ujcv.baleadashermanas.Service

import hn.edu.ujcv.baleadashermanas.DataCollection.ClienteDataCollectionItem
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ClienteService {

    @GET("cliente")
    fun listClientes(): Call<List<ClienteDataCollectionItem>>
    @GET("cliente/id/{id}")
    fun getClienteById(@Path("id") id: Long): Call<ClienteDataCollectionItem>

    @GET("cliente/dni/{dni}")
    fun getClienteByDNI(@Path("dni") dni: String): Call<ClienteDataCollectionItem>

    @Headers("Content-Type: application/json")
    @POST("cliente/addcliente")
    fun addCliente(@Body clienteData: ClienteDataCollectionItem): Call<ClienteDataCollectionItem>


    @Headers("Content-Type: application/json")
    @PUT("cliente")
    fun updateCliente(@Body clienteData: ClienteDataCollectionItem): Call<ClienteDataCollectionItem>

    @DELETE("cliente/delete/{id}")
    fun deleteCliente(@Path("id") id: Long): Call<ResponseBody>
}