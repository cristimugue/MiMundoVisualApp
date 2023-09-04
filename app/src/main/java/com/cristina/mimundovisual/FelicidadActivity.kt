package com.cristina.mimundovisual

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class FelicidadActivity : AppCompatActivity(),View.OnClickListener {

    private lateinit var user:String
    private lateinit var key:String
    private lateinit var estado:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_felicidad)



        val b1 :ImageButton=findViewById(R.id.imageButton)
        val b2 :ImageButton=findViewById(R.id.imageButton2)
        val b3 :ImageButton=findViewById(R.id.imageButton3)
        val b4 :ImageButton=findViewById(R.id.imageButton4)

        b1.setImageResource(R.drawable.divertido)
        b2.setImageResource(R.drawable.alegre)
        b3.setImageResource(R.drawable.triste)
        b4.setImageResource(R.drawable.enfadado)

        b1.setOnClickListener(this)
        b2.setOnClickListener(this)
        b3.setOnClickListener(this)
        b4.setOnClickListener(this)

        val intent = intent
        user=intent.getStringExtra("account").toString()
        key=intent.getStringExtra("key").toString()



    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imageButton -> estado = "divertido"
            R.id.imageButton2 -> estado = "feliz"
            R.id.imageButton3 -> estado = "triste"
            R.id.imageButton4 -> estado = "enfadado"
        }
        val dt= Date()
        val df= SimpleDateFormat("yyMMdd", Locale.getDefault())
        val ftdt=df.format(dt)
        val db=FirebaseDatabase.getInstance().reference.child("Mi-Mundo-Visual").child(user).child("Tareas$ftdt")

        //val db= FirebaseDatabase.getInstance().getReference().child("Mi-Mundo-Visual").child(user).child("Lista")
        //val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        //val currentDate: String = sdf.format(Date())
        //val dbResults=FirebaseDatabase.getInstance().reference.child("Mi-Mundo-Visual").child(user).child("results").child(currentDate)

        val actualizacion: MutableMap<String, Any> = HashMap()
        actualizacion["/estado"] = estado
        db.child(key).updateChildren(actualizacion)

        val inte= Intent(this,MenuHijoActivity::class.java)
        inte.putExtra("key",key)
        inte.putExtra("account",user)
        startActivity(inte)
    }
}