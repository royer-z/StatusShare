package com.example.statusshare.Utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.support.annotation.RequiresApi
import com.example.statusshare.R

class NotificationHelper (base:Context):ContextWrapper(base){

    companion object {
        private val FRIEND_CHANNEL_ID = "com.example.statusshare"
        private val FRIEND_CHANNEL_NAME = "statusshare"
    }

    private var manager: NotificationManager? = null
    init {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createChannel(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(defaultUri: Uri?) {

        val friendChannel = NotificationChannel(FRIEND_CHANNEL_ID,FRIEND_CHANNEL_NAME,NotificationManager.IMPORTANCE_DEFAULT)
        friendChannel.enableLights(true)
        friendChannel.enableVibration(true)
        friendChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
            .build()

        friendChannel.setSound(defaultUri!!,audioAttributes)
        getManager()!!.createNotificationChannel(friendChannel)


    }

    public fun getManager(): NotificationManager {
        if(manager == null)
            manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return manager!!
    }

    fun getRealtimeTrackingNotification(title:String, content:String):Notification.Builder{
        return Notification.Builder(applicationContext, FRIEND_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(title)
            .setContentText(content)
            .setAutoCancel(false)
    }

}