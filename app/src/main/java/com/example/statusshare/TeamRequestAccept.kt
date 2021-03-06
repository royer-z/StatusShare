package com.example.statusshare

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.example.statusshare.Interface.IFirebaseLoadDone
import com.example.statusshare.Service.ViewHolders.FriendRequestViewHolder
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_contacts.*
import kotlinx.android.synthetic.main.activity_friend_request.*





class TeamRequestAccept : AppCompatActivity() {


    lateinit var adapter : FirebaseRecyclerAdapter<AllUsersHelper, FriendRequestViewHolder>
    private val firebaseUser = FirebaseAuth.getInstance().currentUser


    private val uid =  firebaseUser?.uid.toString()
    private val currentUserReference  = FirebaseDatabase.getInstance().getReference("Registration q")
        .child(uid)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_request)

        recycler_team_list.setHasFixedSize(true)
        recycler_team_list.setLayoutManager(LinearLayoutManager(this))

        loadTeamRequestList()

        adapter.startListening()


    }

    private fun loadTeamRequestList() {
        val query = FirebaseDatabase.getInstance().getReference("Registration q")
            .child(uid)
            .child("Request")

        val options = FirebaseRecyclerOptions.Builder<AllUsersHelper>()
            .setQuery(query, AllUsersHelper::class.java)
            .build()

        adapter = object:FirebaseRecyclerAdapter<AllUsersHelper, FriendRequestViewHolder>(options){

            override fun onCreateViewHolder(p0: ViewGroup, p1: Int): FriendRequestViewHolder {
                val itemView = LayoutInflater.from(p0.context)
                    .inflate(R.layout.layout_friend_request, p0, false)
                return FriendRequestViewHolder(itemView)
            }

            override fun onBindViewHolder(holder: FriendRequestViewHolder, position: Int, model: AllUsersHelper) {
                holder.txt_user_email.text = model.email

                holder.decline_btn.setOnClickListener {
                    //Delete the friend request
                    deleteTeamRequest(model,true)
                }

                holder.accept_btn.setOnClickListener {
                    //Accept friend request
                    deleteTeamRequest(model,false)
                    addToTeamAcceptList(model) //Add your friend to your friend list   GOOOOOOODDDDD
                    addUserToTeamContact(model) //Add YOU to your friend's friend list
                    finish()
                }
            }

        }

        recycler_team_list.adapter = adapter
    }

    private fun addUserToTeamContact(model: AllUsersHelper) {
        val acceptList = FirebaseDatabase.getInstance()
            .getReference("Registration q")
            .child(model.uid.toString())
            .child("Accept List Team")

        currentUserReference.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                val userInformation  = p0.getValue(AllUsersHelper::class.java)
                acceptList.child(firebaseUser?.uid.toString()).setValue(userInformation)

            }
        })
    }

    private fun addToTeamAcceptList(model: AllUsersHelper) {
        val acceptList = FirebaseDatabase.getInstance()
            .getReference("Registration q")
            .child(firebaseUser?.uid.toString())
            .child("Accept List Team")

        acceptList.child(model.uid.toString()).setValue(model)

    }

    private fun deleteTeamRequest(model: AllUsersHelper, isShowMessage: Boolean) {
        val friendRequest = FirebaseDatabase.getInstance()
            .getReference("Registration q")
            .child(firebaseUser?.uid.toString())
            .child("Request")

        friendRequest.child(model.uid!!.toString()).removeValue()
            .addOnSuccessListener {
                if(isShowMessage)
                    Toast.makeText(this@TeamRequestAccept,"REMOVED",Toast.LENGTH_SHORT).show()
            }
        finish()
    }

    override fun onDestroy() {
        recycler_family_list.adapter = null;
        super.onDestroy()
    }

    override fun onStop() {
        if(adapter != null){
            adapter!!.stopListening()
        }
        super.onStop()
    }
}
