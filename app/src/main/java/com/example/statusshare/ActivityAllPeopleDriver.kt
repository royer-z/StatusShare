package com.example.statusshare

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.widget.TextView
import kotlinx.android.synthetic.main.all_users_display_layout.view.*



class ActivityAllPeopleDriver : AppCompatActivity() {
    lateinit var mSearchText : EditText
    lateinit var mRecyclerView : RecyclerView
    lateinit var mDatabase : DatabaseReference
    lateinit var searchEmailButton : ImageButton
    lateinit var emptyView : TextView
    lateinit  var adapter : FirebaseRecyclerAdapter<AllUsersHelper, AllViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_people)

        searchEmailButton = findViewById(R.id.email_search_button)
        mSearchText = findViewById(R.id.email_search_holder)
        mRecyclerView = findViewById(R.id.recycler_search_email)
        emptyView = findViewById(R.id.empty_view)
        mDatabase = FirebaseDatabase.getInstance().getReference("Registration q")

        //init view
        mRecyclerView.setHasFixedSize(true)
        //mRecyclerView.layoutManager = LinearLayoutManager(this,LinearLayout.VERTICAL, false)
        mRecyclerView.setLayoutManager(LinearLayoutManager(this))



        mSearchText.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText = mSearchText.getText().toString().trim()

                loadFirebaseData(searchText)
                adapter.startListening()


            }
        })

    }

    private fun loadFirebaseData(searchText : String) {
            if(searchText.isEmpty()){
                mRecyclerView.adapter = adapter
            }else {


                //val query = FirebaseDatabase.getInstance().reference.child("Registration q")
                val query = mDatabase.orderByChild("email").startAt(searchText).endAt(searchText + "\uf8ff")
                val options = FirebaseRecyclerOptions.Builder<AllUsersHelper>()
                    .setQuery(query, AllUsersHelper::class.java)
                    .build()

                //Toast.makeText(this, "Enter all details", Toast.LENGTH_SHORT).show()
                adapter = object : FirebaseRecyclerAdapter<AllUsersHelper, AllViewHolder>(options) {
                    override fun onCreateViewHolder(parent: ViewGroup, position: Int): AllViewHolder {
                        val itemView = LayoutInflater.from(parent.context)
                            .inflate(R.layout.all_users_display_layout, parent, false)
                        return AllViewHolder(itemView)
                    }

                    override fun onBindViewHolder(holder: AllViewHolder, position: Int, model: AllUsersHelper) {
                        holder.user_name.text = model.firstName
                        holder.user_status.text = model.lastName
                        holder.email_field.text = model.email
                    }

                }

                mRecyclerView.adapter = adapter
            }
       // mRecyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))


    }

   /*override fun onStart() {
        if(adapter != null)
            adapter.startListening()
        super.onStart()
    }
    */
    override fun onStop() {
        if(adapter != null)
            adapter.startListening()
        super.onStop()
    }
}