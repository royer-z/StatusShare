package com.example.statusshare

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button

class LoginPage: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val registerButton = findViewById<Button>(R.id.loginRegisterButton)

        registerButton.setOnClickListener {

            val intent = Intent(this, Registration::class.java)
            startActivity(intent)
        }

    }
}