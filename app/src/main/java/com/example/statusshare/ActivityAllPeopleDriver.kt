package com.example.statusshare

import android.app.Notification
import android.app.NotificationChannel
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import android.widget.TextView
import com.example.statusshare.Interface.IRecyclerItemClickListener
import android.app.NotificationManager
import android.content.Context
import com.example.statusshare.Service.ViewHolders.AllViewHolder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import io.reactivex.disposables.CompositeDisposable



class ActivityAllPeopleDriver : AppCompatActivity() {

    lateinit var mSearchText: EditText
    lateinit var mRecyclerView: RecyclerView
    lateinit var mDatabase: DatabaseReference
    lateinit var searchEmailButton: ImageButton
    lateinit var emptyView: TextView
    lateinit var adapter: FirebaseRecyclerAdapter<AllUsersHelper, AllViewHolder>

    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    private var channelId = "com.example.statusshare.notificationExample"
    private var description = "TEST NOTIFICATION"

    private val firebaseUser = FirebaseAuth.getInstance().currentUser
    private val uid: String = firebaseUser?.uid.toString()
    private val currentUserReference = FirebaseDatabase.getInstance().getReference("Registration q")
        .child(uid)

    val compositeDisposable = CompositeDisposable()


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

        mSearchText.addTextChangedListener(object : TextWatcher {
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

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    }

    private fun showDialogRequest(model: AllUsersHelper) {
        val alertDialog = AlertDialog.Builder(this, R.style.MyRequestDialog)
        alertDialog.setTitle("RequestFriend")
        alertDialog.setMessage("Do you want to send a request friend to " + model.email)
        alertDialog.setIcon(R.drawable.ic_person_add_black_24dp)

        alertDialog.setNegativeButton("Cancel") { dialog: DialogInterface?, _: Int -> dialog?.dismiss() }
        alertDialog.setPositiveButton("Send") { _, _ ->
            val acceptList = FirebaseDatabase.getInstance()
                .reference.child("Registration q")
                .child("Accept List")
                .child(model.uid.toString())
                .child("email")

            //Check user friend list to make sure they are not already friends

            acceptList.orderByKey().equalTo(model.email)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0.value == null) {
                            //NOT FRIENDS
                            sendFriendRequest(model)
                        } else
                            Toast.makeText(
                                this@ActivityAllPeopleDriver,
                                "You and " + model.email + "are already friends",
                                Toast.LENGTH_LONG
                            ).show()
                    }

                })
        }

        alertDialog.show()


    }

    private fun sendFriendRequest(model: AllUsersHelper) {
        val tokens = FirebaseDatabase.getInstance().getReference("Tokens")

        tokens.orderByKey().equalTo(model.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(p0: DataSnapshot) {

                    currentUserReference.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            val userInformation = p0.getValue(AllUsersHelper::class.java)


                            //Pending Friend Request
                            val friend_request = FirebaseDatabase.getInstance()
                                .getReference("Registration q")
                                .child(model.uid.toString())
                                .child("Request")


                            friend_request.child(uid).setValue(userInformation)
                            Toast.makeText(this@ActivityAllPeopleDriver, "Request sent", Toast.LENGTH_LONG).show()

                            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                notificationChannel =
                                    NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
                                notificationChannel.enableLights(true)
                                notificationChannel.enableVibration(true)
                                notificationManager.createNotificationChannel(notificationChannel)
                            }*/
                            finish()

                        }
                    })
                }
            })


    }


    private fun loadFirebaseData(searchText: String) {
        if (searchText.isEmpty()) {
            mRecyclerView.adapter = adapter
        } else {
            val query = mDatabase.orderByChild("email").startAt(searchText).endAt(searchText + "\uf8ff")
            val options = FirebaseRecyclerOptions.Builder<AllUsersHelper>()
                .setQuery(query, AllUsersHelper::class.java)
                .build()

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

                    holder.setClick(object : IRecyclerItemClickListener {
                        override fun onItemClickListener(view: View, position: Int) {
                            Toast.makeText(this@ActivityAllPeopleDriver, "Button clicked", Toast.LENGTH_SHORT).show()
                            showDialogRequest(model)
                        }

                    })

                }

            }

            mRecyclerView.adapter = adapter

        }
    }


    override fun onStop() {
        if (adapter != null)
            adapter.startListening()
        compositeDisposable.clear()
        super.onStop()

    }
}