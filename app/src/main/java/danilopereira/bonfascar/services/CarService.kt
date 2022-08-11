package danilopereira.bonfascar.services

import danilopereira.bonfascar.entities.Car
import danilopereira.bonfascar.entities.Wash
import retrofit2.Call
import retrofit2.http.*


//http://localhost:8080/cars - Retorna uma lista

interface CarService {
    //Retorna uma lista de todos os carros cadastrados em /cars
    @GET("cars")
    fun list(): Call<List<Car>>

    //Retorna os dados de um carro a partir de sua placa
    @GET("cars/{plate}")
    fun getCar(@Path("plate") plate : String) : Call<Car>

    //Adiciona um novo carro
    @POST("cars")
    fun addNewCar(@Body car: Car): Call<Car>

    //Atualiza as informações de um carro
    @PUT("cars/{id}")
    fun updateCarService(@Path("id") id: String, @Body car: Car) : Call<Car>
}