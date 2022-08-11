package danilopereira.bonfascar.view.viewholder

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
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

        //Define o onClickListener do botão Cancelar
        binding.btnCancelAW.setOnClickListener {
            //Cria a intent para voltar ao MainActivity
            val intent = Intent(this@AddWashActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        //Cria uma constante de SharedPreferences para pegar os dados passados pela Activity anterior
        val sharedPreference = getSharedPreferences("addCar", Context.MODE_PRIVATE)

        //Cria um objeto de Car a ser adicionado ao Banco de dados
        var newCar = Car()
        newCar.plate = sharedPreference.getString("carPlate", "").toString()
        newCar.name = sharedPreference.getString("carName", "").toString()
        newCar.owner = sharedPreference.getString("carOwner", "").toString()
        newCar.telephone = sharedPreference.getString("carTelephone", "").toString().toInt()
        newCar.address = sharedPreference.getString("carAddress", "").toString()

        //Define o onClickListener do botão Adicionar
        binding.btnAddWashAW.setOnClickListener {
            //Cria um objeto de Wash a ser adicionado ao Banco de dados
            var newWash = Wash()
            newWash.date = sharedPreference.getInt("washDate", 0)
            newWash.car = newCar
            newWash.deliver = binding.swtDeliverAW.isChecked
            if(binding.edtPriceAW.text.toString() != "") {
                newWash.price = binding.edtPriceAW.text.toString().toInt()
            }
            newWash.wax = binding.swtWaxAW.isChecked
            newWash.payed = binding.swtPayedAW.isChecked
            newWash.obs = binding.edtObsAW.text.toString()

            //Cria um novo serviço de WashService para adicionar essa lavagem ao DB utilizando o método POST
            val addWash = RetrofitClient.createService(WashService::class.java)
            val addNewWash: Call<Wash> = addWash.addWashService(newWash)
            //Cria a chamada a API para adicionar um washService
            addNewWash.enqueue(object: Callback<Wash>{
                //Caso a chamada tenha uma resposta
                override fun onResponse(call: Call<Wash>, response: Response<Wash>) {
                    //Cria a intent para voltar ao MainActivity
                    val intent = Intent(this@AddWashActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                //Caso a chamada falhe
                override fun onFailure(call: Call<Wash>, t: Throwable) {
                    Toast.makeText(this@AddWashActivity, "Falha na conexão com o banco de dados, verifique a sua conexão a Internet", Toast.LENGTH_LONG).show()
                    val intent = Intent(this@AddWashActivity, AddWashActivity::class.java)
                    startActivity(intent)
                    finish()
                }

            })

        }
    }
}