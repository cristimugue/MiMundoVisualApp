package com.cristina.mimundovisual

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.FirebaseDatabase

class SeleccionarPerfilActivity : AppCompatActivity() {

    private var nombre:String=""
    private var contrasena:String=""

    private lateinit var user:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seleccionar_perfil)
        
        val btnHijo:Button = findViewById(R.id.botonhijo)
        val btnTutor: Button= findViewById(R.id.botonmadre)
        
        val intent: Intent =intent

        user=intent.getStringExtra("account").toString()
        val dbRef= FirebaseDatabase.getInstance().getReference().child("Mi-Mundo-Visual").child(user)
           dbRef.child("Usuario").get().addOnSuccessListener {
               nombre=it.value.toString()
               btnHijo.setText("Continuar como $nombre")

            }.addOnFailureListener{
                Log.e("firebase", "Error getting data", it)
            }


        dbRef.child("Clave").get().addOnSuccessListener {
            Log.i("firebase", "Got value ${it.value}")
            contrasena=it.value.toString()



        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }


        btnHijo.setOnClickListener{
            val intent1= Intent(this, MenuHijoActivity::class.java)
            intent1.putExtra("account",user)
            startActivity(intent1)
        }
        btnTutor.setOnClickListener{
            //val intent2= Intent(this, TutorPasswordActivity::class.java )
            //intent2.putExtra("account",user)
            //startActivity(intent2)
            openPasswordDialog()
        }
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                //showing dialog and then closing the application..
                volver()

            }
        }
        onBackPressedDispatcher.addCallback(this,onBackPressedCallback)
    }
    fun volver(){
        val intent1=Intent(this,MainActivity::class.java)
        intent1.putExtra("account",user)
        startActivity(intent1)
    }
    private fun openPasswordDialog(){
        val mDialog=AlertDialog.Builder(this)
        val inflater=layoutInflater

        val mDialogView=inflater.inflate(R.layout.activity_clave_tutor,null)
        mDialog.setView(mDialogView)

        val etPassword=mDialogView.findViewById<EditText>(R.id.etpw)
        val btPassword=mDialogView.findViewById<Button>(R.id.button)

        val alertDialog=mDialog.create()
        alertDialog.show()

        btPassword.setOnClickListener{
            val pw: String =etPassword.text.toString()
            if (pw==contrasena)
            {
                val intent1 =Intent(this,MenuTutorActivity::class.java)
                intent1.putExtra("account",user)
                startActivity(intent1)
            }else{
                Toast.makeText(this, "Contraseña inválida", Toast.LENGTH_SHORT).show()
            }
        }

    }
}