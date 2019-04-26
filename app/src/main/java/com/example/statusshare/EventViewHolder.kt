package com.example.statusshare

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView


class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val eventTitle: TextView
    private val eventAddress: TextView
    private val eventDate: TextView
    private val eventTime: TextView
    private val eventDescription: TextView

    init {
        eventTitle = itemView.findViewById(R.id.eventName) as TextView
        eventAddress = itemView.findViewById(R.id.eventAddress) as TextView
        eventDate = itemView.findViewById(R.id.eventDate) as TextView
        eventTime = itemView.findViewById(R.id.eventTime) as TextView
        eventDescription = itemView.findViewById(R.id.eventDescription) as TextView

    }
    fun setModel(model: EventItemModel?) {
        if (model == null || model!!.eventTitle == null) return
        itemView.tag = model.eventId
        eventTitle.text = model.eventTitle
        eventAddress.text = model.eventAddress
        eventDate.text = model.eventDate
        eventTime.text = model.eventTime
        eventDescription.text = model.eventDesciption
    }
}