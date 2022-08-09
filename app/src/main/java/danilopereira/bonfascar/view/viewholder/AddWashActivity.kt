package danilopereira.bonfascar.view.viewholder

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import danilopereira.bonfascar.databinding.ActivityAddWashBinding
import danilopereira.bonfascar.entities.Car
import danilopereira.bonfascar.entities.Wash
import danilopereira.bonfascar.retrofit.RetrofitClient
import danilopereira.bonfascar.services.CarService
import danilopereira.bonfascar.services.WashService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddWashActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddWashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddWashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCancelAW.setOnClickListener {
            val intent = Intent(this@AddWashActivity, MainActivity::class.java)
            startActivity(intent)
        }

        val sharedPreference = getSharedPreferences("addCar", Context.MODE_PRIVATE)
        var washDate = sharedPreference.getInt("washDate", 0)
        val carPlate = sharedPreference.getString("carPlate", "")
        var carName = sharedPreference.getString("carName", "")
        var carOwner = sharedPreference.getString("carOwner", "")
        var carTelephone = sharedPreference.getString("carTelephone", "")
        var carAddress = sharedPreference.getString("carAddress", "")

        var newCar = Car()
        newCar.plate = carPlate.toString()
        newCar.name = carName.toString()
        newCar.owner = carOwner.toString()
        newCar.telephone = carTelephone.toString().toInt()
        newCar.address = carAddress.toString()

        binding.btnAddWashAW.setOnClickListener {
            var newWash = Wash()
            newWash.date = washDate
            newWash.car = newCar
            newWash.deliver = binding.swtDeliverAW.isChecked
            if(binding.edtPriceAW.text.toString() != "") {
                newWash.price = binding.edtPriceAW.text.toString().toInt()
            }
            newWash.wax = binding.swtWaxAW.isChecked
            newWash.payed = binding.swtPayedAW.isChecked
            newWash.obs = binding.edtObsAW.text.toString()

            val addWash = RetrofitClient.createService(WashService::class.java)
            val addNewWash: Call<Wash> = addWash.addWashService(newWash)

            addNewWash.enqueue(object: Callback<Wash>{
                override fun onResponse(call: Call<Wash>, response: Response<Wash>) {
                    val intent = Intent(this@AddWashActivity, MainActivity::class.java)
                    startActivity(intent)
                }

                override fun onFailure(call: Call<Wash>, t: Throwable) {
                    val s = ""
                }

            })

        }
    }
}