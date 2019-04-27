package com.example.statusshare

class EventItemModel {
    var eventId: String ? = null
    var eventTitle: String? = null
    var eventAddress: String? = null
    var eventDate: String? = null
    var eventTime: String? = null
    var eventDesciption: String? = null
    constructor()
    constructor(event_id: String, event_title: String, event_address: String,event_date: String,event_time: String,event_description: String) {
        this.eventId = event_id
        this.eventTitle = event_title
        this.eventAddress = event_address
        this.eventDate = event_date
        this.eventTime = event_time
        this.eventDesciption = event_description
    }
}