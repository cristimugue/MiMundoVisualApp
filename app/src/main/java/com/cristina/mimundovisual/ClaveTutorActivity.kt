package com.cristina.mimundovisual

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class ClaveTutorActivity : AppCompatActivity() {

    lateinit var user:String
    lateinit var contrasena:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clave_tutor)

        val password: EditText = findViewById(R.id.etpw)
        val btnIngresar: Button = findViewById(R.id.button)

        val intent: Intent = getIntent()
        user =intent.getStringExtra("account").toString()

        val dbRef= FirebaseDatabase.getInstance().getReference().child("Mi-Mundo-Visual").child(user)

        dbRef.child("Clave").get().addOnSuccessListener {
            Log.i("firebase", "Got value ${it.value}")
            contrasena=it.value.toString()



        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }

        btnIngresar.setOnClickListener{
            val pw: String =password.text.toString()
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