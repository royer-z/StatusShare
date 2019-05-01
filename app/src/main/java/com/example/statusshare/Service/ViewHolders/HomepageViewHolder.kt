package com.example.statusshare.Service.ViewHolders

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.example.statusshare.Interface.IRecyclerItemClickListener
import com.example.statusshare.R

class HomepageViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{

    //Model
    var name : TextView
    var status : TextView
    lateinit var iRecyclerItemClickListener: IRecyclerItemClickListener

    init {
        name  = itemView.findViewById(R.id.contactsContactName)
        status = itemView.findViewById(R.id.contactsContactStatus)
    }

    fun setClick(iRecyclerItemClickListener: IRecyclerItemClickListener){
        this.iRecyclerItemClickListener = iRecyclerItemClickListener
        itemView.setOnClickListener(this)
    }

    override fun onClick(p0:View?){
        iRecyclerItemClickListener.onItemClickListener(p0!!,adapterPosition)
    }

}