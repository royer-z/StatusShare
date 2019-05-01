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
import kotlinx.android.synthetic.main.activity_friend_request.*





class FriendRequest : AppCompatActivity(), IFirebaseLoadDone {


    lateinit var adapter : FirebaseRecyclerAdapter<AllUsersHelper, FriendRequestViewHolder>
    //var searchAdapter : FirebaseRecyclerAdapter<AllUsersHelper, FriendRequestViewHolder>
    var suggestList:List<String> = ArrayList()

    lateinit var mSearchText : EditText
    lateinit var iFirebaseLoadDone: IFirebaseLoadDone
    private val firebaseUser = FirebaseAuth.getInstance().currentUser


    private val uid =  firebaseUser?.uid.toString()
    private val currentUserReference  = FirebaseDatabase.getInstance().getReference("Registration q")
        .child(uid)

    //val currentUser = AllUsersHelper(currentUserReference.child("firstName").toString(), currentUserReference.child("lastName").toString())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_request)





        //init view
/*        material_search_bar.setCardViewElevation(10)
        material_search_bar.addTextChangeListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val suggest = ArrayList<String>()
                for (search in suggestList)
                    if(search.toLowerCase().contentEquals(material_search_bar.text.toLowerCase()))
                        suggest.add(search)
                material_search_bar.lastSuggestions = (suggest)
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
        material_search_bar.setOnSearchActionListener(object: MaterialSearchBar.OnSearchActionListener{
            override fun onButtonClicked(buttonCode: Int) {

            }

            override fun onSearchStateChanged(enabled: Boolean) {
                if(!enabled)
                {
                    //Close search
                    if(adapter!= null)
                        recycler_friend_request.adapter = adapter
                }
            }

            override fun onSearchConfirmed(text: CharSequence?) {
                startSearch(text.toString())
            }

        })*/

        recycler_friend_request.setHasFixedSize(true)
        recycler_friend_request.setLayoutManager(LinearLayoutManager(this))

        iFirebaseLoadDone = this

        loadFriendRequestList()
        //loadSearchData()

        adapter.startListening()


    }

/*    private fun startSearch(searchString: String) {
        val query = FirebaseDatabase.getInstance().getReference("Registration q")
            .child(firebaseUser?.uid.toString())
            .child("Friend_Request")

        val options = FirebaseRecyclerOptions.Builder<AllUsersHelper>()
            .setQuery(query, AllUsersHelper::class.java)
            .build()

        searchAdapter = object:FirebaseRecyclerAdapter<AllUsersHelper, FriendRequestViewHolder>(options){
            override fun onCreateViewHolder(p0: ViewGroup, p1: Int): FriendRequestViewHolder {
                val itemView = LayoutInflater.from(p0.context)
                    .inflate(R.layout.layout_friend_request, p0, false)
                return FriendRequestViewHolder(itemView)
            }

            override fun onBindViewHolder(holder: FriendRequestViewHolder, position: Int, model: AllUsersHelper) {
                holder.txt_user_email.text = model.email

                holder.decline_btn.setOnClickListener {
                    //Delete the friend request
                    deleteFriendRequest(model,true)
                }

                holder.accept_btn.setOnClickListener {
                    //Accept friend request
                    deleteFriendRequest(model,false)
                    addToAcceptList(model) //Add your friend to your friend list
                    addUserToFriendContact(model) //Add YOU to your friend's friend list
                }
            }

        }
        adapter!!.startListening()
        recycler_friend_request.adapter = adapter
    }*/

/*    private fun loadSearchData() {
        val lstUserEmail = ArrayList<String>()
        val userList = FirebaseDatabase.getInstance().getReference("Registration q")
            //.child(firebaseUser?.uid.toString())
           // .child("Friend_Request")

        userList.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                 iFirebaseLoadDone.onFirebaseLoadFail(p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                for(userSnapShot in p0.children)
                {
                    val user = userSnapShot.getValue(AllUsersHelper::class.java)
                    lstUserEmail.add(user!!.email!!)
                }
            }

        })

    }*/

    private fun loadFriendRequestList() {
        val query = FirebaseDatabase.getInstance().getReference("Registration q")
            .child(uid)
            .child("Friend_Request")

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
                deleteFriendRequest(model,true)
                }

                holder.accept_btn.setOnClickListener {
                    //Accept friend request
                    deleteFriendRequest(model,false)
                    addToAcceptList(model) //Add your friend to your friend list   GOOOOOOODDDDD
                    addUserToFriendContact(model) //Add YOU to your friend's friend list
                    finish()
                }
            }

        }

        recycler_friend_request.adapter = adapter
    }

    private fun addUserToFriendContact(model: AllUsersHelper) {
        val acceptList = FirebaseDatabase.getInstance()
            .getReference("Registration q")
            .child(model.uid.toString())
            .child("Accept List")

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

    private fun addToAcceptList(model: AllUsersHelper) {
        val acceptList = FirebaseDatabase.getInstance()
            .getReference("Registration q")
            .child(firebaseUser?.uid.toString())
            .child("Accept List")



        acceptList.child(model.uid.toString()).setValue(model)
        
    }

    private fun deleteFriendRequest(model: AllUsersHelper, isShowMessage: Boolean) {
        val friendRequest = FirebaseDatabase.getInstance()
            .getReference("Registration q")
            .child(model.uid.toString())
            .child("Friend_Request")

        friendRequest.child(model.uid!!).removeValue()
            .addOnSuccessListener {
                if(isShowMessage)
                    Toast.makeText(this@FriendRequest,"REMOVE",Toast.LENGTH_SHORT).show()
            }
    }

    override fun onFirebaseLoadUserDone(lstEmail: List<String>) {
        //material_search_bar.lastSuggestions = lstEmail
        Toast.makeText(this,"Load complete",Toast.LENGTH_SHORT).show()
    }

    override fun onFirebaseLoadFail(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    override fun onStop() {
        if(adapter != null){
            adapter!!.stopListening()
        }
        super.onStop()
    }
}
