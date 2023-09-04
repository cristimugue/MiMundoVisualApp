package com.cristina.mimundovisual

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.GsonBuilder
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.set


class CrearTareaActivity : AppCompatActivity() {
    private lateinit var titulo:String
    private lateinit var etDescripcion: EditText
    private lateinit var add: Button
    private lateinit var dbRef:DatabaseReference
    private lateinit var user:String
    private lateinit var time:TimePicker
    private lateinit var spcategoria:Spinner
    private lateinit var spsubcategoria:Spinner
    private lateinit var subcatAdap:ArrayAdapter<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_tarea)
        add = findViewById(R.id.btnadd)
        val atras: ImageButton = findViewById(R.id.imageButton)
        etDescripcion = findViewById(R.id.etdescripcion)
        spcategoria=findViewById(R.id.spinnercat)
        spsubcategoria=findViewById(R.id.spinnersubcat)
        time=findViewById(R.id.tp)
        time.setIs24HourView(true)
        val intent = intent
        user =intent.getStringExtra("account").toString()
        //get date to save in listadate
        val dt= Date()
        val df=SimpleDateFormat("yyMMdd",Locale.getDefault())
        val ftdt=df.format(dt)
        dbRef=FirebaseDatabase.getInstance().reference.child("Mi-Mundo-Visual").child(user).child("Tareas$ftdt")
        val categoria = arrayOf(
            "Seleccione la categoría",
            "Alimentación",
            "Desplazamiento",
            "Educación",
            "Hogar",
            "Ocio",
            "Rutina",
            "Otros"
        )
        val categAdap=ArrayAdapter(this,R.layout.my_spinner_item,categoria)
        categAdap.setDropDownViewResource(R.layout.my_spinner_dropdown_item)
        spcategoria.adapter=categAdap
        spcategoria.post{spcategoria.setSelection(0)}

        subcatAdap=ArrayAdapter(this,R.layout.my_spinner_item)
        subcatAdap.setDropDownViewResource(R.layout.my_spinner_dropdown_item)
        spsubcategoria.adapter=subcatAdap

        spcategoria.onItemSelectedListener=object:AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selcat=categoria[position]
                val subcategoria=getSubCat(selcat)
                updateSubCatSp(subcategoria)
                subcatAdap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spsubcategoria.adapter=subcatAdap
                spsubcategoria.onItemSelectedListener=object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        titulo = spsubcategoria.getItemAtPosition(position).toString()

                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        // Handle case when nothing is selected (if needed)
                    }
                }

            }
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        atras.setOnClickListener{
            volver()
        }
        add.setOnClickListener{
                guardarTarea()
        }
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                //showing dialog and then closing the application..
                volver()
            }
        }
        onBackPressedDispatcher.addCallback(this,onBackPressedCallback)
    }

    private fun getSubCat(categoria: String): List<String> {
        return when (categoria) {
            "Alimentación"-> listOf("Seleccione la tarea","Desayunar", "Almorzar", "Merendar","Cenar", "Beber")
            "Ocio"-> listOf("Seleccione la tarea","Jugar", "Andar", "Coger el autobús", "Correr", "Dibujar", "Escuchar música", "Hacer deporte", "Jugar", "Jugar al ordenador", "Leer", "Nadar", "Pasear", "Ver la tele")
            "Desplazamiento"-> listOf("Seleccione la tarea","Ir a casa", "Ir al avión", "Ir al campo", "Ir al cine", "Ir al colegio", "Ir al parque", "Ir a la playa", "Ir de excursión", "Ir en autobús", "Ir en barco", "Ir en coche", "Ir en avión", "Ir de viaje", "Visitar a", "Viajar")
            "Educación"-> listOf("Seleccione la tarea","Aprender", "Clase de educación física", "Clase de idiomas", "Clase de lengua", "Clase de matemáticas", "Clase de música", "Clase de plástica", "Estudiar", "Exámen", "Hacer la tarea", "Vacaciones")
            "Rutina"-> listOf("Seleccione la tarea","Bañarse", "Cortarse el pelo", "Cortarse las uñas", "Dormir", "Dormir la siesta", "Ducharse", "Ir al baño", "Irse a la cama", "Lavarse la cara", "Lavarse las manos", "Lavarse los dientes", "Levantarse", "Ponerse el pijama", "Vestirse")
            "Hogar"-> listOf("Seleccione la tarea","Barrer", "Cocinar", "Fregar", "Hacer la cama", "Limpiar", "Poner la mesa", "Recoger", "Recoger la mesa")
            "Otros"-> listOf("Seleccione la tarea",  "Ayudar a","Ir a votar","Ir al médico", "Llamar a")
            else -> listOf("Seleccione la tarea") // Empty list for unknown categories
        }
    }
    private fun updateSubCatSp(subcatlist:List<String>) {
        val newList = ArrayList(subcatlist)
        subcatAdap.clear()
        subcatAdap.addAll(newList)
        subcatAdap.notifyDataSetChanged()
    }

    private fun guardarTarea() {
        val hora:String
        val horas=time.hour
        val minutos=time.minute
        //val name=etTitulo.text.toString().trim()
        if(horas<=9){
            if(minutos<=9){
                hora=StringBuilder().append("0").append(horas).append(":0").append(minutos).toString()
            }else{
                hora=StringBuilder().append("0").append(horas).append(":").append(minutos).toString()
            }
        }else if(minutos<=9){
            hora=StringBuilder().append(horas).append(":0").append(minutos).toString()
        }else{
            hora=StringBuilder().append(horas).append(":").append(minutos).toString()
        }
        val descripcion=etDescripcion.text.toString()
        if (hora.isEmpty()){
            showToast("Por favor, introduzca la hora programada para la actividad")
        }else if(titulo.isEmpty()){
            showToast("Por favor, seleccione la tarea")
        }
        else{
            val itemData=HashMap<String,Any>()
            itemData["titulo"]=titulo
            itemData["descripcion"]=descripcion
            itemData["hora"]=hora
            itemData["hecho"]=false
            val opid = dbRef.push().key
            itemData["key"] = opid.toString()
            if (opid != null) {
                dbRef.child(opid).setValue(itemData).addOnCompleteListener{
                    //showToast("Tarea guardada con éxito")
                    val intent2= Intent(this,MenuTutorActivity::class.java)
                    intent2.putExtra("account",user)
                    intent2.putExtra("key",opid.toString())
                    startActivity(intent2)
                }.addOnFailureListener{err ->
                    showToast("Error ${err.message}")
                }
            }
        }
    }
    fun volver(){
        val intent1=Intent(this,MenuTutorActivity::class.java)
        intent1.putExtra("account",user)
        startActivity(intent1)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}