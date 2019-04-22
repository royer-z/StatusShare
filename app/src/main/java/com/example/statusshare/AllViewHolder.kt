package com.example.statusshare

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.example.statusshare.R

class AllViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {

    //Model

    var user_name:TextView
    var user_status:TextView
    var email_field:TextView

    init{
        user_name = itemView.findViewById(R.id.user_name_field)
        user_status = itemView.findViewById(R.id.user_status_field)
        email_field = itemView.findViewById(R.id.email_holder)
    }

}