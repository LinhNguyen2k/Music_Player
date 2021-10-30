package com.example.music_player.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.music_player.R

class Splash : AppCompatActivity() {
    lateinit var handler: Handler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar?.hide()
        requestRunTimePermission()
        if (Player.musicService != null){
            handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            },0)
        } else{
            handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            },3000)
        }

    }
    private fun requestRunTimePermission() {
        if (ActivityCompat.checkSelfPermission(applicationContext,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                13)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 13) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    13)
            }
        }
    }
}