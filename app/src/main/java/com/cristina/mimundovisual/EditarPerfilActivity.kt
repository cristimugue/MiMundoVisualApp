package com.cristina.mimundovisual

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase

class EditarPerfilActivity : AppCompatActivity() {
    private var user:String=""
    private val dbR=FirebaseDatabase.getInstance().reference
    private lateinit var etcpw:EditText
    private lateinit var etpw1:EditText
    private lateinit var etpw2:EditText
    private lateinit var etname:EditText
    private lateinit var etname2:EditText
    private var passwordsaved=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cambio_clave)

        etcpw=findViewById(R.id.etPassword)
        etpw1=findViewById(R.id.etPassword2)
        etpw2=findViewById(R.id.etPassword3)
        etname=findViewById(R.id.etNombrecambio)
        etname2=findViewById(R.id.etNombrecambio2)
        val btn=findViewById<Button>(R.id.btnchngepw)

        val intent=intent
        user=intent.getStringExtra("account").toString()


        btn.setOnClickListener{
            val currentpassword= etcpw.text.toString()
            val newpassword=etpw1.text.toString()
            val confirmnewpassword=etpw2.text.toString()
            val newname=etname.text.toString()
            val newname2=etname2.text.toString()

            val dbRef=dbR.child("Mi-Mundo-Visual").child(user)
            dbRef.child("Clave").get().addOnSuccessListener {
                Log.i("firebase", "Got value ${it.value}")
                passwordsaved=it.value.toString()
                if(currentpassword.isNotEmpty()) {
                    if (currentpassword.equals(passwordsaved)) {
                        if (newpassword.equals(confirmnewpassword)) {
                            //actualizar
                            val data = dbRef.child("Clave")
                            data.setValue(newpassword)
                            Toast.makeText(
                                this,
                                "Contraseña actualizada con éxito",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent2 = Intent(this, MenuTutorActivity::class.java)
                            intent2.putExtra("account", user)
                            startActivity(intent2)
                        } else {
                            Toast.makeText(
                                this,
                                "Por favor, confirme la nueva contraseña",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(this, "Contraseña actual incorrecta", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

            }.addOnFailureListener{
                Log.e("firebase", "Error getting data", it)
            }
            dbRef.child("Usuario").get().addOnSuccessListener {
                Log.i("firebase", "Got value ${it.value}")

                if(newname.isNotEmpty()) {
                    if (newname.equals(newname2)) {
                        //actualizar
                        val data = dbRef.child("Usuario")
                        data.setValue(newname)
                        Toast.makeText(this, "Nombre actualizado con éxito", Toast.LENGTH_SHORT)
                            .show()
                        val intent2 = Intent(this, MenuTutorActivity::class.java)
                        intent2.putExtra("account", user)
                        startActivity(intent2)
                    } else {
                        Toast.makeText(this,"Por favor, confirme el nuevo nombre",Toast.LENGTH_SHORT).show()
                    }
                } else{
                    //Toast.makeText(this,"Por favor, indique un nuevo nombre",Toast.LENGTH_SHORT).show()
                }

            }.addOnFailureListener{
                Log.e("firebase", "Error getting data", it)
            }
        }
    }

}