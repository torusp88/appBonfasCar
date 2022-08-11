package danilopereira.bonfascar.services

import danilopereira.bonfascar.entities.Wash
import retrofit2.Call
import retrofit2.http.*

interface WashService {
    //Retorna uma lista de washservices em /services
    @GET("services")
    fun list(): Call<List<Wash>>

    //Retorna uma lista de washServices a partir de uma data recebida
    @GET("services/{date}")
    fun listByDate(@Path("date") date: String): Call<List<Wash>>

    //função para deletar uma washService especifica a partir de um id
    @DELETE("services/{id}")
    fun deleteWashService(@Path("id") id: Long): Call<Unit>

    //Adiciona um washService
    @POST("services")
    fun addWashService(@Body wash: Wash): Call<Wash>

    //Atualiza um washService
    @PUT("services/{id}")
    fun updateWashService(@Path("id") id: Long, @Body wash: Wash) : Call<Wash>
}