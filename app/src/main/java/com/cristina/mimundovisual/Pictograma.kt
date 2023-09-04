package com.cristina.mimundovisual

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class Pictograma{
    companion object{
        fun getpicto(value: String):Int{
            var id=0
            when(value){
                "Almorzar"->
                    id=R.drawable.almorzar
                "Desayunar"->
                    id=R.drawable.desayunar1
                "Merendar"->
                    id=R.drawable.merendar
                "Cenar"->
                    id=R.drawable.cenar
                "Jugar"->
                    id=R.drawable.jugar
                "Jugar al ordenador"->
                    id=R.drawable.jugar_con_el_ordenador
                "Ver la tele"->
                    id=R.drawable.ver_la_television
                "Nadar"->
                    id=R.drawable.nadar
                "Correr"->
                    id=R.drawable.correr_3
                "Andar"->
                    id=R.drawable.andar
                "Escuchar música"->
                    id=R.drawable.musica_4
                "Hacer deporte"->
                    id=R.drawable.deporte
                "Pasear"->
                    id=R.drawable.pasear
                "Leer"->
                    id=R.drawable.leer
                "Dibujar"->
                    id=R.drawable.dibujar
                "Beber"->
                    id=R.drawable.beber
                "Visitar a"->
                    id=R.drawable.visitar_1
                "Ir al colegio"->
                    id=R.drawable.ir_al_colegio
                "Ir a casa"->
                    id=R.drawable.casa_1
                "Ir de viaje"->
                    id=R.drawable.viajar_5
                "Ir de excursión"->
                    id=R.drawable.ir_de_excursion_1
                "Vacaciones"->
                    id=R.drawable.vacaciones
                "Ir a la playa"->
                    id=R.drawable.playapng
                "Ir en autobús"->
                    id=R.drawable.viajar_4
                "Ir en coche"->
                    id=R.drawable.viajar
                "Ir en barco"->
                    id=R.drawable.viajar_3
                "Ir en avión"->
                    id=R.drawable.viajar_2
                "Ir en tren"->
                    id=R.drawable.viajar_1
                "Viajar"->
                    id=R.drawable.viajar_5
                "Ir al cine"->
                    id=R.drawable.cine
                "Ir al parque"->
                    id=R.drawable.parque
                "Ir al campo"->
                    id=R.drawable.campo
                "Clase de educación física"->
                    id=R.drawable.clase_de_educ_fis
                "Clase de idiomas"->
                    id=R.drawable.clase_de_idiomas
                "Clase de lengua"->
                    id=R.drawable.clase_de_lengua
                "Clase de matemáticas"->
                    id=R.drawable.clase_de_mates
                "Clase de música"->
                    id=R.drawable.clase_de_musica
                "Clase de plástica"->
                    id=R.drawable.clase_de_plastica
                "Aprender"->
                    id=R.drawable.aprender
                "Estudiar"->
                    id=R.drawable.estudiar
                "Exámen"->
                    id=R.drawable.examen
                "Hacer la tarea"->
                    id=R.drawable.deberes
                "Ir al baño"->
                    id=R.drawable.bano
                "Levantarse"->
                    id=R.drawable.levantar
                "Vestirse"->
                    id=R.drawable.vestir
                "Lavarse la cara"->
                    id=R.drawable.lavarcara
                "Lavarse los dientes"->
                    id=R.drawable.cepillar_los_dientes
                "Lavarse las manos"->
                    id=R.drawable.lavarmanos
                "Cortarse las uñas"->
                    id=R.drawable.cortarunas
                "Cortarse el pelo"->
                    id=R.drawable.cortarpelo
                "Ducharse"->
                    id=R.drawable.duchar
                "Bañarse"->
                    id=R.drawable.banarse_4
                "Dormir"->
                    id=R.drawable.dormir
                "Dormir la siesta"->
                    id=R.drawable.siesta
                "Ponerse el pijama"->
                    id=R.drawable.pijama
                "Irse a la cama"->
                    id=R.drawable.acostar
                "Limpiar"->
                    id=R.drawable.limpiar
                "Poner la mesa"->
                    id=R.drawable.ponermesa
                "Recoger la mesa"->
                    id=R.drawable.quitarmesa
                "Cocinar"->
                    id=R.drawable.cocinar
                "Barrer"->
                    id=R.drawable.barrer
                "Fregar"->
                    id=R.drawable.fregar
                "Hacer la cama"->
                    id=R.drawable.hacer_la_cama
                "Recoger"->
                    id=R.drawable.recoger_los_juguetes
                "Ir al médico"->
                    id=R.drawable.medico
                "Ir a votar"->
                    id=R.drawable.votar
                "Ayudar a"->
                    id=R.drawable.ayudar
                "Llamar a"->
                    id=R.drawable.llamar_6
                "Coger el autobús"->
                    id=R.drawable.subirse_al_autobus_1

            }
            return id

        }

    }

}