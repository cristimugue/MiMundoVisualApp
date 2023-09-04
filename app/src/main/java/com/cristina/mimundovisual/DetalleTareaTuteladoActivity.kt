package com.cristina.mimundovisual

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class DetalleTareaTuteladoActivity : AppCompatActivity() {

    private lateinit var nombre: TextView
    private lateinit var descripcion: TextView
    private lateinit var hora: TextView
    private lateinit var done: CheckBox
    private lateinit var cambiar: Button
    private lateinit var imagen: ImageView
    private lateinit var atras: ImageButton
    private lateinit var user:String
    private lateinit var key:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_tarea_tutelado)

        nombre=findViewById(R.id.titulodetallehijo)
        descripcion=findViewById(R.id.descripciondetallehijo)
        hora=findViewById(R.id.horadetallehijo)
        done=findViewById(R.id.cbhechocambiar)
        cambiar=findViewById(R.id.botonguardarhecho)
        imagen=findViewById(R.id.imagendetallehijo)
        atras=findViewById(R.id.imageButton4)

        val intent=intent
        user=intent.getStringExtra("account").toString()
        val extras:Bundle= intent.extras!!
        nombre.text=extras.getString("tarTitulo")
        descripcion.text=extras.getString("tarDescripcion")
        hora.text="A las "+extras.getString("tarHora")
        key=extras.getString("key").toString()
        val boleano:Boolean=extras.getBoolean("hecho")
        val id:Int=extras.getInt("imagen")
        imagen.setImageResource(id)
        Glide.with(this)
            .load(id)
            .centerInside()
            .into(imagen)
        //Glide.with(this).load(imageUrl).into(imagen)
        if (descripcion.text.isEmpty()){
            descripcion.visibility=View.INVISIBLE
        }
        done.isChecked = boleano
        done.setOnCheckedChangeListener { buttonView, isChecked -> done.isChecked = isChecked }

        atras.setOnClickListener{
            val intent1= Intent(this,MenuHijoActivity::class.java)
            intent1.putExtra("key",key)
            intent1.putExtra("account",user)
            startActivity(intent1)
        }
        cambiar.setOnClickListener(View.OnClickListener{
            val hecho:Boolean=done.isChecked
            updatechild(hecho)
        })

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                //showing dialog and then closing the application..
               volver()
            }
        }
        onBackPressedDispatcher.addCallback(this,onBackPressedCallback)
    }
    fun volver(){
        val intent1=Intent(this,MenuHijoActivity::class.java)
        intent1.putExtra("account",user)
        startActivity(intent1)
    }
    private fun updatechild(hecho:Boolean){
        val dt= Date()
        val df= SimpleDateFormat("yyMMdd", Locale.getDefault())
        val ftdt=df.format(dt)
        val db=FirebaseDatabase.getInstance().reference.child("Mi-Mundo-Visual").child(user).child("Tareas$ftdt")
        //val db=FirebaseDatabase.getInstance().reference.child("Mi-Mundo-Visual").child(user).child("Lista")
        val actualizacion: MutableMap<String, Any> = HashMap()
        actualizacion["/hecho"] = hecho
        actualizacion["/estado"]=""
        db.child(key).updateChildren(actualizacion)
        if(hecho==true){
            val intent2= Intent(this,FelicidadActivity::class.java)
            intent2.putExtra("key",key)
            intent2.putExtra("account",user)
            startActivity(intent2)
        }else{
            val intent2= Intent(this,MenuHijoActivity::class.java)
            intent2.putExtra("account",user)
            startActivity(intent2)
        }
    }
}