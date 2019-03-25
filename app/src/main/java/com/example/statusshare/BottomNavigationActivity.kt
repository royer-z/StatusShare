package com.example.statusshare

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity

import kotlinx.android.synthetic.main.bottomnavbar.*

class BottomNavigationActivity : AppCompatActivity() {
    val manager = supportFragmentManager

    private val mOnNavigationItemSelectedListener  = BottomNavigationView.OnNavigationItemSelectedListener {item->
            when (item.itemId) {
                R.id.nav_home_button-> {
                    createHomeFragment()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_setting_button->{
                    createSettingsFragment()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_notification_button->{
                    createNotifyFragment()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_contact_button->{
                    createContactsFragment()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_profile->{
                    createMyProfileFragment()
                    return@OnNavigationItemSelectedListener true
                }
            }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bottomnavbar)

        createSettingsFragment()

        navigation_bar.setOnNavigationItemSelectedListener ( mOnNavigationItemSelectedListener )
    }

    fun createSettingsFragment(){
        val transaction = manager.beginTransaction()
        val fragment = AppSettings()
        transaction.replace(R.id.fragmentHolder,fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
    fun createNotifyFragment(){
        val transaction = manager.beginTransaction()
        val fragment = NotificationActivity()
        transaction.replace(R.id.fragmentHolder,fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
    fun createHomeFragment(){
        val transaction = manager.beginTransaction()
        val fragment = HomePage()
        transaction.replace(R.id.fragmentHolder,fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
    fun createContactsFragment(){
        val transaction = manager.beginTransaction()
        val fragment = ContactActivity()
        transaction.replace(R.id.fragmentHolder,fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
    fun createMyProfileFragment(){
        val transaction = manager.beginTransaction()
        val fragment = ProfileActivity()
        transaction.replace(R.id.fragmentHolder,fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

}