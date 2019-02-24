package com.example.statusshare

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mainEnterButton = findViewById(R.id.mainEnterButton) as Button

        mainEnterButton.setOnClickListener {
            setContentView(R.layout.activity_login)
        }
    }
}
