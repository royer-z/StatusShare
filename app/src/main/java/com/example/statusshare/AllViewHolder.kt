package com.example.statusshare

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.example.statusshare.Interface.IRecyclerItemClickListener


class AllViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{

    //Model

    var user_name:TextView
    var user_status:TextView
    var email_field:TextView
    lateinit var iRecyclerItemClickListener: IRecyclerItemClickListener

    init{
        user_name = itemView.findViewById(R.id.user_name_field)
        user_status = itemView.findViewById(R.id.user_status_field)
        email_field = itemView.findViewById(R.id.email_holder)
    }

    fun setClick(iRecyclerItemClickListener: IRecyclerItemClickListener){
        this.iRecyclerItemClickListener = iRecyclerItemClickListener
        itemView.setOnClickListener(this)
    }

    override fun onClick(p0:View?){
        iRecyclerItemClickListener.onItemClickListener(p0!!,adapterPosition)
    }

}