package com.cristina.mimundovisual

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.RemoteViews
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.*
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList


class MenuHijoActivity : AppCompatActivity() {

    //Ubicación
    private var latitud:String=""
    private var longitud:String=""
    private lateinit var lblTiempo:TextView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    //Tiempo
    val API:String="15eae490e54aac4a3438ecc04454e34c"
    private lateinit var imagen:ImageView
    private lateinit var tiempo: String

    //Principal
    private lateinit var ListaTareas: ArrayList<Tarea>
    private lateinit var user:String
    private val permissionId = 1001
    private lateinit var tvsaludo:TextView
    private var nTareas: Int = 0

    //Alarmas
    private lateinit var alarmIntent: Intent


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_hijo)

        val lblDia = findViewById<TextView>(R.id.lblDia)
        lblTiempo = findViewById(R.id.lblTiempo)
        imagen=findViewById(R.id.ivTiempo)
        tvsaludo=findViewById(R.id.tvSaludo)

        val intent = intent
        user=intent.getStringExtra("account").toString()
        val dbR: DatabaseReference = FirebaseDatabase.getInstance().getReference().child("Mi-Mundo-Visual").child(user)
        val db=dbR

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        val isLocationEnabled = isLocationEnabled()
        if (isLocationEnabled) {
            //Toast.makeText(this, "Location enabled",Toast.LENGTH_SHORT).show()
            checkLocationPermission()
        } else {
            //Toast.makeText(this, "Location disabled",Toast.LENGTH_SHORT).show()
            displayLastKnownLocation()
        }
        //Día y hora
        val sdfHora = SimpleDateFormat("HH", Locale.getDefault())
        val sdfMinu = SimpleDateFormat("MM", Locale.getDefault())
        val dhora = Date()
        sdfHora.timeZone

        val horast = sdfHora.format(dhora)
        val minutost=sdfMinu.format(dhora)

        val oneHourInMillis = 60 * 60 * 1000
        val horamenos=Date(dhora.time-oneHourInMillis)
        val horamenost=sdfHora.format(horamenos)
        horamenost.toString()+":"+minutost

        horast.toString()+":"+minutost
        val hora:Int=horast.toInt()

        if((hora>=8)&&(hora<13)){
            tvsaludo.text="Buenos días "
        }else if((hora>=13)&&(hora<20)){
            tvsaludo.text="Buenas tardes "
        }else{
            tvsaludo.text="Buenas noches "
        }
        val today = LocalDate.now()
        val day = today.dayOfWeek
        val dia=diaespanol(day.toString())
        lblDia.text = "Hoy es $dia "

        //Databasereference for recyclerview
        val dt= Date()
        val df=SimpleDateFormat("yyMMdd",Locale.getDefault())
        val ftdt=df.format(dt)
        //val dbq: Query = db.child("Tareas$ftdt").orderByChild("hora").startAt(horamenost).limitToFirst(5)
        val dbq: Query = db.child("Tareas$ftdt").orderByChild("hora")
        //Recyclerview
        val recycler: RecyclerView = findViewById(R.id.lvTareas)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.setHasFixedSize(true)

        ListaTareas = arrayListOf()

        //Recyclerview touch listener
        dbq.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                ListaTareas.clear()

                if (snapshot.exists()) {
                    for (tarSnap in snapshot.children) {
                        val tarData = tarSnap.getValue(Tarea::class.java)
                        if(tarData!!.hora!!.substring(0,2)>=horamenost){
                            ListaTareas.add(tarData)
                        }else {
                            if (!tarData!!.hecho) {
                                ListaTareas.add(tarData)
                            }
                        }

                    }
                   // val undone =ListaTareas.filter{ !it.hecho }.take(5).toMutableList()

                    //val mAdapter = TareaAdapter(undone as ArrayList<Tarea>)
                    val mAdapter = TareaAdapter(ListaTareas)
                    recycler.adapter = mAdapter
                    nTareas=ListaTareas.count()


                    mAdapter.setOnItemClickListener(object : TareaAdapter.onItemClickListener {
                        override fun onItemClick(position: Int) {
                            val intent3 = Intent(this@MenuHijoActivity, DetalleTareaTuteladoActivity::class.java)
                            intent3.putExtra("account", user)
                            intent3.putExtra("key", ListaTareas[position].key)
                            intent3.putExtra("tarHora", ListaTareas[position].hora)
                            intent3.putExtra("tarDescripcion", ListaTareas[position].descripcion)
                            intent3.putExtra("tarTitulo", ListaTareas[position].titulo)
                            intent3.putExtra("hecho",ListaTareas[position].hecho)
                            val id=Pictograma.getpicto(ListaTareas[position].titulo.toString())
                            intent3.putExtra("imagen",id)
                            startActivity(intent3)
                        }
                    })
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
//PRUEBITATTATATTA
       // dbAlarm=db.child("Tareas$ftdt")

        alarmIntent = Intent(this, AlarmasForegroundService::class.java)
        alarmIntent.putExtra("usuario",user)
        alarmIntent.putExtra("lista","Tareas$ftdt")
        startForegroundService(alarmIntent)
        //fetchHourFromFirebase()
        //startRefreshHandler()

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                volver()
            }
        }
        onBackPressedDispatcher.addCallback(this,onBackPressedCallback)
    }

    //Function to disable backbutton
    fun volver(){
        val intent1=Intent(this,SeleccionarPerfilActivity::class.java)
        intent1.putExtra("account",user)
        startActivity(intent1)
    }
    //Translates days of week to spanish
    private fun diaespanol(day:String):String{
        var dia=""
        when(day){
            "MONDAY"->
                dia="Lunes"
            "TUESDAY"->
                dia="Martes"
            "WEDNESDAY"->
                dia="Miércoles"
            "THURSDAY"->
                dia="Jueves"
            "FRIDAY"->
                dia="Viernes"
            "SATURDAY"->
                dia="Sábado"
            "SUNDAY"->
                dia="Domingo"
        }
        return dia
    }
    //Check location permisions, return city name
    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            //Toast.makeText(this, "Location permissions=true",Toast.LENGTH_SHORT).show()
            getLocation()
        } else {
           // Toast.makeText(this, "Location permissions=false",Toast.LENGTH_SHORT).show()
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }
    private fun getLocation() {

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        //Toast.makeText(this, "Location : ${location.latitude} ${location.longitude}",Toast.LENGTH_SHORT).show()
                        saveLastKnownLocation(location.latitude, location.longitude)
                        displayLocation(location.latitude, location.longitude)
                    } else {
                        //Toast.makeText(this, "CANOT GET LOCATION NOW",Toast.LENGTH_SHORT).show()
                        displayLastKnownLocation()
                        //updateLocation()
                    }
                }

    }
    private fun saveLastKnownLocation(latitude: Double, longitude: Double) {
        val editor = sharedPreferences.edit()
        editor.putFloat("lastLatitude", latitude.toFloat())
        editor.putFloat("lastLongitude", longitude.toFloat())
        editor.apply()
    }
    private fun displayLocation(latitude: Double, longitude: Double) {

        getCoordinates(latitude.toString(),longitude.toString())

        //Toast.makeText(this, "Location: $latitude $longitude",Toast.LENGTH_SHORT).show()
    }
    private fun displayLastKnownLocation() {
        val latitude = sharedPreferences.getFloat("lastLatitude", 0f).toDouble()
        val longitude = sharedPreferences.getFloat("lastLongitude", 0f).toDouble()

        if (latitude != 0.0 && longitude != 0.0) {
            getCoordinates(latitude.toString(),longitude.toString())
        } else {
            displayNoLocationAvailable()
        }
    }
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
    private fun displayNoLocationAvailable() {

        Toast.makeText(this, "No location available",Toast.LENGTH_SHORT).show()
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation()
            } else {
                displayLastKnownLocation()
            }
        }
    }
    private fun getCoordinates(la:String,lo:String){
        latitud=la
        longitud=lo
        weatherTask().execute()
        //Toast.makeText(this,"Latitud: $la Longitud: $lo",Toast.LENGTH_SHORT).show()
    }
    //task getting Openweathermap api--onPostExeute gives TV values
    inner class weatherTask : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg params: String?): String? {
            var response:String?
            try{ response=
                    URL("https://api.openweathermap.org/data/2.5/weather?lat=$latitud&lon=$longitud&appid=$API&units=metric&lang=en").readText(Charsets.UTF_8)
            }catch(e: Exception){ response=null }
            return response
        }
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try{
                val jsonObj= JSONObject(result)
                val main=jsonObj.getJSONObject("main")
                val weather=jsonObj.getJSONArray("weather").getJSONObject(0)
                //Aquí me falta acortar temp a 2 cifras
                val temp=main.getString("temp")
                val subcadena=temp.substring(0,2)
                subcadena+"ºC"
                val weatherdescription=weather.getString("description")
                tiempo=weatherdescription.capitalize()

                //findViewById<TextView>(R.id.lblTiempo).text=tempcorto
                val tiempoesp=traducirTiempo(tiempo)
                findViewById<TextView>(R.id.lblTiempo).text=tiempoesp
                setWeatherImage(tiempo)
            }catch(e: Exception){ }
        }
    }
    private fun traducirTiempo(tiempo: String):String {
        var esp=""

        when(tiempo) {
            "Clear sky" ->
                esp= "Está despejado"
            "Few clouds" ->
                esp= "Hay nubes"
            "Scattered clouds" ->
                esp= "Hay nubes"
            "Overcast clouds" ->
                esp= "Está nublado"
            "Broken clouds" ->
                esp= "Está nublado"
            "Shower rain" ->
                esp= "Está lloviendo"
            "Heavy intensity shower rain" ->
                esp= "Está lloviendo"
            "Light intensity shower rain" ->
                esp= "Está lloviendo"
            "Ragged shower rain" ->
                esp= "Está lloviendo"
            "Light intensity drizzle" ->
                esp= "Está lloviendo"
            "Drizzle" ->
                esp= "Está lloviendo"
            "heavy intensity drizzle" ->
                esp= "Está lloviendo"
            "light intensity drizzle rain" ->
                esp= "Está lloviendo"
            "drizzle rain" ->
                esp= "Está lloviendo"
            "heavy intensity drizzle rain" ->
                esp= "Está lloviendo"
            "shower rain and drizzle" ->
                esp= "Está lloviendo"
            "heavy shower rain and drizzle" ->
                esp= "Está lloviendo"
            "Shower drizzle" ->
                esp= "Está lloviendo"
            "Rain" ->
                esp= "Está lloviendo"
            "Moderate rain" ->
                esp= "Está lloviendo"
            "Light rain" ->
                esp= "Está lloviendo"
            "Heavy intensity rain" ->
                esp= "Está lloviendo"
            "Very heavy rain" ->
                esp= "Está lloviendo"
            "Extreme rain" ->
                esp= "Está lloviendo"
            "Thunderstorm" ->
                esp= "Hay tormenta"
            "Thunderstorm with light rain" ->
                esp= "Hay tormenta"
            "Thunderstorm with rain" ->
                esp= "Hay tormenta"
            "Thunderstorm with heavy rain" ->
                esp= "Hay tormenta"
            "Light thunderstorm" ->
                esp= "Hay tormenta"
            "Heavy thunderstorm" ->
                esp= "Hay tormenta"
            "Ragged thunderstorm" ->
                esp= "Hay tormenta"
            "Thunderstorm with light drizzle" ->
                esp= "Hay tormenta"
            "Thunderstorm with drizzle" ->
                esp= "Hay tormenta"
            "Thunderstorm with heavy drizzle" ->
                esp= "Hay tormenta"
            "Freezing rain" ->
                esp= "Hay tormenta"
            "Snow" ->
                esp= "Está nevando"
            "Light snow" ->
                esp= "Está nevando"
            "Heavy snow" ->
                esp= "Está nevando"
            "Sleet" ->
                esp= "Está nevando"
            "Light shower sleet" ->
                esp= "Está nevando"
            "Shower sleet" ->
                esp= "Está nevando"
            "Light rain and snow" ->
                esp= "Está nevando"
            "Rain and snow" ->
                esp= "Está nevando"
            "Light shower snow" ->
                esp= "Está nevando"
            "Shower snow" ->
                esp= "Está nevando"
            "heavy shower snow" ->
                esp= "Está nevando"
            "Mist" ->
                esp= "Hay niebla"
            "Smoke" ->
                esp= "Hay niebla"
            "Haze" ->
                esp= "Hay niebla"
            "Sand/dust whirls" ->
                esp= "Hay niebla"
            "Fog" ->
                esp= "Hay niebla"
            "Sand" ->
                esp= "Hay niebla"
            "Dust" ->
                esp= "Hay niebla"
            "Volcanic ash" ->
                esp= "Hay niebla"
            "Squalls" ->
                esp= "Hay niebla"
            "Tornado" ->
                esp= "Hay niebla"
        }
        return esp


        }
    //Setting weather image
    @SuppressLint("UseCompatLoadingForDrawables")
    fun setWeatherImage(value: String){
        when(value){
            "Clear sky"->
                imagen.setImageDrawable(resources.getDrawable(R.drawable.w01d))
            "Few clouds"->
                imagen.setImageDrawable(resources.getDrawable(R.drawable.w02d))
            "Scattered clouds"->
                imagen.setImageDrawable(resources.getDrawable(R.drawable.w03d))
            "Overcast clouds"->
                imagen.setImageDrawable(resources.getDrawable(R.drawable.w04d))
            "Broken clouds"->
                imagen.setImageDrawable(resources.getDrawable(R.drawable.w04d))
            "Shower rain"->
                imagen.setImageDrawable(resources.getDrawable(R.drawable.w09d))
            "Heavy intensity shower rain"->
                imagen.setImageDrawable(resources.getDrawable(R.drawable.w09d))
            "Light intensity shower rain"->
                imagen.setImageDrawable(resources.getDrawable(R.drawable.w09d))
            "Ragged shower rain"->
                imagen.setImageDrawable(resources.getDrawable(R.drawable.w09d))
            "Light intensity drizzle"->
                imagen.setImageDrawable(resources.getDrawable(R.drawable.w09d))
            "Drizzle"->
                imagen.setImageDrawable(resources.getDrawable(R.drawable.w09d))
            "heavy intensity drizzle"->
                imagen.setImageDrawable(resources.getDrawable(R.drawable.w09d))
            "light intensity drizzle rain"->
                imagen.setImageDrawable(resources.getDrawable(R.drawable.w09d))
            "drizzle rain"->
                imagen.setImageDrawable(resources.getDrawable(R.drawable.w09d))
            "heavy intensity drizzle rain"->
                imagen.setImageDrawable(resources.getDrawable(R.drawable.w09d))
            "shower rain and drizzle"->
                imagen.setImageDrawable(resources.getDrawable(R.drawable.w09d))
            "heavy shower rain and drizzle"->
                imagen.setImageDrawable(resources.getDrawable(R.drawable.w09d))
            "Shower drizzle"->
                imagen.setImageDrawable(resources.getDrawable(R.drawable.w09d))
            "Rain"->
                imagen.setImageDrawable(resources.getDrawable(R.drawable.w10d))
            "Moderate rain"->
                imagen.setImageDrawable(resources.getDrawable(R.drawable.w10d))
            "Light rain"->
                imagen.setImageDrawable(resources.getDrawable(R.drawable.w10d))
            "Heavy intensity rain"->
                imagen.setImageDrawable(resources.getDrawable(R.drawable.w10d))
            "Very heavy rain"->
                imagen.setImageDrawable(resources.getDrawable(R.drawable.w10d))
            "Extreme rain"->
                imagen.setImageDrawable(resources.getDrawable(R.drawable.w10d))
            "Thunderstorm"->
                imagen.setImageDrawable(resources.getDrawable(R.drawable.w11d))
            "Thunderstorm with light rain"->
                imagen.setImageDrawable(resources.getDrawable(R.drawable.w11d))
            "Thunderstorm with rain"->
                imagen.setImageDrawable(resources.getDrawable(R.drawable.w11d))
            "Thunderstorm with heavy rain"->
                imagen.setImageDrawable(resources.getDrawable(R.drawable.w11d))
            "Light thunderstorm"->
                imagen.setImageDrawable(resources.getDrawable(R.drawable.w11d))
            "Heavy thunderstorm"->
                imagen.setImageDrawable(resources.getDrawable(R.drawable.w11d))
            "Ragged thunderstorm"->
                imagen.setImageDrawable(resources.getDrawable(R.drawable.w11d))
            "Thunderstorm with light drizzle"->
                imagen.setImageDrawable(resources.getDrawable(R.drawable.w11d))
            "Thunderstorm with drizzle"->
                imagen.setImageDrawable(resources.getDrawable(R.drawable.w11d))
            "Thunderstorm with heavy drizzle"->
                imagen.setImageDrawable(resources.getDrawable(R.drawable.w11d))
            "Freezing rain"->
                imagen.setImageDrawable(resources.getDrawable(R.drawable.w13d))
            "Snow"->
                imagen.setImageDrawable(resources.getDrawable(R.drawable.w13d))
            "Light snow"->
                imagen.setImageDrawable(resources.getDrawable(R.drawable.w13d))
            "Heavy snow"->
                imagen.setImageDrawable(resources.getDrawable(R.drawable.w13d))
            "Sleet"->
                imagen.setImageDrawable(resources.getDrawable(R.drawable.w13d))
            "Light shower sleet"->
                imagen.setImageDrawable(resources.getDrawable(R.drawable.w13d))
            "Shower sleet"->
                imagen.setImageDrawable(resources.getDrawable(R.drawable.w13d))
            "Light rain and snow"->
                imagen.setImageDrawable(resources.getDrawable(R.drawable.w13d))
            "Rain and snow"->
                imagen.setImageDrawable(resources.getDrawable(R.drawable.w13d))
            "Light shower snow"->
                imagen.setImageDrawable(resources.getDrawable(R.drawable.w13d))
            "Shower snow"->
                imagen.setImageDrawable(resources.getDrawable(R.drawable.w13d))
            "heavy shower snow"->
                imagen.setImageDrawable(resources.getDrawable(R.drawable.w13d))
            "Mist"->
                imagen.setImageDrawable(resources.getDrawable(R.drawable.w50d))
            "Smoke"->
                imagen.setImageDrawable(resources.getDrawable(R.drawable.w50d))
            "Haze"->
                imagen.setImageDrawable(resources.getDrawable(R.drawable.w50d))
            "Sand/dust whirls"->
                imagen.setImageDrawable(resources.getDrawable(R.drawable.w50d))
            "Fog"->
                imagen.setImageDrawable(resources.getDrawable(R.drawable.w50d))
            "Sand"->
                imagen.setImageDrawable(resources.getDrawable(R.drawable.w50d))
            "Dust"->
                imagen.setImageDrawable(resources.getDrawable(R.drawable.w50d))
            "Volcanic ash"->
                imagen.setImageDrawable(resources.getDrawable(R.drawable.w50d))
            "Squalls"->
                imagen.setImageDrawable(resources.getDrawable(R.drawable.w50d))
            "Tornado"->
                imagen.setImageDrawable(resources.getDrawable(R.drawable.w50d))

        }
    }

}


class MyAlarm : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent
    ) {

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "my_channel_id"
        val channelName = "My Channel"
        val importance = NotificationManager.IMPORTANCE_HIGH

        val notificationChannel = NotificationChannel(channelId, channelName, importance)

        notificationManager.createNotificationChannel(notificationChannel)
        val extras:Bundle= intent.extras!!
        val titulo = intent.getStringExtra("titulo")
        val descripcion=extras.getString("descripcion")
        val hecho=extras.getBoolean("hecho")
        val key=extras.getString("key")
        val hora=extras.getString("hora")
        val user=extras.getString("user")
        val texto=extras.getString("texto")
        //var notificationIdCounter=extras.getInt("counter")

        val contentView = RemoteViews(context.packageName, R.layout.notification_layout)

        val titulolc= titulo!!.lowercase()
        val notificationId=titulolc.hashCode() ?:0
        contentView.setTextViewText(R.id.notification_title, "$texto $titulolc")
        contentView.setImageViewResource(R.id.imagennotificacion,R.mipmap.ic_launcher)

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setCustomBigContentView(contentView)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            //.setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(contentView)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)

        val alarmIntent = Intent(context, DetalleTareaTuteladoActivity::class.java)
        // Add any extra data you want to pass to the AlarmActivity
        alarmIntent.putExtra("account",user)
        alarmIntent.putExtra("tarTitulo", titulo)
        alarmIntent.putExtra("tarDescripcion",descripcion)
        alarmIntent.putExtra("hecho",hecho)
        alarmIntent.putExtra("key",key)
        alarmIntent.putExtra("tarHora",hora)
        val id=Pictograma.getpicto(titulo.toString())
        alarmIntent.putExtra("imagen",id)

        // Create a PendingIntent to handle the tap on the notification
        val pendingIntent = PendingIntent.getActivity(context, notificationId, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        notificationBuilder.setContentIntent(pendingIntent)

        //notificationBuilder.color=Color.BLUE

        val notification:Notification=notificationBuilder.build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel?.let {
                notificationManager.createNotificationChannel(it)
            }
        }

        notificationManager.notify(notificationId, notification)
        Log.d("BGSV", "Alarm $hora is ringing")

        //notificationIdCounter++

        // Pass the updated notificationIdCounter back to the AlarmasForegroundService
        /*val serviceIntent = Intent(context, AlarmasForegroundService::class.java)
        serviceIntent.putExtra("notificationIdCounter", notificationId)
        context.startService(serviceIntent)*/
    }

}

