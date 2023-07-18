package com.example.sensor_detection

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dashBtn = findViewById<Button>(R.id.dash_btn)
        val rptBtn = findViewById<Button>(R.id.rpt_btn)
        val bkBtnMenu = findViewById<ImageButton>(R.id.bk_btn_wlcm)

        dashBtn.setOnClickListener {
            val DICTIONARY = Intent(this, Dashboard::class.java)
            startActivity(DICTIONARY)
        }
        rptBtn.setOnClickListener {
            val HELP = Intent(this, Report::class.java)
            startActivity(HELP)
        }
        bkBtnMenu.setOnClickListener {
            val Menu_bkBtn = Intent(this, Welcome::class.java)
            startActivity(Menu_bkBtn)
        }

    }
}