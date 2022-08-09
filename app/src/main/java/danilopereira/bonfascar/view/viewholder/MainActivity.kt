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

        val sP = getSharedPreferences("addCar", Context.MODE_PRIVATE)
        val editor = sP.edit()

        val sdf = SimpleDateFormat("dd/M/yyyy")
        val currentDate = sdf.format(Date())

        val sqlDate = SimpleDateFormat("ddMyyyy")
        val sqlCurrentDate = sqlDate.format(Date())

        binding.textHeader.setText("São Paulo " + currentDate)

        binding.imgAdd.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val edtPlaca = EditText(this)
            edtPlaca.setHint("Placa")
            edtPlaca.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            builder.setMessage("Digite a placa do carro:")
                .setCancelable(false)
                .setView(edtPlaca)
                .setPositiveButton("Adicionar") { dialog, id ->
                    //Buscar placa e adicionar dados no sharedPreferences
                    val carService = RetrofitClient.createService(CarService::class.java)
                    val requestCar: Call<Car> = carService.getCar(edtPlaca.text.toString())
                    requestCar.enqueue(object : Callback<Car>{
                        override fun onResponse(call: Call<Car>, r: Response<Car>) {
                            var isCarNew: Boolean = false
                            if(r.body()?.name == null){
                                isCarNew = true
                            }
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
                            val intent = Intent(this@MainActivity, AddCarActivity::class.java)
                            startActivity(intent)
                        }

                        override fun onFailure(call: Call<Car>, t: Throwable) {
                            val s =""
                        }

                    })

                }
                .setNegativeButton("Cancelar") { dialog, id ->
                    // Cancela a exclusão
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }
    }

    override fun onResume() {
        super.onResume()

        val sdf = SimpleDateFormat("ddMyyyy")
        val currentDate = sdf.format(Date())

        val recyclerViewWashServices = binding.recyclerViewWashServices
        recyclerViewWashServices.layoutManager = LinearLayoutManager(this)

        val sharedPreference = getSharedPreferences("washService", Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        val washService = RetrofitClient.createService(WashService::class.java)
        val washCall: Call<List<Wash>> = washService.listByDate(currentDate)
        washCall.enqueue(object : Callback<List<Wash>>{
            override fun onResponse(call: Call<List<Wash>>, r: Response<List<Wash>>) {
                val list = r.body()
                if (list != null) {
                    washAdapter = WashAdapter( list.toMutableList())
                    washAdapter.setOnClickListener(object : WashAdapter.onItemCLickListener{
                        override fun onItemCLick(position: Int) {
                            var washId = list[position].id
                            var washDate = list[position].date
                            var carPlate = list[position].car.plate
                            var carName = list[position].car.name
                            var carOwner = list[position].car.owner
                            var carTelephone = list[position].car.telephone.toString()
                            var carAddress = list[position].car.address
                            var washDeliver = list[position].deliver //Boolean
                            var washPrice = list[position].price.toString()
                            var washWax = list[position].wax//Boolean
                            var washPayed = list[position].payed//Boolean
                            var washObs = list[position].obs


                            editor.apply {
                                putLong("washId", washId)
                                putInt("washDate", washDate)
                                putString("carPlate", carPlate)
                                putString("carName", carName)
                                putString("carOwner", carOwner)
                                putString("carTelephone", carTelephone)
                                putString("carAddress", carAddress)
                                putBoolean("washDeliver", washDeliver)
                                putString("washPrice", washPrice)
                                putBoolean("washWax", washWax)
                                putBoolean("washPayed", washPayed)
                                putString("washObs", washObs)
                                apply() //assync commit() = sync
                            }
                            //Toast.makeText(this@MainActivity, "$mPlate", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@MainActivity, EditServiceActivity::class.java)
                            startActivity(intent)
                        }
                    })
                }
                recyclerViewWashServices.adapter = washAdapter
            }

            override fun onFailure(call: Call<List<Wash>>, t: Throwable) {
                val s = ""
            }

        })
    }
}