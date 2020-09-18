package com.shourya.a7minuteworkout

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val videoPath = "android.resource://$packageName/raw/splash2"
        video_view.setVideoPath(videoPath)
        video_view.setOnCompletionListener {
            val r = object:Runnable{
                override fun run() {
                    startActivity(Intent(this@SplashActivity,MainActivity::class.java))
                    finish()
                }
            }
            Handler().postDelayed(r,300)
        }
        video_view.start()

        skip_button.setOnClickListener {
            startActivity(Intent(this@SplashActivity,MainActivity::class.java))
            finish()
        }

    }
}