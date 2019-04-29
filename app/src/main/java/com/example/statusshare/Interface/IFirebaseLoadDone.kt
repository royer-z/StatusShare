package com.example.statusshare.Interface

interface IFirebaseLoadDone {
    fun onFirebaseLoadUserDone(lstEmail:List<String>)
    fun onFirebaseLoadFail(message:String)
}