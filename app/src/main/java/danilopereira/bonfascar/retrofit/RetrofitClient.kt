package danilopereira.bonfascar.retrofit

import danilopereira.bonfascar.services.CarService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

//http://localhost:8080/cars - Retorna uma lista

class RetrofitClient {

    companion object{
        private lateinit var INSTANCE: Retrofit

        private const val BASE_URL = "http://192.168.15.11:8080/" // 192.168.15.11

        private fun getRetrofitInstance(): Retrofit {
            val http = OkHttpClient.Builder()
            if (!::INSTANCE.isInitialized) {
                INSTANCE = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(http.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                }
                return INSTANCE

        }

        fun <S> createService(c: Class<S>): S{
            return getRetrofitInstance().create(c)
        }
    }

}
/*
 fun createCarService() : CarService{
            return getRetrofitInstance().create(CarService::class.java)
        }
 */