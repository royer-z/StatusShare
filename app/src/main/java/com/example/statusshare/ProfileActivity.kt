package com.example.statusshare

import android.content.Context
import android.net.Uri
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_profile.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ProfileActivity.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ProfileActivity.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class ProfileActivity : Fragment() {
    // TODO: Rename and change types of parameters

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.activity_profile, container, false)

    }

}


class whatever: AppCompatActivity(){
    var mDatabase: DatabaseReference? = null
    var mCurrentUser: FirebaseUser? = null
    //var mStorageRef:StorageReference?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mCurrentUser = FirebaseAuth.getInstance().currentUser

        var userID = mCurrentUser!!.uid

        mDatabase = FirebaseDatabase.getInstance().reference
            .child("Registration q")
            .child(userID)

        mDatabase!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var user_status = dataSnapshot!!.child("status").value
                var user_location = dataSnapshot!!.child("location").value
                var user_destination = dataSnapshot!!.child("destination").value

                profileStatus.text = user_status.toString()
                profileLocationHeading.text = user_location.toString()
                profileDestinationHeading.text = user_destination.toString()

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

        profileUpdateProfileButton.setOnClickListener {
            var intent = Intent(this, EditProfileActivity::class.java)
            intent.putExtra("status", profileStatus.text.toString())
            intent.putExtra("location", profileLocationHeading.text.toString())
            intent.putExtra("destination", profileDestinationHeading.text.toString())
            startActivity(intent)
        }
    }
}