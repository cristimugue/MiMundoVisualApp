package com.cristina.mimundovisual

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class AlarmasForegroundService: Service() {
    private val refreshInterval:Long = 30 * 60 * 1000 // 30 minutes
    private lateinit var db: DatabaseReference
    private var isRunning = false
    private val handler = Handler()
    private lateinit var refreshRunnable: Runnable
    private var notificationIdCounter = 0
    private lateinit var usuario:String
    private lateinit var listadia:String

override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    createNotificationChannel()
    showNotification()
    if (intent!=null) {
        val extras: Bundle = intent.extras!!
        usuario = extras.getString("usuario").toString()
        listadia = extras.getString("lista").toString()
        Log.d("PRUEBAS","$usuario")
        Log.d("PRUEBAS","$listadia")
    }
    if (!isRunning) {
        isRunning = true
        startRefreshHandler()
        Log.d("BGSV","Background service started")
        //Primera iteración
        fetchHourFromFirebase()
    }
    return START_STICKY
}
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
override fun onDestroy() {
    super.onDestroy()
    isRunning = false
    handler.removeCallbacks(refreshRunnable)
    stopForeground(true)
}
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "ForegroundServiceChannel"
            val channelName = "Foreground Service Channel"
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
    private fun showNotification() {
        val notificationIntent = Intent(this, MenuHijoActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        val notification: Notification = NotificationCompat.Builder(this, "ForegroundServiceChannel")
            .setContentTitle("Mi Mundo Visual se está ejecutando en segundo plano")
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(1, notification)
    }

    private fun startRefreshHandler() {
        refreshRunnable = Runnable {
            Log.d("BGSV","Inside refreshRunnable")
            fetchHourFromFirebase()
            // Programar el siguiente refresh
            handler.postDelayed(refreshRunnable, refreshInterval)
        }
        // Programar el refresh inicial
        handler.postDelayed(refreshRunnable, refreshInterval)
    }

    private fun fetchHourFromFirebase() {
        db= FirebaseDatabase.getInstance().reference.child("Mi-Mundo-Visual").child(usuario).child(listadia)
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                removeAlarms()
                for (childSnapshot in dataSnapshot.children) {
                    val hour = childSnapshot.child("hora").getValue(String::class.java).toString()
                    hour.let {
                        // If the hour is retrieved successfully, set the alarm
                        val titulo=childSnapshot.child("titulo").getValue(String::class.java).toString()
                        val desc=childSnapshot.child("descripcion").getValue(String::class.java).toString()
                        val key=childSnapshot.child("key").getValue(String::class.java).toString()
                        val hecho=childSnapshot.child("hecho").getValue(Boolean::class.java)
                        if (hecho != true) {
                            //maybe hecho=false too??
                            setAlarm(this@AlarmasForegroundService,it,titulo,desc,hecho!!,key)
                        }
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("BGSV", "Failed to fetch hour from database")

            }
        })
    }

    private fun setAlarm(context:Context,hour: String, titulo: String, descripcion:String,hecho:Boolean,key:String) {

        Log.d("BGSV", "Setting alarm")

        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val calendar = Calendar.getInstance()

        // Get the current time
        val currentTime = Calendar.getInstance()

        // Parse the hour value from Firebase
        val alarmTime = sdf.parse(hour)

        // Set the hour value from Firebase to the calendar instance
        calendar.time = alarmTime!!

        // Subtract 15 minutes from the alarm time
        //calendar.add(Calendar.MINUTE, -15)
        calendar.set(
            currentTime.get(Calendar.YEAR),
            currentTime.get(Calendar.MONTH),
            currentTime.get(Calendar.DAY_OF_MONTH)
        )

        if (calendar.timeInMillis >= currentTime.timeInMillis){
            // Update the calendar instance with the correct year, month, and day

            val intent = Intent(context, MyAlarm::class.java)
            intent.putExtra("titulo",titulo)
            intent.putExtra("descripcion",descripcion)
            intent.putExtra("hecho",hecho)
            intent.putExtra("key",key)
            intent.putExtra("hora",hour)
            intent.putExtra("user",usuario)
            intent.putExtra("texto","Es hora de")
            intent.putExtra("counter",notificationIdCounter)
            val pendingIntent = PendingIntent.getBroadcast(context, notificationIdCounter, intent, 0)
            notificationIdCounter++

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val timeInMillis = calendar.timeInMillis - currentTime.timeInMillis
            // If the alarm time has already passed, set the alarm to ring immediately
            // by using a negative or zero value for the time delay
            val delay = if (timeInMillis <= 0) 0 else timeInMillis
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delay, pendingIntent)
            //Toast.makeText(this, "Alarm is set at $hour", Toast.LENGTH_SHORT).show()
            Log.d("BGSV", "Alarm set at $hour")

        }else{
            val intent = Intent(context, MyAlarm::class.java)
            intent.putExtra("titulo", titulo)
            intent.putExtra("descripcion", descripcion)
            intent.putExtra("hecho", hecho)
            intent.putExtra("key", key)
            intent.putExtra("hora", hour)
            intent.putExtra("user", usuario)
            intent.putExtra("counter",notificationIdCounter)
            intent.putExtra("texto","No olvides")
            context.sendBroadcast(intent)
        }
    }

    private fun removeAlarms(){
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Iterate through all the previous alarms and cancel them
        for (i in 0 until notificationIdCounter) {
            val intent = Intent(this, MyAlarm::class.java)
            val pendingIntent = PendingIntent.getBroadcast(this, i, intent, PendingIntent.FLAG_IMMUTABLE)

            pendingIntent?.let {
                alarmManager.cancel(it)
                it.cancel()
            }
        }

        // Reset the notificationIdCounter
        notificationIdCounter = 0
        Log.d("BGSV", "Alarms removed")
    }
}