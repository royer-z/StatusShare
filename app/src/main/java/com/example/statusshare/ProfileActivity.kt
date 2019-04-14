package com.example.statusshare

import android.content.Context
import android.net.Uri
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import android.widget.Toast
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_profile.*
import android.util.Log
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

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

    lateinit var locationMapFragment : SupportMapFragment
    lateinit var locationGoogleMap : GoogleMap

    lateinit var destinationMapFragment : SupportMapFragment
    lateinit var destinationGoogleMap : GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        locationMapFragment = supportFragmentManager.findFragmentById(R.id.profileLocationMap) as SupportMapFragment
        locationMapFragment.getMapAsync(OnMapReadyCallback {
            locationGoogleMap = it
            // locationGoogleMap.isMyLocationEnabled = true

            val defaultLocation = LatLng(40.743920, -74.178079)
            val defaultLocationTitle = "NJIT Library"
            locationGoogleMap.addMarker(MarkerOptions().position(defaultLocation).title(defaultLocationTitle))
            locationGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15f))
        })

        destinationMapFragment = supportFragmentManager.findFragmentById(R.id.profileDestinationMap) as SupportMapFragment
        destinationMapFragment.getMapAsync(OnMapReadyCallback {
            destinationGoogleMap = it

            val defaultDestination = LatLng(40.743920, -74.178079)
            val defaultDestinationTitle = "NJIT Library"
            destinationGoogleMap.addMarker(MarkerOptions().position(defaultDestination).title(defaultDestinationTitle))
            destinationGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(defaultDestination, 15f))
        })
      
        //colorStatus pic
        val colorStatusPic = findViewById<ImageView>(R.id.profileAvailabilityColor)
      
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


                val statusColorNum = dataSnapshot!!.child("colorStatus").value.toString()


                Log.d("STATUS NUM!!", "${statusColorNum}")
                if(statusColorNum == "0"){
                    //colorStatusPic.setImageResource(R.drawable.availability_color_green)
                    colorStatusPic.setImageDrawable(getResources().getDrawable(R.drawable.availability_color_green));
                    Log.d("STATUS", " available!")
                }
                if(statusColorNum == "1"){
                    //colorStatusPic.setImageResource(R.drawable.availability_color_orange)
                    colorStatusPic.setImageDrawable(getResources().getDrawable(R.drawable.availability_color_orange));
                    Log.d("STATUS", " awayyy!!!!")
                }
                if(statusColorNum=="2"){
                    //colorStatusPic.setImageResource(R.drawable.availability_color_red)
                    colorStatusPic.setImageDrawable(getResources().getDrawable(R.drawable.availability_color_red));
                    Log.d("STATUS", " busy!!!!")
                }
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