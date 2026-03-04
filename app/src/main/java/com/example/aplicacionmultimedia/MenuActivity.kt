package com.example.aplicacionmultimedia

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen


class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        //llamar al splash
        installSplashScreen()
        //tiempo de ejecucion
        Thread.sleep(5000)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)
        //Iniciar variable boton
        val btnMenu = findViewById<Button>(R.id.btnMenu)//busca el componente Button con id "btnMenu"
        //Botón para ir a la segunda pantalla MainActivity
        btnMenu.setOnClickListener{
            val intentMainActivity= Intent(this, MainActivity::class.java)

            startActivity(intentMainActivity)
        }
    }
}