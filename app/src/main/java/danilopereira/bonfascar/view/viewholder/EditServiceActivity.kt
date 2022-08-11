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

        //Cria uma constante de SharedPreferences para pegar os dados passados pela Activity anterior
        val sharedPreference = getSharedPreferences("washService", Context.MODE_PRIVATE)

        //Insere os dados passados pela Activity anterior nos respectivos lugares
        binding.txtPlateES.setText(sharedPreference.getString("carPlate", ""))
        binding.edtName.setText(sharedPreference.getString("carName", ""))
        binding.edtTelephone.setText(sharedPreference.getString("carTelephone", ""))
        binding.edtAdressES.setText(sharedPreference.getString("carAddress", ""))
        binding.edtObsES.setText(sharedPreference.getString("washObs", ""))
        binding.edtPriceES.setText(sharedPreference.getString("washPrice", ""))
        binding.edtOwnerES.setText(sharedPreference.getString("carOwner", ""))
        binding.switchWaxES.isChecked = sharedPreference.getBoolean("washWax", false)
        binding.switchDeliverES.isChecked = sharedPreference.getBoolean("washDeliver", false)
        binding.switchPayed.isChecked = sharedPreference.getBoolean("washPayed", false)


        //Define o onClickListener do botão Deletar
        binding.buttonDeleteES.setOnClickListener {
            //Cria uma chamada a API para deletar um serviço por meio do método DELETE
            val washService = RetrofitClient.createService(WashService::class.java)
            val requestCall: Call<Unit> = washService.deleteWashService(sharedPreference.getLong("washId", 0 ))

            //Faz o pop-up perguntando se tem certeza que quer excluir aquela lavagem
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Tem certeza que deseja deletar essa lavagem?")
                .setCancelable(false)
                .setPositiveButton("Sim") { dialog, id ->
                    //Caso a chamada tenha uma resposta
                    // Deleta a washService especifico e volta para a MainActivity
                    requestCall.enqueue(object: Callback<Unit>{
                        override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                            val intent = Intent(this@EditServiceActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        //Caso a chamada falhe
                        override fun onFailure(call: Call<Unit>, t: Throwable) {
                            Toast.makeText(this@EditServiceActivity, "Falha na conexão com o banco de dados, verifique a sua conexão a Internet", Toast.LENGTH_LONG).show()
                            val intent = Intent(this@EditServiceActivity, AddWashActivity::class.java)
                            startActivity(intent)
                            finish()
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
        //Define o onClickListener do botão Atualizar
        binding.buttonRefreshES.setOnClickListener {
            //Cria um objeto de Wash a ser atualizado do Banco de Dados
            val newWash = Wash()
            newWash.id = sharedPreference.getLong("washId", 0 )
            newWash.date = sharedPreference.getInt("washDate", 0)
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

            //Cria uma nova chamada a API para atualizar um serviço por meio do método PUT
            val washService = RetrofitClient.createService(WashService::class.java)
            val requestCall: Call<Wash> = washService.updateWashService(newWash.id, newWash)
            requestCall.enqueue(object : Callback<Wash>{
                //Caso a chamada tenha uma resposta
                override fun onResponse(call: Call<Wash>, response: Response<Wash>) {
                    //Cria a intent para voltar ao MainActivity
                    val intent = Intent(this@EditServiceActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                //Caso a chamada falhe
                override fun onFailure(call: Call<Wash>, t: Throwable) {
                    Toast.makeText(this@EditServiceActivity, "Falha na conexão com o banco de dados, verifique a sua conexão a Internet", Toast.LENGTH_LONG).show()
                    val intent = Intent(this@EditServiceActivity, AddWashActivity::class.java)
                    startActivity(intent)
                    finish()
                }

            })
        }
    }
}

