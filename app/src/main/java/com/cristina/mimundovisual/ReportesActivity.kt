package com.cristina.mimundovisual

import android.Manifest
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import android.graphics.pdf.PdfDocument
import java.io.File
import java.io.FileOutputStream
import android.provider.MediaStore
import android.text.*
import android.text.style.StyleSpan
import android.util.Log
import java.io.FileInputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
class ReportesActivity : AppCompatActivity() {

    private lateinit var calendarView: CalendarView
    private lateinit var recyclerView: RecyclerView
    private lateinit var reportsAdapter: ReportsAdapter
    private lateinit var tvCount: TextView
    private lateinit var tvHechoCount: TextView
    private lateinit var tvNoHechoCount: TextView
    private val dbRef:DatabaseReference= FirebaseDatabase.getInstance().reference
    private lateinit var user: String
    private var selectedDate: String = ""
    private lateinit var lay:LinearLayout
    private val REQUEST_CODE=1232
    var milista = mutableListOf<Tarea>()
    var documento=PdfDocument()
    lateinit var botonpdf:Button
    private var pageCounter=1
    private lateinit var diaescrito:String
    private lateinit var dia:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_reportes)

        calendarView = findViewById(R.id.calendarView)
        recyclerView = findViewById(R.id.recyclerViewReports)
        tvCount = findViewById(R.id.tvactivitiesday)
        tvHechoCount = findViewById(R.id.tvactivitiesdone)
        tvNoHechoCount = findViewById(R.id.tvactivitiesnotdone)
        lay=findViewById(R.id.linearlayoutmenureport)
        user=intent.getStringExtra("account").toString()

        recyclerView.layoutManager = LinearLayoutManager(this)
        reportsAdapter = ReportsAdapter(emptyList())
        recyclerView.adapter = reportsAdapter


        botonpdf=findViewById(R.id.botonpdf)

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate = formatDate(year, month, dayOfMonth)
            diaescrito=formatDate2(year, month, dayOfMonth)
            val formattedDate = selectedDate.substring(2)
            dia=formattedDate
            loadReports(formattedDate)
        }

        botonpdf.setOnClickListener{
            if (checkPermissions()){
                //val daylist=listofDays(selectedDate.substring(2))
                val pdfContent = StringBuilder() //texto de los días iniciado
                var counter=0

                val firebaseRef = dbRef.child("Mi-Mundo-Visual").child(user).child("Tareas${dia}")
                Log.d("PDF","Fetching day $diaescrito from Firebase")
                var fetchedData:String=""
                // Add a ValueEventListener to fetch data from Firebase
                firebaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    var fetched=false
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {

                            val data = buildString {
                                for (childSnapshot in dataSnapshot.children) {
                                    val tarea = childSnapshot.getValue(Tarea::class.java)
                                    tarea?.let {
                                        //append(" - ${tarea.titulo},  Hora: ${tarea.hora} \n  Hecho: ${if (tarea.hecho) "Sí" else "No"}, Estado: ${if (tarea.hecho) "${tarea.estado}" else "-"}\n")
                                        val boldTitulo = tarea.titulo // Text you want to make bold

                                        // Create a SpannableString with the title in bold
                                        val spannableString = SpannableString("- $boldTitulo,  Hora: ${tarea.hora} \n  Hecho: ${if (tarea.hecho) "Sí" else "No"}, Estado: ${if (tarea.hecho) "${tarea.estado}" else "-"}\n")
                                        if (boldTitulo != null) {
                                            spannableString.setSpan(
                                                StyleSpan(Typeface.BOLD),
                                                2, // Start index of the text you want to make bold
                                                2 + boldTitulo.length, // End index (exclusive) of the text you want to make bold
                                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                                            )
                                        }

                                        // Append the SpannableString
                                        append(spannableString)
                                    }
                                }
                            }
                            fetchedData=data
                            Log.d("PDF", " $diaescrito $fetchedData")
                            counter++
                        } else {
                            // Child doesn't exist, handle accordingly
                            Log.d("PDF", "No data for $diaescrito")
                            //pdfContent.append("No hay tareas para el día $diaescrito\n")
                            counter++
                        }
                        Log.d("PDF","$counter")
                        //pdfContent.append("Tareas del día $diaescrito:\n")
                        if (fetchedData.isNotEmpty()) {
                            pdfContent.append("$fetchedData\n")
                        } else {
                            pdfContent.append("No se han programado tareas en este día\n")
                        }

                        generarPdf(selectedDate,pdfContent.toString())


                    }

                    override fun onCancelled(databaseError: DatabaseError) {

                        Log.d("PDF","NO HAY NA")

                    }
                })
                Log.d("PDF"," dentro de callback ")
               /* for(day in daylist){
                    val firebaseRef = dbRef.child("Mi-Mundo-Visual").child(user).child("Tareas$day")
                    Log.d("PDF","Fetching day $day from Firebase")
                    var fetchedData:String=""
                    // Add a ValueEventListener to fetch data from Firebase
                    firebaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        var fetched=false
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists()) {

                                val data = buildString {
                                    for (childSnapshot in dataSnapshot.children) {
                                        val tarea = childSnapshot.getValue(Tarea::class.java)
                                        tarea?.let {
                                            append("Tarea: ${tarea.titulo},  Hora: ${tarea.hora},  Estado: ${if (tarea.hecho) "Hecho" else "No Hecho"}\n")
                                        }
                                    }
                                }
                                fetchedData=data
                                Log.d("PDF", " $day $fetchedData")
                                counter++
                            } else {
                                // Child doesn't exist, handle accordingly
                                Log.d("PDF", "No data for $day")
                                pdfContent.append("No hay tareas para el día $day\n")
                                counter++
                            }
                            Log.d("PDF","$counter")
                            pdfContent.append("Tareas del día $day:\n")
                            if (fetchedData.isNotEmpty()) {
                                pdfContent.append("$fetchedData\n")
                            } else {
                                pdfContent.append("No hay tareas en este día\n")
                            }
                            if (counter == daylist.size) {
                                // Call generarPdf here, after all tasks are completed
                                generarPdf(selectedDate,pdfContent.toString())
                            }

                        }

                        override fun onCancelled(databaseError: DatabaseError) {

                            Log.d("PDF","NO HAY NA")

                        }
                    })
                    Log.d("PDF"," dentro de callback ")

                }*/

            }
            else{
                askPermissions()
            }
        }
    }

    private fun loadReports(date: String) {
        dbRef.child("Mi-Mundo-Visual").child(user).child("Tareas$date").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val tareasList = mutableListOf<Tarea>()
                var hechoCount = 0
                var noHechoCount = 0
                var count=0

                for (childSnapshot in dataSnapshot.children) {
                    val tarea = childSnapshot.getValue(Tarea::class.java)
                    tarea?.let {
                        tareasList.add(tarea)
                        if (tarea.hecho) {
                            hechoCount++
                            count++
                        } else {
                            noHechoCount++
                            count++
                        }
                    }
                }

                reportsAdapter.updateData(tareasList)
                if (tareasList.isEmpty()) {
                    reportsAdapter.clearData()
                }

                botonpdf.visibility=View.VISIBLE
                lay.visibility=View.VISIBLE
                tvHechoCount.text = "Tareas realizadas: $hechoCount"
                tvNoHechoCount.text = "Tareas pendientes: $noHechoCount"
                tvCount.text= "Total de tareas: $count"
                milista=tareasList

            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    private fun formatDate(year: Int, month: Int, dayOfMonth: Int): String {
        val monthStr = if (month + 1 < 10) "0${month + 1}" else "${month + 1}"
        val dayStr = if (dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"
        return "$year$monthStr$dayStr"
    }
    private fun formatDate2(year: Int, month: Int, dayOfMonth: Int): String {
        val monthStr = if (month + 1 < 10) "0${month + 1}" else "${month + 1}"
        val dayStr = if (dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"
        return "$dayStr/$monthStr/$year"
    }

    private fun listofDays(date:String):List<String>{
        val daysToGenerate = mutableListOf<String>()
        val sdf = SimpleDateFormat("yyMMdd", Locale.getDefault())
        val startDate = sdf.parse(date)

        for (i in 0..6) {
            val calendar = Calendar.getInstance()
            calendar.time = startDate
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            daysToGenerate.add(sdf.format(calendar.time))
        }

        return daysToGenerate
    }


    private fun generarPdf(date:String,content:String){
        //Log.d("PDF", "generarPdf function called for date: $date")
        val file = File(getExternalFilesDir(null), "reporte_$date.pdf")
        documento = PdfDocument()

        var paint= Paint()
        var titulo = TextPaint()
        paint.textSize = 12f
        var contenido = TextPaint()

        var pageInfo=PdfDocument.PageInfo.Builder(595,842,pageCounter).create()
        var pagina=documento.startPage(pageInfo)
        var canvas = pagina.canvas

        var bitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
        var bitmapEscala = Bitmap.createScaledBitmap(bitmap, 80,80, false)
        canvas.drawBitmap(bitmapEscala, 350f, 70f, paint)

        titulo.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD))
        titulo.textSize = 22f
        canvas.drawText("Mi Mundo Visual ", 100f, 100f, titulo)
        titulo.textSize = 18f
        canvas.drawText("Histórico diario: $diaescrito", 100f, 130f, titulo)

        contenido.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC))
        contenido.textSize = 12f

        val width:Int=contenido.measureText(content).toInt()

        canvas.save()

        val textoTareasLy=StaticLayout.Builder.obtain(
            content,0,content.length,contenido,width)
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(10f,2.0f)
            .build()

        canvas.translate(50f, 200f)
        textoTareasLy.draw(canvas)
        canvas.restore()

        documento.finishPage(pagina)
        Log.d("PDF", "Page finished")
        try {
            FileOutputStream(file).use{outputStream->documento.writeTo(outputStream)}
        }catch (e: Exception) {
            e.printStackTrace()
        }

        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, file.name)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
        }

        val uri = contentResolver.insert(MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY), values)

        uri?.let { uri ->
            try {
                val outputStream = contentResolver.openOutputStream(uri)
                val pdfInputStream = FileInputStream(file)
                outputStream?.use { output ->
                    pdfInputStream.use { input ->
                        input.copyTo(output)
                    }
                }
                // Notify the media scanner about the new PDF
                val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                mediaScanIntent.data = uri
                sendBroadcast(mediaScanIntent)
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e("PDF","Error saving pdf: ${e.message}")
            }

        }
        documento.close()
        pageCounter++
        //Log.d("PDF", "Documento closed")

    }

    private fun checkPermissions():Boolean{
        val permission=ContextCompat.checkSelfPermission(applicationContext,READ_EXTERNAL_STORAGE)
        return permission==PackageManager.PERMISSION_GRANTED
    }

    private fun askPermissions(){
        ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),REQUEST_CODE)
    }
}
/*
class ReportesActivity : AppCompatActivity() {

    private lateinit var calendarView: CalendarView
    private lateinit var recyclerView: RecyclerView
    private lateinit var reportsAdapter: ReportsAdapter
    private lateinit var tvCount: TextView
    private lateinit var tvHechoCount: TextView
    private lateinit var tvNoHechoCount: TextView
    private val dbRef:DatabaseReference= FirebaseDatabase.getInstance().reference
    private lateinit var user: String
    private var selectedDate: String = ""
    private lateinit var lay:LinearLayout
    private val REQUEST_CODE=1232
    var milista = mutableListOf<Tarea>()
    lateinit var botonpdf:Button
    private var pageCounter=1
    private lateinit var page:PdfDocument.Page
    private lateinit var documentofinal:PdfDocument


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_reportes)

        calendarView = findViewById(R.id.calendarView)
        recyclerView = findViewById(R.id.recyclerViewReports)
        tvCount = findViewById(R.id.tvactivitiesday)
        tvHechoCount = findViewById(R.id.tvactivitiesdone)
        tvNoHechoCount = findViewById(R.id.tvactivitiesnotdone)
        lay=findViewById(R.id.linearlayoutmenureport)
        user=intent.getStringExtra("account").toString()

        recyclerView.layoutManager = LinearLayoutManager(this)
        reportsAdapter = ReportsAdapter(emptyList())
        recyclerView.adapter = reportsAdapter


        botonpdf=findViewById(R.id.botonpdf)

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate = formatDate(year, month, dayOfMonth)
            val formattedDate = selectedDate.substring(2)
            loadReports(formattedDate)
        }

        botonpdf.setOnClickListener{
            if (checkPermissions()){
                val daylist=listofDays(selectedDate.substring(2))

                var counter=0
                documentofinal=PdfDocument()
                for(day in daylist){
                    val pdfContent = StringBuilder()
                    val firebaseRef = dbRef.child("Mi-Mundo-Visual").child(user).child("Tareas$day")
                    Log.d("PDF","Fetching day $day from Firebase")

                    val pdf=PdfDocument()

                    // Add a ValueEventListener to fetch data from Firebase
                    firebaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        var fetched=false

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists()) {

                                val data = buildString {
                                    for (childSnapshot in dataSnapshot.children) {
                                        val tarea = childSnapshot.getValue(Tarea::class.java)
                                        tarea?.let {
                                            append("Tarea: ${tarea.titulo},  Hora: ${tarea.hora},  Estado: ${if (tarea.hecho) "Hecho" else "No Hecho"}\n")
                                        }
                                    }
                                }
                                fetched=true

                                pdfContent.append("Tareas del dia $day: \n")
                                pdfContent.append("$data\n")
                                counter++
                            } else {

                                //val dia=escribirdia(day)
                                pdfContent.append("Tareas del día $day:\n")
                                Log.d("PDF", "No data for $day")
                                pdfContent.append("No hay tareas para el día $day\n")
                                counter++

                            }
                            Log.d("PDF","$counter")
                            val pageInfo=PdfDocument.PageInfo.Builder(595,842,pageCounter).create()
                            val pagina=pdf.startPage(pageInfo)
                            val canvas = pagina.canvas
                            drawContent(canvas,pdfContent.toString())
                            pageCounter++

                            pdf.finishPage(pagina)
                            if (counter == daylist.size) {
                                // Call generarPdf here, after all tasks are completed
                                val dayFile = File(getExternalFilesDir(null), "reporte_$day.pdf")
                                savepdf(pdf,dayFile)
                                pdf.close()
                            }

                        }

                        override fun onCancelled(databaseError: DatabaseError) {

                            Log.d("PDF","NO HAY DATOS")

                        }
                    })
                }
            } else{
                askPermissions()
            }
        }
    }
    private fun savepdf(pdfDoc:PdfDocument,file:File){
        try {
            FileOutputStream(file).use{outputStream->pdfDoc.writeTo(outputStream)}
            Log.d("PDF", "PDF written to file")
        }catch (e: Exception) {
            e.printStackTrace()
        }

        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, file.name)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
        }

        val uri = contentResolver.insert(MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY), values)

        uri?.let { uri ->
            try {
                val outputStream = contentResolver.openOutputStream(uri)
                val pdfInputStream = FileInputStream(file)
                outputStream?.use { output ->
                    pdfInputStream.use { input ->
                        input.copyTo(output)
                    }
                }
                // Notify the media scanner about the new PDF
                val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                mediaScanIntent.data = uri
                sendBroadcast(mediaScanIntent)
                Log.d("PDF", "PDF saved in MediaStore")
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e("PDF","Error saving pdf: ${e.message}")
            }

        }
        pdfDoc.close()
    }

    private fun drawContent(canvas: Canvas, content: String) {
        val paint = Paint()
        paint.textSize = 12f
        val lineHeight = paint.fontSpacing
        val lines = content.split("\n")
        var yPos = lineHeight

        for (line in lines) {
            if (yPos + lineHeight > canvas.height) {
                // Start a new page if the text exceeds the page height
                canvas.restore()
                documentofinal.finishPage(page)
                val pageInfo = PdfDocument.PageInfo.Builder(595, 842, pageCounter).create()
                page = documentofinal.startPage(pageInfo)
                yPos = lineHeight
            }

            // Draw the current line of text
            canvas.drawText(line, 50f, yPos, paint)
            yPos += lineHeight
        }
    }

    private fun loadReports(date: String) {
        dbRef.child("Mi-Mundo-Visual").child(user).child("Tareas$date").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val tareasList = mutableListOf<Tarea>()
                var hechoCount = 0
                var noHechoCount = 0
                var count=0

                for (childSnapshot in dataSnapshot.children) {
                    val tarea = childSnapshot.getValue(Tarea::class.java)
                    tarea?.let {
                        tareasList.add(tarea)
                        if (tarea.hecho) {
                            hechoCount++
                            count++
                        } else {
                            noHechoCount++
                            count++
                        }
                    }
                }

                // Update the reports adapter with the new data
                reportsAdapter.updateData(tareasList)
                if (tareasList.isEmpty()) {
                    reportsAdapter.clearData()
                }

                // Update the counts in the UI

                botonpdf.visibility=View.VISIBLE
                lay.visibility=View.VISIBLE
                tvHechoCount.text = "Tareas realizadas: $hechoCount"
                tvNoHechoCount.text = "Tareas pendientes: $noHechoCount"
                tvCount.text= "Total de tareas: $count"
                milista=tareasList

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error case
            }
        })
    }

    private fun formatDate(year: Int, month: Int, dayOfMonth: Int): String {
        val monthStr = if (month + 1 < 10) "0${month + 1}" else "${month + 1}"
        val dayStr = if (dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"
        return "$year$monthStr$dayStr"
    }
   /* private fun escribirdia(date:String): String{
        val monthStr = if (month + 1 < 10) "0${month + 1}" else "${month + 1}"
        val dayStr = if (dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"
        return "$dayStr/$monthStr/$year"
    }*/

    private fun listofDays(date:String):List<String>{
        val daysToGenerate = mutableListOf<String>()
        val sdf = SimpleDateFormat("yyMMdd", Locale.getDefault())
        val startDate = sdf.parse(date)

        for (i in 0..6) {
            val calendar = Calendar.getInstance()
            calendar.time = startDate
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            daysToGenerate.add(sdf.format(calendar.time))
        }

        return daysToGenerate
    }


    private fun checkPermissions():Boolean{
        val permission=ContextCompat.checkSelfPermission(applicationContext,READ_EXTERNAL_STORAGE)
        return permission==PackageManager.PERMISSION_GRANTED
    }

    private fun askPermissions(){
        ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),REQUEST_CODE)
    }

}


 */