package danilopereira.bonfascar.view.viewholder

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import danilopereira.bonfascar.R
import danilopereira.bonfascar.databinding.ActivityMainBinding
import danilopereira.bonfascar.entities.Car
import danilopereira.bonfascar.entities.Wash
import danilopereira.bonfascar.retrofit.RetrofitClient
import danilopereira.bonfascar.services.CarService
import danilopereira.bonfascar.services.WashService
import danilopereira.bonfascar.view.adapter.WashAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var washAdapter: WashAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Cria uma constante de SharedPreferences para adicionar dados a serem repassados a proxima
        //Activity ao adicionar um carro
        val sP = getSharedPreferences("addCar", Context.MODE_PRIVATE)
        val editor = sP.edit()

        //Cria a constante de Data para ser adicionado ao cabeçalho da MainActivity
        val sdf = SimpleDateFormat("dd/M/yyyy")
        val currentDate = sdf.format(Date())

        //cria a constante de Data para ser adicionada no banco de dados
        val sqlDate = SimpleDateFormat("ddMyyyy")
        val sqlCurrentDate = sqlDate.format(Date())

        //Define o texto do cabeçalho da MainActivity
        binding.textHeader.setText("São Paulo " + currentDate)

        /*
        *Cria o onClickListener do botão da MainActivity que adicionará um novo serviço a lista de
        * serviços
         */
        binding.imgAdd.setOnClickListener {
            val builder = AlertDialog.Builder(this) // cria uma caixa de dialogo
            val edtPlaca = EditText(this)// cria um EditText para a caixa de dialogo
            edtPlaca.setHint("Placa") // Põe uma dica para o EditText
            edtPlaca.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD //Muda o inputType do EditText, para que ele tenha letras e numeros no teclado
            builder.setMessage("Digite a placa do carro:")
                .setCancelable(false)
                .setView(edtPlaca)//adiciona o EditText na caixa de dialogo
                .setPositiveButton("Adicionar") { dialog, id ->
                    //Busca placa e adicionar dados no sharedPreferences
                    val carService = RetrofitClient.createService(CarService::class.java) // cria um novo serviço de CarService
                    val requestCar: Call<Car> = carService.getCar(edtPlaca.text.toString()) // Busca por um carro no DB, a partir do texto do EditText
                    //cria uma chamada a API para receber os dados referente a placa do carro
                    requestCar.enqueue(object : Callback<Car>{
                        //Caso a chamada obtenha uma resposta
                        override fun onResponse(call: Call<Car>, r: Response<Car>) {
                            var isCarNew: Boolean = false
                            if(r.body()?.name == null){ // checa se o body() esta vazio, para determinar se na proxima Activity utilizaremos POST ou PUT
                                isCarNew = true
                            }
                            //Adiciona os dados pertinentes no SharedPreferences a serem buscados na proxima Activity
                            editor.apply {
                                putInt("washDate", sqlCurrentDate.toString().toInt())
                                putString("carPlate", edtPlaca.text.toString().uppercase())
                                putString("carName", r.body()?.name)
                                putString("carOwner", r.body()?.owner)
                                putString("carTelephone", r.body()?.telephone.toString())
                                putString("carAddress", r.body()?.address)
                                putBoolean("isCarNew", isCarNew)
                                apply() //assync commit() = sync
                            }
                            //Cria a Activity de adicionar(ou atualizar) um novo carro
                            val intent = Intent(this@MainActivity, AddCarActivity::class.java)
                            startActivity(intent)
                        }
                        //Caso a chamada falhe
                        override fun onFailure(call: Call<Car>, t: Throwable) {
                            Toast.makeText(this@MainActivity, "Falha na conexão com o banco de dados, verifique a sua conexão a Internet", Toast.LENGTH_LONG).show()
                        }

                    })

                }
                .setNegativeButton("Cancelar") { dialog, id ->
                    // Cancela a adição(ou atualização) de um novo carro
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }
    }

    override fun onResume() {
        super.onResume()

        //cria a constante de Data para ser adicionada no banco de dados
        val sdf = SimpleDateFormat("ddMyyyy")
        val currentDate = sdf.format(Date())

        //Faz o binding da recyclerView e define o seu LayoutManager
        val recyclerViewWashServices = binding.recyclerViewWashServices
        recyclerViewWashServices.layoutManager = LinearLayoutManager(this)

        //Cria uma constante de SharedPreferences para adicionar dados a serem repassados a proxima
        //Activity referentes ao item clicado no recyclerView
        val sharedPreference = getSharedPreferences("washService", Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        val washService = RetrofitClient.createService(WashService::class.java) //Cria um novo serviço de WashService
        val washCall: Call<List<Wash>> = washService.listByDate(currentDate) //Busca os washServices do DB para a data atual
        //Cria uma chamada a API para receber uma lista dos serviços do dia e lista-los no washAdapter
        washCall.enqueue(object : Callback<List<Wash>>{
            override fun onResponse(call: Call<List<Wash>>, r: Response<List<Wash>>) {
                //Caso a chamada tenha uma resposta
                val list = r.body()
                if (list != null) {
                    washAdapter = WashAdapter( list.toMutableList()) //define o adapter passando a lista de washServices como parâmetro
                    //Define o onCLickListener de um item do Adapter
                    washAdapter.setOnClickListener(object : WashAdapter.onItemCLickListener{
                        override fun onItemCLick(position: Int) {
                           // Adiciona os dados do item clicado no SharedPreferences "washService"
                            editor.apply {
                                putLong("washId", list[position].id)
                                putInt("washDate", list[position].date)
                                putString("carPlate", list[position].car.plate)
                                putString("carName", list[position].car.name)
                                putString("carOwner", list[position].car.owner)
                                putString("carTelephone", list[position].car.telephone.toString())
                                putString("carAddress", list[position].car.address)
                                putBoolean("washDeliver", list[position].deliver )
                                putString("washPrice", list[position].price.toString())
                                putBoolean("washWax", list[position].wax)
                                putBoolean("washPayed", list[position].payed)
                                putString("washObs", list[position].obs)
                                apply() //assync commit() = sync
                            }
                            //
                            val intent = Intent(this@MainActivity, EditServiceActivity::class.java)
                            startActivity(intent)
                        }
                    })
                }
                recyclerViewWashServices.adapter = washAdapter
            }
            //Caso a chamada falhe
            override fun onFailure(call: Call<List<Wash>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Falha na conexão com o banco de dados, verifique a sua conexão a Internet", Toast.LENGTH_LONG).show()
            }

        })
    }
}