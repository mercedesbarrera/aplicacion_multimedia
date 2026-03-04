package com.example.aplicacionmultimedia

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.media.MediaRecorder
import android.os.Bundle
import android.view.SurfaceView
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File
import android.Manifest
import android.widget.TextView
import androidx.annotation.RequiresPermission


class MainActivity : AppCompatActivity() {
    private lateinit var surfaceView: SurfaceView
    private lateinit var btnRecord: Button
    private lateinit var btnStop: Button
    private lateinit var btnPlay: Button
    private lateinit var txtRecording: TextView

    private lateinit var cameraDevice: CameraDevice
    private lateinit var cameraCaptureSession: CameraCaptureSession
    private lateinit var mediaRecorder: MediaRecorder
    private var videoFile: File? = null


    private val cameraManager by lazy {
        getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    private val REQUES_CODE_PERMISSIONS = 100
    @RequiresPermission(Manifest.permission.CAMERA)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        surfaceView = findViewById(R.id.surfaceView)
        btnRecord = findViewById(R.id.btnRecord)
        btnStop = findViewById(R.id.btnStop)
        btnPlay = findViewById(R.id.btnPlay)

        btnRecord.setOnClickListener { startRecording() }
        btnStop.setOnClickListener { stopRecording() }
        btnPlay.setOnClickListener {
            videoFile?.let {
                val intent = Intent(this, PlayerActivity::class.java)
                intent.putExtra("videoPath", it.absolutePath)
                startActivity(intent)
            }
        }
        if (checkPermissions()) {
            openCamera()
        } else {
            requestPermissions()
        }
    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO),
            REQUES_CODE_PERMISSIONS
        )
    }


    @RequiresPermission(Manifest.permission.CAMERA)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permission: Array<out String>,
        grantResult: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permission, grantResult)
        if (requestCode == REQUES_CODE_PERMISSIONS) {
            if (grantResult.isNotEmpty() &&
                grantResult.all { it == PackageManager.PERMISSION_GRANTED }
            ) {
                openCamera()
            }
        }
    }

    @RequiresPermission(Manifest.permission.CAMERA)
    private fun openCamera() {
        val cameraId = cameraManager.cameraIdList[0]

        if (!checkPermissions()) return

        cameraManager.openCamera(cameraId, object : CameraDevice.StateCallback() {
            override fun onOpened(camera: CameraDevice) {
                cameraDevice = camera
            }

            override fun onDisconnected(camera: CameraDevice) {}
            override fun onError(camera: CameraDevice, error: Int) {}
        }, null)
    }

    private fun setupMediaRecorder() {
        mediaRecorder = MediaRecorder()
        videoFile = File(getExternalFilesDir(null),
            "video_${System.currentTimeMillis()}.mp4")

        mediaRecorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setVideoSource(MediaRecorder.VideoSource.SURFACE)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setOutputFile(videoFile!!.absolutePath)
            setVideoEncodingBitRate(10000000)
            setVideoFrameRate(30)
            setVideoSize(1280, 720)
            setVideoEncoder(MediaRecorder.VideoEncoder.H264)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            prepare()
        }
    }
    private fun startRecording() {
        if (!::cameraDevice.isInitialized) return
        if (!surfaceView.holder.surface.isValid) return

        setupMediaRecorder()

        val recorderSurface = mediaRecorder.surface
        val previewSurface = surfaceView.holder.surface
        val surfaces = listOf(previewSurface, recorderSurface)

        val requestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD)
        requestBuilder.addTarget(previewSurface)
        requestBuilder.addTarget(recorderSurface)

        cameraDevice.createCaptureSession(surfaces, object : CameraCaptureSession.StateCallback() {
            override fun onConfigured(session: CameraCaptureSession) {
                cameraCaptureSession = session
                session.setRepeatingRequest(requestBuilder.build(), null, null)
                mediaRecorder.start()
            }

            override fun onConfigureFailed(session: CameraCaptureSession) {}
        }, null)
    }

    private fun stopRecording() {
        try {
            mediaRecorder.stop()
            mediaRecorder.reset()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}


