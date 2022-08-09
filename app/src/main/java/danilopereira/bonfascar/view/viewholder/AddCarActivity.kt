package danilopereira.bonfascar.view.viewholder

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import danilopereira.bonfascar.databinding.ActivityAddCarBinding
import danilopereira.bonfascar.entities.Car
import danilopereira.bonfascar.retrofit.RetrofitClient
import danilopereira.bonfascar.services.CarService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddCarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddCarBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sP = getSharedPreferences("addCar", Context.MODE_PRIVATE)
        val editor = sP.edit()

        val sharedPreference = getSharedPreferences("addCar", Context.MODE_PRIVATE)
        var washDate = sharedPreference.getInt("washDate", 0)
        val carPlate = sharedPreference.getString("carPlate", "")
        var carName = sharedPreference.getString("carName", "")
        var carOwner = sharedPreference.getString("carOwner", "")
        var carTelephone = sharedPreference.getString("carTelephone", "")
        var carAddress = sharedPreference.getString("carAddress", "")
        var isCarNew = sharedPreference.getBoolean("isCarNew", false)

        binding.txtPlateAC.setText(carPlate)
        binding.edtNameAC.setText(carName)
        binding.edtOwnerAC.setText(carOwner)
        if(carTelephone != "null") {
            binding.edtTelephoneAC.setText(carTelephone)
        }
        binding.edtAdressAC.setText(carAddress)

        binding.btnCancelAC.setOnClickListener {
            val intent = Intent(this@AddCarActivity, MainActivity::class.java)
            startActivity(intent)
        }

        binding.btnNextAC.setOnClickListener {
            //Cria um novo objeto de carro a ser adicionado ao Banco de dados
            var newCar = Car()
            newCar.plate = carPlate.toString()
            newCar.name = binding.edtNameAC.text.toString()
            newCar.owner = binding.edtOwnerAC.text.toString()
            if(binding.edtTelephoneAC.text.toString() != "") {
                newCar.telephone = binding.edtTelephoneAC.text.toString().toInt()
            }
            newCar.address = binding.edtAdressAC.text.toString()

            val addCar = RetrofitClient.createService(CarService::class.java)
            //Se a placa ainda não existir no Banco de dados e adiciona um carro novo a este Banco de dados
            if(isCarNew == true){
                val addNewCar: Call<Car> = addCar.addNewCar(newCar)
                addNewCar.enqueue(object: Callback<Car>{
                    override fun onResponse(call: Call<Car>, response: Response<Car>) {
                        editor.apply {
                            putInt("washDate", washDate)
                            putString("carPlate", newCar.plate)
                            putString("carName", newCar.name)
                            putString("carOwner", newCar.owner)
                            putString("carTelephone", newCar.telephone.toString())
                            putString("carAddress", newCar.address)
                            apply() //assync commit() = sync
                        }
                        val intent = Intent(this@AddCarActivity, AddWashActivity::class.java)
                        startActivity(intent)
                    }

                    override fun onFailure(call: Call<Car>, t: Throwable) {
                        TODO("Not yet implemented")
                    }

                })
            } else {
                val updateCar: Call<Car> = addCar.updateCarService(newCar.plate, newCar)
                updateCar.enqueue(object: Callback<Car>{
                    override fun onResponse(call: Call<Car>, response: Response<Car>) {
                        editor.apply {
                            putInt("washDate", washDate)
                            putString("carPlate", newCar.plate)
                            putString("carName", newCar.name)
                            putString("carOwner", newCar.owner)
                            putString("carTelephone", newCar.telephone.toString())
                            putString("carAddress", newCar.address)
                            apply() //assync commit() = sync
                        }
                        val intent = Intent(this@AddCarActivity, AddWashActivity::class.java)
                        startActivity(intent)
                    }

                    override fun onFailure(call: Call<Car>, t: Throwable) {
                        TODO("Not yet implemented")
                    }

                })
            }

        }
    }

    override fun onResume() {
        super.onResume()
    }
}