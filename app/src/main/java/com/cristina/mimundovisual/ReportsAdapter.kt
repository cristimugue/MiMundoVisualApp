package com.cristina.mimundovisual

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView

class ReportsAdapter(private var tareasList: List<Tarea>) : RecyclerView.Adapter<ReportsAdapter.ReportViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.report, parent, false)
        return ReportViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val tarea = tareasList[position]
        holder.tvTitle.text = tarea.titulo
        holder.tvDescription.text = tarea.descripcion
        holder.tvHora.text = tarea.hora
        holder.tvHecho.text = if (tarea.hecho) "Realizada" else "No realizada"
        if(!tarea.descripcion.isNullOrBlank()){
            holder.tvDescription.visibility=View.VISIBLE
        }
        val estado=tarea.estado
        if(estado=="feliz"){
            holder.ivestado.setImageResource(R.drawable.alegre)
        }
        else if(estado=="divertido"){
            holder.ivestado.setImageResource(R.drawable.divertido)
        }
        else if(estado=="triste"){
            holder.ivestado.setImageResource(R.drawable.triste)
        }
        else if(estado=="enfadado"){
            holder.ivestado.setImageResource(R.drawable.enfadado)
        }else{
            holder.ivestado.isVisible=false
        }
       /* if(tarea.hecho){
            holder.lay.setBackgroundDrawable(ColorDrawable(Color.parseColor("#758BC34A")))
        }else{
            holder.lay.setBackgroundDrawable(ColorDrawable(Color.parseColor("#75FF5722")))
        }*/
        //holder.bind(tarea)
    }

    override fun getItemCount(): Int {
        return tareasList.size
    }
    fun updateData(newTareasList: List<Tarea>) {
        tareasList = newTareasList
        notifyDataSetChanged()
    }
    fun clearData(){
        tareasList= emptyList()
        notifyDataSetChanged()
    }


    inner class ReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val tvHora: TextView = itemView.findViewById(R.id.tvHour)
        val tvHecho: TextView = itemView.findViewById(R.id.tvDone)
        val ivestado: ImageView = itemView.findViewById(R.id.imagenreport)
        val lay: LinearLayout =itemView.findViewById(R.id.linearlayoutreport)
/*
        fun bind(tarea: Tarea) {
            tvTitle.text = tarea.titulo
            tvDescription.text = tarea.descripcion
            tvHora.text = tarea.hora
            tvHecho.text = if (tarea.hecho) "Hecho" else "No Hecho"
            if(tarea.hecho){
                holder.lay.setBackgroundDrawable(ColorDrawable(Color.parseColor("#758BC34A")))
            }else{}
        }*/
    }
}