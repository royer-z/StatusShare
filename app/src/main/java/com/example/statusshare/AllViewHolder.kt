package com.example.statusshare

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AllViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {
    lateinit var user_name:TextView
    lateinit var user_status:TextView

    init{
        user_name = itemView.findViewById(R.id.user_name_field)
        user_status = itemView.findViewById(R.id.user_status_field)
    }

}