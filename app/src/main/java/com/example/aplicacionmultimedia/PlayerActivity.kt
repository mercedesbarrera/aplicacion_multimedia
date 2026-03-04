package com.example.aplicacionmultimedia



import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import java.io.File

class PlayerActivity: AppCompatActivity() {
    //Variables
    //Motor de reproducción de video moderno de Android
    private lateinit var player:ExoPlayer
    //Componente visual que muestra el video en pantalla y los controles(play, pausa,etc)
    private lateinit var playerView: PlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        playerView=findViewById(R.id.playerView)
        //Recibe el dato enviado desde MainActivitu
        //intent.putExtra("videoPath", it.absolutePath)
        val videoPath=intent.getStringExtra("videoPath")
        //Crear el reproductor
        //Se crea una instancia de ExoPlayer
        player=ExoPlayer.Builder(this).build()
        //Conectar PlayerView con el reproductor
        playerView.player=player
        //Crear el MediaItem
        //Se crea un File con la ruta recibida que se convierta a Uri y se crea un mediaItem
        //Objeyo que representa el contenido multimedia
        val mediaItem= MediaItem.fromUri(Uri.fromFile(File(videoPath)))
        //Preparar y reproducir
        player.setMediaItem(mediaItem)//carga el video
        player.prepare()//prepara buffers y decodificadores
        player.play()//inicia la reproduccion
    }
    //Liberar recursos
    override fun onDestroy() {
        super.onDestroy()
        player.release()//libera memoria, decodificadores y recursos de audio/video
        //si este paso no se hace la app puede tener fugas de memoria
    }

}