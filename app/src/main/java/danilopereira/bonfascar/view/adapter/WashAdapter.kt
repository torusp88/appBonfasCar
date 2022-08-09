package danilopereira.bonfascar.view.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import danilopereira.bonfascar.databinding.WashserviceItemBinding
import danilopereira.bonfascar.entities.Wash
import danilopereira.bonfascar.view.viewholder.MainActivity

class WashAdapter( private val washList: MutableList<Wash>):
    RecyclerView.Adapter<WashAdapter.WashViewHolder>() {

    private lateinit var mListener : onItemCLickListener

    interface onItemCLickListener{
        fun onItemCLick(position: Int)
    }

    fun setOnClickListener(listener: onItemCLickListener){
        mListener = listener

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WashViewHolder {
        val itemWashService = WashserviceItemBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return WashViewHolder(itemWashService,mListener)
    }

    override fun onBindViewHolder(holder: WashViewHolder, position: Int) {
        holder.txtPlate.text = washList[position].car.plate
        holder.txtCar.text = washList[position].car.name
        holder.txtPrice.text = "    R$" + washList[position].price.toString()
        when(washList[position].payed){
            true ->  holder.txtPG.setTextColor(-65536)
            false -> holder.txtPG.text = ""
        }
        when(washList[position].deliver){
            true -> {holder.txtDeliver.text = "Entregar"
                holder.txtDeliver.setTextColor(Color.parseColor("#008631"))}
            false -> holder.txtDeliver.text = ""
        }

    }

    override fun getItemCount() = washList.size

    inner class WashViewHolder(binding: WashserviceItemBinding, listener: onItemCLickListener): RecyclerView.ViewHolder(binding.root) {
        val txtPlate = binding.txtPlate
        val txtCar = binding.txtCar
        val txtPrice = binding.txtPrice
        val txtPG = binding.txtPG
        val txtDeliver = binding.txtDeliver
        init {
            itemView.setOnClickListener {
                listener.onItemCLick(adapterPosition)
            }
        }
    }
}