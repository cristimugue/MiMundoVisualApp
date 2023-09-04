@file:Suppress("OverrideDeprecatedMigration", "OverrideDeprecatedMigration")

package com.cristina.mimundovisual

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class MainActivity : AppCompatActivity() {

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 225
    }
    //val AUTHORITY="${BuildConfig.APPLICATION_ID}.provider"

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val permissionCheck =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
        val continuar: Button = findViewById(R.id.pulsador)
        continuar.setOnClickListener {

            val intent = Intent(this@MainActivity, IdentificacionActivity::class.java)
            startActivity(intent)

        }
        //val pdf:Button=findViewById(R.id.botonguia)
        /*pdf.setOnClickListener{
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Permission not granted, request it
                val STORAGE_PERMISSION_REQUEST_CODE=1005
                requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    STORAGE_PERMISSION_REQUEST_CODE
                )
            } else {
                // Permission already granted, proceed with PDF operation
                guiadeusuario()
            }
        }*/

    }
    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setMessage("¿Salir de la aplicación?")
            .setCancelable(false)
            .setPositiveButton("Si") { dialog, whichButton ->
                finishAffinity() //Sale de la aplicación.
            }
            .setNegativeButton("Cancelar") { dialog, whichButton ->

            }
            .show()
    }

   /* fun guiadeusuario() {

        val pdfname="usersguide.pdf"
        //val directorio=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val directorio=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path
        val pdf = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), pdfname)
        val pdfUri = FileProvider.getUriForFile(
            this,
            "com.example.tfgkotlin.provider",
            pdf
        )

        try {

            val inputStream = assets.open(pdfname)
            val outputStream = FileOutputStream(pdf)
            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }
            outputStream.close()
            inputStream.close()
        } catch (e:IOException){
            e.printStackTrace()
        }

        val uri = FileProvider.getUriForFile(this, AUTHORITY, pdf)
        Toast.makeText(this, "GOT URI $uri", Toast.LENGTH_LONG).show()

        // intent para abrir el pdf
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/pdf")
        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {

            Toast.makeText(this, "No PDF viewer found", Toast.LENGTH_SHORT).show()
        }
    }*/


}
/*override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)
    } */