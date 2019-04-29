package com.example.statusshare.Service.ViewHolders

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.statusshare.R


class FriendRequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    var txt_user_email:TextView
    var accept_btn : ImageView
    var decline_btn : ImageView


    init {
        txt_user_email = itemView.findViewById(R.id.email_holder) as TextView
        accept_btn = itemView.findViewById(R.id.accept_btn)  as ImageView
        decline_btn = itemView.findViewById(R.id.decline_btn) as ImageView
    }

}