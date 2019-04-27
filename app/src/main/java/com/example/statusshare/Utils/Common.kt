package com.example.statusshare.Utils

import com.example.statusshare.AllUsersHelper
import com.example.statusshare.Remote.IFCMService
import com.example.statusshare.Remote.RetroFitClient

object Common {
    val USER_INFORMATION: String = "UserInformation"
    lateinit var loggedUser : AllUsersHelper
    val TOKENS: String = "Tokens"
    val USER_UID_SAVE_KEY: String = "SAVE_KEY"

    val FROM_UID : String = "FROMUid"
    val FROM_EMAIL: String = "FromEmail"
    val TO_UID : String = "ToUid"
    val TO_EMAIL : String = "ToName"
    val FRIEND_REQUEST: String = "FriendRequest"


    val fcmService:IFCMService
    get() = RetroFitClient.getClient("https://fcm.googleapis.com/")
        .create(IFCMService::class.java)



}