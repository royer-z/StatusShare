package com.example.statusshare

class AllHomeContactHelper {
    var name : String? = null
    var firstName : String? = null
    var lastName : String? = null
    var status : String? = null
    var statusColor : Int? = null

    constructor()

    constructor(name : String, firstName: String, lastName:String, status:String){
        this .name = firstName + lastName
        this.firstName = firstName
        this.lastName = lastName
        this.status = status
    }



}