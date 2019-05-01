package com.example.statusshare


import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.statusshare.Service.ViewHolders.HomepageViewHolder
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.*

//import androidx.fragment.app.Fragment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [HomePage.OnFragmentInteractionListener] interface
 * to handle interaction events.
 *
 */
class HomePage : Fragment() {

    lateinit var homeAdapter: FirebaseRecyclerAdapter<AllHomeContactHelper, HomepageViewHolder>
    lateinit var mRecyclerView: RecyclerView
    lateinit var mDatabase: DatabaseReference
    internal var items:MutableList<AllHomeContactHelper> = ArrayList()



    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.home_page, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fun checkPermission() {
            // Permissions check
            if (ContextCompat.checkSelfPermission(context!!, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                // Permission NOT granted
                // Request permission
                ActivityCompat.requestPermissions(activity!!, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), HomePage.LOCATION_PERMISSION_REQUEST_CODE)
            }
            else if (ContextCompat.checkSelfPermission(context!!, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // Permission IS granted
            }
        }

        checkPermission()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mDatabase = FirebaseDatabase.getInstance().getReference("Registration q")
        mRecyclerView = view.findViewById(R.id.recycler_home_page)

        //init view
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.setLayoutManager(LinearLayoutManager(context))


        loadContactList()
        setContactList()



    }

    private fun setContactList() {

        val query = FirebaseDatabase.getInstance().reference.child("Registration q").child("Accept List Family")
        val options = FirebaseRecyclerOptions.Builder<AllHomeContactHelper>()
            .setQuery(query,AllHomeContactHelper::class.java)
            .build()

        homeAdapter = object:FirebaseRecyclerAdapter<AllHomeContactHelper, HomepageViewHolder>(options)
        {
            override fun onCreateViewHolder(parent: ViewGroup, p1: Int): HomepageViewHolder {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.contacts_contact,parent,false)
                return HomepageViewHolder(itemView)
            }

            override fun onBindViewHolder(holder: HomepageViewHolder, position: Int, model: AllHomeContactHelper) {
                val name = model.firstName + " " + model.lastName
                holder.name.text = name
                holder.status.text = model.status
            }
        }

        mRecyclerView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
        mRecyclerView.adapter = homeAdapter

    }

    private fun loadContactList() {
        val db = FirebaseDatabase.getInstance().reference.child("Registration q").child("Accept List Family")
        db.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("Error",p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                for(itemInView in p0.children){
                    val item = itemInView.getValue(AllHomeContactHelper::class.java)
                    items.add(item!!)
                }
            }
        })
    }

    override fun onStart() {
        if(homeAdapter != null)
            homeAdapter.startListening()
        super.onStart()
    }

}
