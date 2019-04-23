package com.example.statusshare

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.statusshare.Model.Event

 class eventAdapter(val eventList: ArrayList<Event>): RecyclerView.Adapter<eventAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.eventName?.text = eventList[position].eventName
        holder?.eventAddress?.text = eventList[position].eventAddress
        holder?.eventDate?.text = eventList[position].eventDate
        holder?.eventTime?.text = eventList[position].eventTime
        holder?.eventDescription?.text = eventList[position].eventDescription

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.event_row_list, parent, false)
        return ViewHolder(v);
    }

    override fun getItemCount(): Int {
        return eventList.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val eventName = itemView.findViewById<TextView>(R.id.eventName)
        val eventAddress = itemView.findViewById<TextView>(R.id.eventAddress)
        val eventDate = itemView.findViewById<TextView>(R.id.eventDate)
        val eventTime = itemView.findViewById<TextView>(R.id.eventTime)
        val eventDescription = itemView.findViewById<TextView>(R.id.eventDescription)

    }

}