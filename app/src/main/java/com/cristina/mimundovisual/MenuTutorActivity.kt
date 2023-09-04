package com.cristina.mimundovisual

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*


//import com.firebase.ui.database.FirebaseRecyclerAdapter


class MenuTutorActivity : AppCompatActivity() {
    private lateinit var recycler:RecyclerView
    private lateinit var tareasList:ArrayList<Tarea>
    val data:DatabaseReference = FirebaseDatabase.getInstance().getReference()
    private lateinit var user:String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_tutor)


        val intent: Intent =getIntent()
        user =intent.getStringExtra("account").toString()
       // val database: DatabaseReference = FirebaseDatabase.getInstance().getReference().child("Mi-Mundo-Visual").child(user)
        val dt= Date()
        val df= SimpleDateFormat("yyMMdd", Locale.getDefault())
        val ftdt=df.format(dt)
        val datab=data.child("Mi-Mundo-Visual").child(user).child("Tareas$ftdt")

        //val datab=data.child("Mi-Mundo-Visual").child(user.toString()).child("Lista")
        val db:Query=datab.orderByChild("hora")

        recycler=findViewById(R.id.listaTareas)
        recycler.layoutManager=LinearLayoutManager(this)
        recycler.setHasFixedSize(true)

        //Ahora aquí ya si que va el adapter y viewholder


        tareasList = arrayListOf<Tarea>()

        db.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                tareasList.clear()
                if (snapshot.exists()){
                    for (tarSnap in snapshot.children){
                        val tarData= tarSnap.getValue(Tarea::class.java)
                        tareasList.add(tarData!!)
                    }
                    val mAdapter=TareaAdapterTutor(tareasList)
                    recycler.adapter=mAdapter
                    mAdapter.setOnItemClickListener(object: TareaAdapterTutor.onItemClickListener{
                        override fun onItemClick(position: Int){
                            val intent3=Intent(this@MenuTutorActivity,DetalleTareaTutorActivity::class.java)

                            //val key: String=mAdapter.get(position).getKey()

                           // intent3.putExtra("key",key)
                            intent3.putExtra("account",user)
                            intent3.putExtra("key",tareasList[position].key)
                            intent3.putExtra("tarHora",tareasList[position].hora)
                            intent3.putExtra("tarDescripcion",tareasList[position].descripcion)
                            intent3.putExtra("tarTitulo",tareasList[position].titulo)
                            intent3.putExtra("tarHecho",tareasList[position].hecho)
                            intent3.putExtra("tarEstado",tareasList[position].estado)
                            startActivity(intent3)
                        }
                    })


                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }

        )

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                //showing dialog and then closing the application..
                volver()

            }
        }
        onBackPressedDispatcher.addCallback(this,onBackPressedCallback)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menuoptions, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.getItemId()
        if (id == R.id.item1) {
            val intent1 = Intent (this, CrearTareaActivity::class.java)
            intent1.putExtra("account",user)
            startActivity(intent1)
        }
        if (id == R.id.item2) {
            val intent1 = Intent (this, ReportesActivity::class.java)
            intent1.putExtra("account",user)
            startActivity(intent1)
        }
        if (id == R.id.item3) {
            val intent1 = Intent (this, EditarPerfilActivity::class.java)
            intent1.putExtra("account",user)
            startActivity(intent1)
            //Toast.makeText(this, "Editar perfil", Toast.LENGTH_SHORT).show()
        }
        if (id == R.id.item4) {

            cerrarsesion()
            //Toast.makeText(this, "Cierre de sesión", Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }

    fun volver(){
        val intent1=Intent(this,SeleccionarPerfilActivity::class.java)
        intent1.putExtra("account",user)
        startActivity(intent1)
    }
    fun cerrarsesion(){
        val gso=GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
        val gsc:GoogleSignInClient=GoogleSignIn.getClient(this,gso)
        gsc.signOut().addOnCompleteListener(this){task->
            if(task.isSuccessful){
                val intent=Intent(this,MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

    }

}