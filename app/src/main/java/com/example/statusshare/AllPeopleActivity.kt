package com.example.statusshare

import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.statusshare.Interface.IRecyclerItemClickListener
import com.example.statusshare.Model.User
import com.example.statusshare.Utils.Common
import com.example.statusshare.ViewHolder.IFirebaseLoadDone
import com.example.statusshare.ViewHolder.UserViewHolder
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mancj.materialsearchbar.MaterialSearchBar
import kotlinx.android.synthetic.main.activity_all_people.*
import java.lang.StringBuilder


class AllPeopleActivity : AppCompatActivity(), IFirebaseLoadDone {

    var adapter:FirebaseRecyclerAdapter<User,UserViewHolder>? = null
    var searchAdapter:FirebaseRecyclerAdapter<User,UserViewHolder>? = null

    lateinit var iFirebaseLoadDone: IFirebaseLoadDone
    var suggestList:List<String> = ArrayList()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_people)

        //Init View
        material_search_bar.setCardViewElevation(10)
        material_search_bar.addTextChangeListener(object:TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val suggest = ArrayList<String>()
                for(search in suggestList)
                    if(search.toLowerCase().contentEquals(material_search_bar.text.toLowerCase()))
                        suggest.add(search)
                material_search_bar.lastSuggestions = (suggest)
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
        material_search_bar.setOnSearchActionListener(object:MaterialSearchBar.OnSearchActionListener{
            override fun onButtonClicked(buttonCode: Int) {

            }

            override fun onSearchStateChanged(enabled: Boolean) {
                if(!enabled) {
                    //Close seacrch -> return default
                    if(adapter != null)
                        recycler_all_people.adapter = adapter
                }
            }


            override fun onSearchConfirmed(text: CharSequence?) {
                startSearch(text.toString())
            }

        })

        recycler_all_people.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        recycler_all_people.layoutManager = layoutManager
        recycler_all_people.addItemDecoration(DividerItemDecoration(this,layoutManager.orientation))

        iFirebaseLoadDone = this

        loadUserList()
        loadSearchDate()
    }

    private fun startSearch(search_string:String) {
        val query = FirebaseDatabase.getInstance().reference.child("Registration q").child("email")
            .orderByChild("Registration q")
            .startAt(search_string)

        val options = FirebaseRecyclerOptions.Builder<User>()
            .setQuery(query,User::class.java)
            .build()

        searchAdapter = object:FirebaseRecyclerAdapter<User, UserViewHolder>(options) {
            override fun onCreateViewHolder(p0: ViewGroup, p1: Int): UserViewHolder {
                val itemView = LayoutInflater.from(p0.context)
                    .inflate(R.layout.layout_user,p0,false)
                return UserViewHolder(itemView)
            }

            override fun onBindViewHolder(holder: UserViewHolder, position: Int, model: User) {
                if(model.email.equals(Common.loggedUser!!.email))
                {
                    holder.txt_user_email.text = model.email
                    holder.txt_user_email.setTypeface(holder.txt_user_email.typeface,Typeface.ITALIC)
                }
                else {
                    holder.txt_user_email.text = StringBuilder(model.email!!)
                }
                //Event
                holder.setClick(object:IRecyclerItemClickListener{
                    override fun onItemClickListener(view: View, position: Int) {
                        //
                    }
                })
            }

        }
        searchAdapter!!.startListening()
        recycler_all_people.adapter = searchAdapter
    }

    private fun loadSearchDate() {
        val lstUserEmail = ArrayList<String>()
        val userList = FirebaseDatabase.getInstance().reference.child("email")

        userList.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                iFirebaseLoadDone.onFirebaseLoadFailed(p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                for(userSnapShot in  p0.children) {
                    val user = userSnapShot.getValue(User::class.java)
                    lstUserEmail.add(user!!.email!!)
                }
                iFirebaseLoadDone.onFirebaseLoadUserDone(lstUserEmail)
            }

        })
    }

    private fun loadUserList() {
        val query = FirebaseDatabase.getInstance().getReference(Common.USER_INFORMATION)

        val options = FirebaseRecyclerOptions.Builder<User>()
            .setQuery(query,User::class.java)
            .build()

        adapter = object:FirebaseRecyclerAdapter<User, UserViewHolder>(options) {
            override fun onCreateViewHolder(p0: ViewGroup, p1: Int): UserViewHolder {
                val itemView = LayoutInflater.from(p0.context)
                    .inflate(R.layout.layout_user,p0,false)
                return UserViewHolder(itemView)
            }

            override fun onBindViewHolder(holder: UserViewHolder, position: Int, model: User) {
                if(model.email.equals(Common.loggedUser!!.email))
                {
                    holder.txt_user_email.text = StringBuilder(model.email!!).append(" (me) ")
                    holder.txt_user_email.setTypeface(holder.txt_user_email.typeface,Typeface.ITALIC)
                }
                else {
                    holder.txt_user_email.text = StringBuilder(model.email!!)
                }
                //Event
                holder.setClick(object:IRecyclerItemClickListener{
                    override fun onItemClickListener(view: View, position: Int) {
                            //later Implementation
                    }
                })
            }

        }
        adapter!!.startListening()
        recycler_all_people.adapter = adapter
    }

    override fun onStop() {
        if(adapter != null)
            adapter!!.stopListening()
        if(searchAdapter!=null)
            searchAdapter!!.stopListening()

        super.onStop()
    }


    override fun onFirebaseLoadUserDone(lstEmail : List<String>) {
        material_search_bar.lastSuggestions = lstEmail
    }

    override fun onFirebaseLoadFailed(message: String){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }


}