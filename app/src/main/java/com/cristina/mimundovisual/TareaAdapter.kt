package com.cristina.mimundovisual

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class TareaAdapter (private val tarList: ArrayList<Tarea>):
    RecyclerView.Adapter<TareaAdapter.ViewHolder>(){

    private lateinit var mListener: onItemClickListener
    //private val context:Context

    interface onItemClickListener{
        fun onItemClick(position:Int)
    }

    fun setOnItemClickListener(clickListener: onItemClickListener){
        mListener=clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
        val itemView=LayoutInflater.from(parent.context).inflate(R.layout.tareaunicatutelado, parent,false)
        return ViewHolder(itemView,mListener)
    }

    override fun onBindViewHolder(holder: ViewHolder,position: Int){

        val currenttar=tarList[position]
        holder.tarName.text=currenttar.titulo
        holder.tarHora.text="A las "+currenttar.hora
        if(currenttar.hecho){
            holder.lay.background = ColorDrawable(Color.parseColor("#758BC34A"))
            holder.tarHecho.text="REALIZADA"

        }else{
            holder.lay.background = ColorDrawable(Color.parseColor("#75FF5722"))
            holder.tarHecho.text="PENDIENTE"
        }
        val imagesize= 500
        val id=Pictograma.getpicto(currenttar.titulo.toString())
        val lyParams=holder.tarImage.layoutParams
        lyParams.width=imagesize
        lyParams.height=imagesize
        holder.tarImage.layoutParams=lyParams
        //Glide.with(holder.itemView.context).load(id).override(450,450).into(holder.tarImage)
        Glide.with(holder.itemView.context)
            .load(id)
            .override(imagesize,imagesize)
            .centerInside()
            .into(holder.tarImage)
    }
    override fun getItemCount():Int{
        return tarList.size
    }
    class ViewHolder( itemView: View, clickListener: onItemClickListener): RecyclerView.ViewHolder(itemView) {

        val tarName: TextView =itemView.findViewById(R.id.tvTitle)
        val tarHora: TextView=itemView.findViewById(R.id.hora)
       // var tarHecho: CheckBox=itemView.findViewById(R.id.cbhecho)
        val tarHecho:TextView=itemView.findViewById(R.id.cbhecho)
        var tarImage:ImageView=itemView.findViewById(R.id.imagenprevia)
        val lay:LinearLayout=itemView.findViewById(R.id.linearlayouttarea)
        init{
            itemView.setOnClickListener{
                clickListener.onItemClick(adapterPosition)
            }
        }
    }
}