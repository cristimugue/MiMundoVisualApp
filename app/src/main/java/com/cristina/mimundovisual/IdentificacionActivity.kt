package com.cristina.mimundovisual

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class IdentificacionActivity : AppCompatActivity() {
    private lateinit var btnSignIn: Button
    private lateinit  var mGoogleSignInClient: GoogleSignInClient
    private lateinit var gso: GoogleSignInOptions
    private lateinit var usuario: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_identificacion)
        btnSignIn = findViewById(R.id.SIGNIN)
        gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
            requestEmail().build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        val user:GoogleSignInAccount?=GoogleSignIn.getLastSignedInAccount(this)
        if(user!=null){
            toSeleccionarPerfilActivity(user.id.toString())
        }
        btnSignIn.setOnClickListener {
            signIn()
        }
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                //showing dialog and then closing the application..
                volver()
            }
        }
        onBackPressedDispatcher.addCallback(this,onBackPressedCallback)
    }
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task:Task <GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val cuenta = task.getResult(ApiException::class.java)!!
          val ref=FirebaseDatabase.getInstance().getReference().child("Mi-Mundo-Visual")
          ref.addListenerForSingleValueEvent(object: ValueEventListener {
              override fun onDataChange(dataSnapshot: DataSnapshot){
                  var found=false
                  for(childSnapshot in dataSnapshot.children){
                      val childId=childSnapshot.key
                      if(childId==cuenta.id.toString()){
                          found=true
                          break
                      }
                  }
                  if(found){
                  toSeleccionarPerfilActivity(cuenta.id.toString())
                  }else{
                      toIniciacionActivity(cuenta)
                  }
              }
              override fun onCancelled(error: DatabaseError) {
              }
          })
            } catch (e: ApiException) {
                Toast.makeText(this, "Algo ha ido mal", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }
    companion object {
        private const val RC_SIGN_IN = 1001
    }
    private fun toSeleccionarPerfilActivity(us:String) {
        val i = Intent(this, SeleccionarPerfilActivity::class.java)
        i.putExtra("account",us)
        startActivity(i)
        finish()
    }
    private fun toIniciacionActivity(us:GoogleSignInAccount?) {
        val user= us?.id
        //Toast.makeText(this,user,Toast.LENGTH_SHORT).show()
        val i2 = Intent(this, IniciacionActivity::class.java)
        i2.putExtra("account",user)
        startActivity(i2)
        finish()
    }
    fun volver(){
        val intent1=Intent(this,MainActivity::class.java)
        startActivity(intent1)
        finish()
    }
}