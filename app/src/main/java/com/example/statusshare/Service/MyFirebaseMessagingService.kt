package com.example.statusshare.Service

import android.os.Build
import com.example.statusshare.AllUsersHelper
import com.example.statusshare.Utils.Common
import com.example.statusshare.Utils.NotificationHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

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
               // sendNotification(p0)
            addRequestToUserInformation(p0.data)
        }
    }

    private fun sendNotificationWithChannel(p0: RemoteMessage) {
        val data = p0.data
        val title = "Friend Request"
        val content = "New friend request from "+ data[Common.FROM_EMAIL]!!

        val helper: NotificationHelper

    }

    private fun addRequestToUserInformation(data: Map<String, String>) {
        //Pending Friend Request
        val friend_request = FirebaseDatabase.getInstance()
            .getReference(Common.USER_INFORMATION)
            .child(data[Common.TO_UID]!!)
            .child(Common.FRIEND_REQUEST)

        val user = AllUsersHelper(data[Common.FROM_UID]!!, data[Common.FROM_EMAIL]!!)
        friend_request.child(user.uid!!).setValue(user)
    }
}