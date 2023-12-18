package com.thabiso81.taskwiz.view.activities

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.thabiso81.taskwiz.databinding.ActivitySplashPageBinding

private lateinit var binding: ActivitySplashPageBinding
class SplashPageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashPageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.splashAppIcon.alpha = 0f
        binding.splashAppIcon.animate().setDuration(1600).alpha(1f).withEndAction{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }


    }
}