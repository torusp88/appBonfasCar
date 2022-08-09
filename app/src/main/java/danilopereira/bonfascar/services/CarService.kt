package danilopereira.bonfascar.services

import danilopereira.bonfascar.entities.Car
import danilopereira.bonfascar.entities.Wash
import retrofit2.Call
import retrofit2.http.*


//http://localhost:8080/cars - Retorna uma lista

interface CarService {
    @GET("cars")
    fun list(): Call<List<Car>>

    @GET("cars/{plate}")
    fun getCar(@Path("plate") plate : String) : Call<Car>

    @POST("cars")
    fun addNewCar(@Body car: Car): Call<Car>

    @PUT("cars/{id}")
    fun updateCarService(@Path("id") id: String, @Body car: Car) : Call<Car>
}