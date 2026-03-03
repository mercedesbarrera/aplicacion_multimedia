package com.example.aplicacionmultimedia



import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import java.io.File

class PlayerActivity: AppCompatActivity() {
    private lateinit var player:ExoPlayer
    private lateinit var playerView: PlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        playerView=findViewById(R.id.playerView)
        val videoPath=intent.getStringExtra("videoPath")

        player=ExoPlayer.Builder(this).build()
        playerView.player=player

        val mediaItem= MediaItem.fromUri(Uri.fromFile(File(videoPath)))
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }

}