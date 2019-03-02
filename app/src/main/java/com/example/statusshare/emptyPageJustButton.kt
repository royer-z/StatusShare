package com.example.statusshare

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.widget.Button

lateinit var toolbar: ActionBar

class emptyPageJustButton: AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bottomnavbar)

        toolbar = supportActionBar!!
        val bottomNavigation: BottomNavigationView = findViewById(R.id.navigation_bar)
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {

            R.id.nav_home_button -> {

                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_profile_button-> {

                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_share_button-> {

                return@OnNavigationItemSelectedListener true
            }

            R.id.nav_add_friend_button-> {

                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_share_location-> {

                return@OnNavigationItemSelectedListener true
            }

        }
        false
    }

}