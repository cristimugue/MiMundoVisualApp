package com.cristina.mimundovisual

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView

class TareaAdapterTutor (private val tarList: ArrayList<Tarea>):
    RecyclerView.Adapter<TareaAdapterTutor.ViewHolder>(){

    private lateinit var mListener: onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position:Int)
    }

    fun setOnItemClickListener(clickListener: onItemClickListener){
        mListener=clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
        val itemView=LayoutInflater.from(parent.context).inflate(R.layout.tareaunicatutor, parent,false)
        return ViewHolder(itemView,mListener)
    }

    override fun onBindViewHolder(holder: ViewHolder,position: Int){
        val currenttar=tarList[position]
        holder.tarName.text=currenttar.titulo
        holder.tarHora.text=currenttar.hora
        holder.tarHecho.isChecked = currenttar.hecho
        val estado=currenttar.estado
        if(estado=="feliz"){
            holder.tarEstado.setImageResource(R.drawable.alegre)
        }
        else if(estado=="divertido"){
            holder.tarEstado.setImageResource(R.drawable.divertido)
        }
        else if(estado=="triste"){
            holder.tarEstado.setImageResource(R.drawable.triste)
        }
        else if(estado=="enfadado"){
            holder.tarEstado.setImageResource(R.drawable.enfadado)
        }else{
            holder.tarEstado.isVisible=false
        }
    }

    override fun getItemCount():Int{
        return tarList.size
    }
    class ViewHolder( itemView: View, clickListener: onItemClickListener): RecyclerView.ViewHolder(itemView) {

        val tarName: TextView =itemView.findViewById(R.id.tvTitle)
        val tarHora: TextView=itemView.findViewById(R.id.hora)
        var tarHecho: CheckBox=itemView.findViewById(R.id.cbhecho)
        val tarEstado:ImageView=itemView.findViewById(R.id.imageViewestado)


        init{
            itemView.setOnClickListener{
                clickListener.onItemClick(adapterPosition)
            }

        }

    }

}