package com.example.statusshare


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.statusshare.Service.ViewHolders.AllViewHolder
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_contacts.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

class ContactActivity : Fragment() {

    lateinit var adapter : FirebaseRecyclerAdapter<AllUsersHelper, AllViewHolder>
    private val firebaseUser = FirebaseAuth.getInstance().currentUser
    private val uid : String = firebaseUser?.uid.toString()
    lateinit var teamAdapter : FirebaseRecyclerAdapter<AllUsersHelper, AllViewHolder>


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.activity_contacts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val familyAddTextView: View = view.findViewById(R.id.contactsFamilyAddTextView)

        familyAddTextView.setOnClickListener(){
            val intent = Intent(getActivity(), ActivityAllPeopleDriver::class.java)
            getActivity()?.startActivity(intent)
        }

        val friendsAddTextView: View = view.findViewById(R.id.contactsFriendsAddTextView)
        friendsAddTextView.setOnClickListener(){
            //val intent = Intent(getActivity(), FriendRequest::class.java)
            //intent.putExtra("status", profileStatus.text.toString())
            //  getActivity()?.startActivity(intent)
        }

        val teamAddTextView: View = view.findViewById(R.id.contactsTeamAddTextView)
        teamAddTextView.setOnClickListener(){
            val intent = Intent(getActivity(), TeamRequest::class.java)
            getActivity()?.startActivity(intent)
        }



        val pendingFriendRequestButton : Button = view.findViewById(R.id.pendingFriendRequestsButton)

        pendingFriendRequestButton.setOnClickListener(){
            val intent = Intent(getActivity(), FriendRequest::class.java)
            getActivity()?.startActivity(intent)
        }




        //Recycler_friend_list is the recycler view
        recycler_family_list.layoutManager = LinearLayoutManager(activity)
        loadFriendList()
        adapter.startListening()

        //Recycler team_list
        recycler_team_list.layoutManager = LinearLayoutManager(activity)
        loadTeamList()
        teamAdapter.startListening()


    }

    private fun loadFriendList() {
        val query = FirebaseDatabase.getInstance().getReference("Registration q")
            .child(uid)
            .child("Accept List Family")

        val options = FirebaseRecyclerOptions.Builder<AllUsersHelper>()
            .setQuery(query, AllUsersHelper::class.java)
            .build()
        adapter = object : FirebaseRecyclerAdapter<AllUsersHelper, AllViewHolder>(options){

            override fun onCreateViewHolder(p0: ViewGroup, p1: Int): AllViewHolder {
                val itemView = LayoutInflater.from(p0.context)
                    .inflate(R.layout.all_users_display_layout, p0, false)
                return AllViewHolder(itemView)
            }
            override fun onBindViewHolder(holder: AllViewHolder, position: Int, model: AllUsersHelper) {
                holder.user_name.text = model.firstName
                holder.user_status.text = model.lastName
                holder.email_field.text = model.email


            }
        }

            recycler_family_list.adapter = adapter
    }

    private fun loadTeamList() {
        val query = FirebaseDatabase.getInstance().getReference("Registration q")
            .child(uid)
            .child("Accept List Team")

        val options = FirebaseRecyclerOptions.Builder<AllUsersHelper>()
            .setQuery(query, AllUsersHelper::class.java)
            .build()
        teamAdapter = object : FirebaseRecyclerAdapter<AllUsersHelper, AllViewHolder>(options){

            override fun onCreateViewHolder(p0: ViewGroup, p1: Int): AllViewHolder {
                val itemView = LayoutInflater.from(p0.context)
                    .inflate(R.layout.all_users_display_layout, p0, false)
                return AllViewHolder(itemView)
            }
            override fun onBindViewHolder(holder: AllViewHolder, position: Int, model: AllUsersHelper) {
                holder.user_name.text = model.firstName
                holder.user_status.text = model.lastName
                holder.email_field.text = model.email


            }
        }

        recycler_team_list.adapter = teamAdapter
    }


    override fun onStop() {
        if(adapter != null)
            adapter.startListening()
        super.onStop()

    }


}


