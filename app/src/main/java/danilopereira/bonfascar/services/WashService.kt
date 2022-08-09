package danilopereira.bonfascar.services

import danilopereira.bonfascar.entities.Wash
import retrofit2.Call
import retrofit2.http.*

interface WashService {
    //Função para retornar as washservices em /services
    @GET("services")
    fun list(): Call<List<Wash>>

    @GET("services/{date}")
    fun listByDate(@Path("date") date: String): Call<List<Wash>>

    //função para deletar uma washService especifica
    @DELETE("services/{id}")
    fun deleteWashService(@Path("id") id: Long): Call<Unit>

    @POST("services")
    fun addWashService(@Body wash: Wash): Call<Wash>

    @PUT("services/{id}")
    fun updateWashService(@Path("id") id: Long, @Body wash: Wash) : Call<Wash>
}