package com.sumit.musicplayer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager

class SpllashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN )
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spllash_screen)
        Handler().postDelayed({
            startActivity(Intent(applicationContext , MainActivity:: class.java))
        }, 3000)

    }
}