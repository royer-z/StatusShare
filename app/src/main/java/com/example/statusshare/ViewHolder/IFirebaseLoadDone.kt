package com.example.statusshare.ViewHolder



interface IFirebaseLoadDone {
    fun onFirebaseLoadUserDone(lstEmail:List<String>)
    fun onFirebaseLoadFailed(message:String)
}