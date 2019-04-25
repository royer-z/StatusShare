package com.example.statusshare

class AllUsersHelper{
    //global variables
    var firstName: String? = null
    var lastName: String? = null
    var uid: String? = null
    var email: String? = null
    var acceptList:HashMap<String, AllUsersHelper>? = null

    constructor()

    constructor(firstName: String, lastName:String, email:String, uid:String){
        this.firstName = firstName
        this.lastName = lastName
        this.email = email
        this.uid = uid
        acceptList = HashMap()

    }

}