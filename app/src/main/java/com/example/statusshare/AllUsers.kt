package com.example.statusshare

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_all_users.*


class AllUsers : AppCompatActivity() {


    internal var items:MutableList<AllUsersHelper> = ArrayList()
    private  lateinit  var adapter : FirebaseRecyclerAdapter<AllUsersHelper, AllViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_users)

        //init view
        all_users_list.setHasFixedSize(true)
        all_users_list.layoutManager = LinearLayoutManager(this)

        //retrieve data
        retrieveData()

        //setData
        setData()

    }

    private fun setData() {
        val query = FirebaseDatabase.getInstance().reference.child("Registration q")
        val options = FirebaseRecyclerOptions.Builder<AllUsersHelper>()
            .setQuery(query,AllUsersHelper::class.java)
            .build()

        adapter = object:FirebaseRecyclerAdapter<AllUsersHelper,AllViewHolder>(options)
        {
            override fun onCreateViewHolder(parent: ViewGroup, position: Int): AllViewHolder{
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.all_users_display_layout,parent,false)
                return AllViewHolder(itemView)
            }

            override fun onBindViewHolder(holder: AllViewHolder, position: Int, model: AllUsersHelper) {
                holder.user_name.text = model.firstName
                holder.user_status.text = model.lastName
                holder.email_field.text = model.email
            }

        }

        all_users_list.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))
        all_users_list.adapter = adapter

    }

    private fun retrieveData(){

        val db = FirebaseDatabase.getInstance().reference.child("Registration q")
        db.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                Log.d("Error",p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                for(itemInView in p0.children){
                    val item = itemInView.getValue(AllUsersHelper::class.java)

                    items.add(item!!)
                }
            }
        })
    }

    override fun onStart() {
        if(adapter != null)
            adapter.startListening()
        super.onStart()
    }

    override fun onStop() {
        if(adapter != null)
            adapter.startListening()
        super.onStop()
    }
}
