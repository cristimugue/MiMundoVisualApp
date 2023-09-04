package com.cristina.mimundovisual

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class DetalleTareaTutorActivity : AppCompatActivity() {

    private lateinit var tvtitulo: TextView
    private lateinit var tvdescripcion: TextView
    private lateinit var tvhora: TextView
    private lateinit var tvhecho: TextView
    private lateinit var ivestado: ImageView
    private lateinit var  editar: Button
    private lateinit var  eliminar: Button
    private lateinit var llave:String
    private lateinit var user: String
    private lateinit var atras:ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_tarea_tutor)

        intent= intent
        user= intent.getStringExtra("account").toString()
        llave=intent.getStringExtra("key").toString()


        initView()
        setValuesToView()

        editar.setOnClickListener{
            openEditDialog(intent.getStringExtra("tarTitulo").toString(),
                    user,
                    llave)
        }
        eliminar.setOnClickListener{
            deleteTarea(llave)
        }
        atras.setOnClickListener{
            volver()
        }
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                //showing dialog and then closing the application..
                volver()

            }
        }
        onBackPressedDispatcher.addCallback(this,onBackPressedCallback)

    }
    private fun initView(){
        tvtitulo=findViewById(R.id.titulodetalle)
        tvdescripcion=findViewById(R.id.descripciondetalle)
        tvhora=findViewById(R.id.horadetalle)
        tvhecho=findViewById(R.id.textView31)
        ivestado=findViewById(R.id.imagenestado)
        editar= findViewById(R.id.botoneditar)
        eliminar= findViewById(R.id.botoneliminar)
        atras=findViewById(R.id.imageButton2)

    }

    private fun setValuesToView(){
        tvtitulo.text=intent.getStringExtra("tarTitulo")
        tvhora.text=intent.getStringExtra("tarHora")
        tvdescripcion.text=intent.getStringExtra("tarDescripcion")
        val estado=intent.getStringExtra("tarEstado")
        val hecho=intent.getBooleanExtra("tarHecho",false)
        if(hecho){
            tvhecho.text="Tarea realizada"
        }else{
            tvhecho.text="Tarea pendiente"
        }
        if(estado=="feliz"){
            ivestado.setImageResource(R.drawable.alegre)
        }
        else if(estado=="divertido"){
            ivestado.setImageResource(R.drawable.divertido)
        }
        else if(estado=="triste"){
            ivestado.setImageResource(R.drawable.triste)
        }
        else if(estado=="enfadado"){
            ivestado.setImageResource(R.drawable.enfadado)
        }else{
            ivestado.isVisible=false
        }
    }

    private fun openEditDialog(titulo: String, usuario: String, key: String){

        val mDialog= AlertDialog.Builder(this,R.style.AlertDialogCustom)
        val inflater=layoutInflater

        val mDialogView= inflater.inflate(R.layout.activity_editar_tarea,null)
        val titleview=inflater.inflate(R.layout.dialog_title,null)
        mDialog.setView(mDialogView)

        val etTitulo= mDialogView.findViewById<EditText>(R.id.titulodetalle2)
        val etDescripcion=mDialogView.findViewById<EditText>(R.id.descripciondetalle2)
        val etHora=mDialogView.findViewById<EditText>(R.id.horadetalle2)
        val editado=mDialogView.findViewById<Button>(R.id.botonguardaredicion)
        val atras1=mDialogView.findViewById<ImageButton>(R.id.imageButton3)
        val tituloaviso=titleview.findViewById<TextView>(R.id.tvtitle)

        etTitulo.setText(intent.getStringExtra("tarTitulo").toString())
        etDescripcion.setText(intent.getStringExtra("tarDescripcion").toString())
        etHora.setText(intent.getStringExtra("tarHora").toString())
        val hecho=intent.getBooleanExtra("tarHecho",false)
        val estado=intent.getStringExtra("tarEstado").toString()

        mDialog.setCustomTitle(titleview)
        tituloaviso.text="Editando tarea $titulo"

        //mDialog.setTitle("Editando tarea $titulo")

        val alertDialog= mDialog.create()
        alertDialog.show()

        atras1.setOnClickListener{
            alertDialog.dismiss()
        }

        editado.setOnClickListener {
            updateTarea(
                etTitulo.text.toString(),
                etDescripcion.text.toString(),
                etHora.text.toString(),
                key,usuario,hecho,estado
            )

            Toast.makeText(applicationContext, "Tarea editada con éxito", Toast.LENGTH_SHORT).show()

            //Le doy el valor nuevo al TV
            tvtitulo.text = etTitulo.text.toString()
            tvhora.text = etHora.text.toString()
            tvdescripcion.text = etDescripcion.text.toString()

            alertDialog.dismiss()
        }

    }
    private fun updateTarea(tit: String, des: String, hor: String, k:String, us: String, h:Boolean, s:String){
        val dt= Date()
        val df= SimpleDateFormat("yyMMdd", Locale.getDefault())
        val ftdt=df.format(dt)
        val dbRef=FirebaseDatabase.getInstance().reference.child("Mi-Mundo-Visual").child(user).child("Tareas$ftdt").child(k)
        //val dbRef= FirebaseDatabase.getInstance().reference.child("Mi-Mundo-Visual").child(us).child("Lista").child(k)
        val tarInfo=Tarea(k,tit,des,hor,h,s)
        dbRef.setValue(tarInfo)

    }

    private fun deleteTarea(key: String){
        val dt= Date()
        val df= SimpleDateFormat("yyMMdd", Locale.getDefault())
        val ftdt=df.format(dt)
        val dbRef=FirebaseDatabase.getInstance().reference.child("Mi-Mundo-Visual").child(user).child("Tareas$ftdt").child(key)
        //val dbRef= FirebaseDatabase.getInstance().reference.child("Mi-Mundo-Visual").child(user).child("Lista").child(key)
        val mTask=dbRef.removeValue()

        mTask.addOnSuccessListener {

            Toast.makeText(this,"Tarea eliminada con éxito",Toast.LENGTH_SHORT).show()
            val intent4= Intent(this,MenuTutorActivity::class.java)
            intent4.putExtra("account",user)
            intent4.putExtra("key",key)
            finish()
            startActivity(intent4)
        }.addOnFailureListener{error->
            Toast.makeText(this,"Error ${error.message}",Toast.LENGTH_SHORT).show()
        }
    }
    fun volver(){
        val intent1=Intent(this,MenuTutorActivity::class.java)
        intent1.putExtra("account",user)
        startActivity(intent1)
    }
}