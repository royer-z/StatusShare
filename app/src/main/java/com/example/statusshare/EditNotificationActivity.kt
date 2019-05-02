package com.example.statusshare

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

//import androidx.appcompat.app.AppCompatActivity

class EditNotificationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_notification)

        val cancelButton = findViewById<Button>(R.id.editNotificationCancelButton)

        cancelButton.setOnClickListener {
            finish()
        }

    }
}
