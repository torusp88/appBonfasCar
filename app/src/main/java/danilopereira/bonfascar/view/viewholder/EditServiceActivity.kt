package danilopereira.bonfascar.view.viewholder

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import danilopereira.bonfascar.databinding.ActivityEditServiceBinding
import danilopereira.bonfascar.entities.Wash
import danilopereira.bonfascar.retrofit.RetrofitClient
import danilopereira.bonfascar.services.WashService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditServiceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditServiceBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreference = getSharedPreferences("washService", Context.MODE_PRIVATE)
        val washId = sharedPreference.getLong("washId", 0 )
        var washDate = sharedPreference.getInt("washDate", 0)
        val carPlate = sharedPreference.getString("carPlate", "")
        val carName = sharedPreference.getString("carName", "")
        val carOwner = sharedPreference.getString("carOwner", "")
        val carTelephone = sharedPreference.getString("carTelephone", "")
        val carAddress = sharedPreference.getString("carAddress", "")
        val washDeliver = sharedPreference.getBoolean("washDeliver", false)
        val washPrice = sharedPreference.getString("washPrice", "")
        val washWax = sharedPreference.getBoolean("washWax", false)
        val washPayed = sharedPreference.getBoolean("washPayed", false)
        val washObs = sharedPreference.getString("washObs", "")

        binding.txtPlateES.setText(carPlate)
        binding.edtName.setText(carName)
        binding.edtTelephone.setText(carTelephone)
        binding.edtAdressES.setText(carAddress)
        binding.edtObsES.setText(washObs)
        binding.edtPriceES.setText(washPrice)
        binding.edtOwnerES.setText(carOwner)
        binding.switchWaxES.isChecked = washWax
        binding.switchDeliverES.isChecked = washDeliver
        binding.switchPayed.isChecked = washPayed

        binding.buttonDeleteES.setOnClickListener {
            val washService = RetrofitClient.createService(WashService::class.java)
            val requestCall: Call<Unit> = washService.deleteWashService(washId)

            //Fazer o pop-up perguntando se tem certeza que quer excluir aquela lavagem

            val builder = AlertDialog.Builder(this)
            builder.setMessage("Tem certeza que deseja deletar essa lavagem?")
                .setCancelable(false)
                .setPositiveButton("Sim") { dialog, id ->
                    // Deleta a washService especifico e volta para a MainActivity
                    requestCall.enqueue(object: Callback<Unit>{
                        override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                            val intent = Intent(this@EditServiceActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }

                        override fun onFailure(call: Call<Unit>, t: Throwable) {
                            val s = ""
                        }

                    })
                }
                .setNegativeButton("Não") { dialog, id ->
                    // Cancela a exclusão
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()


        }

        binding.buttonRefreshES.setOnClickListener {
            val newWash = Wash()
            newWash.id = washId
            newWash.date = washDate
            newWash.car.plate = binding.txtPlateES.text.toString()
            newWash.car.name = binding.edtName.text.toString()
            newWash.car.owner = binding.edtOwnerES.text.toString()
            newWash.car.telephone = binding.edtTelephone.text.toString().toInt()
            newWash.car.address = binding.edtAdressES.text.toString()
            newWash.deliver = binding.switchDeliverES.isChecked
            newWash.price = binding.edtPriceES.text.toString().toInt()
            newWash.wax = binding.switchWaxES.isChecked
            newWash.payed = binding.switchPayed.isChecked
            newWash.obs = binding.edtObsES.text.toString()

            val washService = RetrofitClient.createService(WashService::class.java)
            val requestCall: Call<Wash> = washService.updateWashService(washId, newWash)

            requestCall.enqueue(object : Callback<Wash>{
                override fun onResponse(call: Call<Wash>, response: Response<Wash>) {
                    val intent = Intent(this@EditServiceActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                override fun onFailure(call: Call<Wash>, t: Throwable) {
                    val s = ""
                }

            })
        }
    }
}

