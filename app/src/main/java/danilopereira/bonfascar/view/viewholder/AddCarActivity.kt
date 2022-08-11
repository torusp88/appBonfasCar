package danilopereira.bonfascar.view.viewholder

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
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

        //Cria uma constante de SharedPreferences para pegar os dados passados pela Activity anterior
        val sP = getSharedPreferences("addCar", Context.MODE_PRIVATE)
        val editor = sP.edit()
        //Cria as variaves de data do serviço, telefone e o boolean para controlar o POST ou PUT
        var washDate = sP.getInt("washDate", 0)
        var carTelephone = sP.getString("carTelephone", "")
        var isCarNew = sP.getBoolean("isCarNew", false)

        //Insere os dados passados pela Activity anterior nos respectivos lugares
        binding.txtPlateAC.setText(sP.getString("carPlate", ""))
        binding.edtNameAC.setText(sP.getString("carName", ""))
        binding.edtOwnerAC.setText(sP.getString("carOwner", ""))
        if(carTelephone != "null") {
            binding.edtTelephoneAC.setText(carTelephone)
        }
        binding.edtAdressAC.setText(sP.getString("carAddress", ""))

        //Define o onClickListener do botão Cancelar
        binding.btnCancelAC.setOnClickListener {
            //Cria a intent para voltar ao MainActivity
            val intent = Intent(this@AddCarActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        //Define o onClickListener do botão Próximo
        binding.btnNextAC.setOnClickListener {
            /*Cria um novo objeto de carro a ser adicionado ao Banco de dados
            e adiciona seus dados a partir dos itens do layout da Activity
             */
            var newCar = Car()
            newCar.plate = binding.txtPlateAC.text.toString()
            newCar.name = binding.edtNameAC.text.toString()
            newCar.owner = binding.edtOwnerAC.text.toString()
            if(binding.edtTelephoneAC.text.toString() != "") {
                newCar.telephone = binding.edtTelephoneAC.text.toString().toInt()
            }
            newCar.address = binding.edtAdressAC.text.toString()

            //Cria um novo serviço de CarService para adicionar esse carro ao DB
            val addCar = RetrofitClient.createService(CarService::class.java)
            //Se a placa ainda não existir no Banco de dados e adiciona um carro novo a este Banco de dados utilizando o método POST
            if(isCarNew == true){
                val addNewCar: Call<Car> = addCar.addNewCar(newCar)
                //Cria uma chamada a API para criar um novo carro no Banco de Dados
                addNewCar.enqueue(object: Callback<Car>{
                    //Caso a chamada tenha uma resposta
                    override fun onResponse(call: Call<Car>, response: Response<Car>) {
                        editor.apply {
                            //Adiciona os dados ao sharedPreferences a serem enviados a proxima Activity
                            putInt("washDate", washDate)
                            putString("carPlate", newCar.plate)
                            putString("carName", newCar.name)
                            putString("carOwner", newCar.owner)
                            putString("carTelephone", newCar.telephone.toString())
                            putString("carAddress", newCar.address)
                            apply() //assync commit() = sync
                        }
                        //Cria a intent para seguir a AddWashActivity
                        val intent = Intent(this@AddCarActivity, AddWashActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    //Caso a chamada falhe
                    override fun onFailure(call: Call<Car>, t: Throwable) {
                        Toast.makeText(this@AddCarActivity, "Falha na conexão com o banco de dados, verifique a sua conexão a Internet", Toast.LENGTH_LONG).show()
                        val intent = Intent(this@AddCarActivity, AddWashActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                })
            } else { // Atualiza os dados do carro utilizando o método PUT
                val updateCar: Call<Car> = addCar.updateCarService(newCar.plate, newCar)
                //Cria uma chamada a API para atualizar os dados de um carro no Banco de Dados
                updateCar.enqueue(object: Callback<Car>{
                    //Caso a chamada tenha sucesso
                    override fun onResponse(call: Call<Car>, response: Response<Car>) {
                        editor.apply {
                            //Adiciona os dados ao sharedPreferences a serem enviados a proxima Activity
                            putInt("washDate", washDate)
                            putString("carPlate", newCar.plate)
                            putString("carName", newCar.name)
                            putString("carOwner", newCar.owner)
                            putString("carTelephone", newCar.telephone.toString())
                            putString("carAddress", newCar.address)
                            apply() //assync commit() = sync
                        }
                        //Cria a intent para seguir a AddWashActivity
                        val intent = Intent(this@AddCarActivity, AddWashActivity::class.java)
                        startActivity(intent)
                    }
                    //Caso a chamada falhe
                    override fun onFailure(call: Call<Car>, t: Throwable) {
                        Toast.makeText(this@AddCarActivity, "Falha na conexão com o banco de dados, verifique a sua conexão a Internet", Toast.LENGTH_LONG).show()
                        val intent = Intent(this@AddCarActivity, AddWashActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                })
            }

        }
    }
}