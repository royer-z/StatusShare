package com.example.statusshare.Service

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.widget.Toast
import com.example.statusshare.AllUsersHelper
import com.example.statusshare.R
import com.example.statusshare.Utils.Common
import com.example.statusshare.Utils.NotificationHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.Random

class MyFirebaseMessagingService:FirebaseMessagingService() {

    override fun onNewToken(s: String?) {
        super.onNewToken(s)
        val user = FirebaseAuth.getInstance().currentUser
        if(user != null){
            val tokens = FirebaseDatabase.getInstance().getReference("Tokens")
            tokens.child(user!!.uid).setValue(s)
        }
    }

    override fun onMessageReceived(p0: RemoteMessage?) {
        super.onMessageReceived(p0)
        if(p0!!.data != null)
        {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                sendNotificationWithChannel(p0)
            else
               sendNotification(p0)
            Toast.makeText(this@MyFirebaseMessagingService,p0.data.toString(),Toast.LENGTH_LONG).show()
            addRequestToUserInformation(p0.data)
        }
    }

    private fun sendNotification(p0: RemoteMessage) {
        val data = p0.data
        val title = "Friend Request"
        val content = "New friend request from "+ data["FROM_EMAIL"]!!

        val builder = NotificationCompat.Builder(this,"")
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(title)
            .setContentText(content)
            .setAutoCancel(false)

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(Random().nextInt(),builder.build())
    }

    private fun sendNotificationWithChannel(p0: RemoteMessage) {
        val data = p0.data
        val title = "Friend Request"
        val content = "New friend request from "+ data["FROM_EMAIL"]!!

        val helper = NotificationHelper(this)
        val builder:Notification.Builder = helper.getRealtimeTrackingNotification(title,content)

        helper.getManager().notify(Random().nextInt(),builder.build())
    }

    private fun addRequestToUserInformation(data: Map<String, String>) {

        //Pending Friend Request
        val friend_request = FirebaseDatabase.getInstance()
            .getReference("Registration q")
            .child(data ["TO_UID"].toString())
            .child("Friend_Request")

        val user = AllUsersHelper(data["FROM_UID"]!!, data["FROM_EMAIL"]!!)
        friend_request.child(user.uid!!).setValue(user)
    }


}