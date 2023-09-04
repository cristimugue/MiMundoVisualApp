package com.cristina.mimundovisual

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase

class IniciacionActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etContrasena: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_iniciacion)

        etNombre=findViewById(R.id.tvnombre)
        etContrasena=findViewById(R.id.tvmail)
        val boton=findViewById<Button>(R.id.guardarnuevousuario)

        val intent: Intent =intent


        boton.setOnClickListener{

            val usuario=intent.getStringExtra("account").toString()
            val db= FirebaseDatabase.getInstance().getReference().child("Mi-Mundo-Visual")


            db.child(usuario).child("Clave").setValue(etContrasena.text.toString())

            db.child(usuario).child("Usuario").setValue(etNombre.text.toString()).addOnCompleteListener{
                Toast.makeText(this,"Perfil guardado con éxito", Toast.LENGTH_LONG).show()


            }.addOnFailureListener{err ->
                Toast.makeText(this,"Error ${err.message}", Toast.LENGTH_LONG).show()
            }

            val i = Intent(this, SeleccionarPerfilActivity::class.java)
            i.putExtra("account",usuario)
            startActivity(i)
            finish()
        }



    }
}