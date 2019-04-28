package com.example.statusshare

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_profile.*
import android.util.Log
import android.widget.Button
import android.widget.Toast
import java.util.*
import com.example.statusshare.Model.Event
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.picasso.Picasso.*
import java.io.IOException

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
class ProfileActivity : Fragment(), OnMapReadyCallback {

    private lateinit var currentUserId : String
    private lateinit var currentUserData: DatabaseReference
    private var userSwitchState : Any? =  null

    lateinit var profileView : View

    // TODO: Rename and change types of parameters
    var mDatabase: DatabaseReference? = null
    var mCurrentUser: FirebaseUser? = null

    private lateinit var locationMap : GoogleMap
    private lateinit var locationMapView : MapView

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    private fun toast(message: String) =
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()

    private fun getAddress(latLng: LatLng): String {
        val geocoder = Geocoder(context, Locale.US)
        val addresses: List<Address>?
        var addressText = ""

        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

            if (addresses.isNotEmpty()) {
                val address = addresses[0]
                addressText = address.getAddressLine(0).toString()
            }
        } catch (e: IOException) {
            toast("Could not get address from $latLng")
        }

        return addressText
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission IS granted
                    setUpLocationMap()
                } else {
                    // Permission NOT granted
                }
                return
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun setUpLocationMap() {
        locationMap.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                val titleStr = getAddress(currentLatLng)
                locationMap.uiSettings.isZoomControlsEnabled = true
                locationMap.addMarker(MarkerOptions().position(currentLatLng).title(titleStr))
                locationMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
            }
        }
    }

    private fun checkPermission() {
        // Permissions check
        if (ActivityCompat.checkSelfPermission(context!!, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            // Permission NOT granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Need to show explanation
                toast("Location permission is needed to show your current location on a map.")
                // Request permission again
                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            }
            else {
                // Request permission
                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            }
        }
        else if (ActivityCompat.checkSelfPermission(context!!, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Permission IS granted
            setUpLocationMap()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
  
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        profileView = inflater!!.inflate(R.layout.activity_profile, container, false)
        return profileView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        locationMapView = profileView.findViewById(R.id.profileLocationMapView)
        locationMapView.onCreate(null)
        locationMapView.onResume()
        locationMapView.getMapAsync(this)

        val events = ArrayList<Event>()
        events.add(Event("Kitty Party", "Home", "today", "2:00", "zxcz"))
        events.add(Event("LOLI Party", "Home", "today", "6:00", "dixzcsc"))
        events.add(Event("Zoo Party", "Work Office", "today", "12:00", "zxc"))
        events.add(Event("Sleep Over", "NJIT", "today", "9:00", "disc"))

        recyclerView1.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = eventAdapter(events)
        }

        val colorStatusPic = getView()?.findViewById<ImageView>(R.id.profileAvailabilityColor)

        mCurrentUser = FirebaseAuth.getInstance().currentUser

        var userID = mCurrentUser!!.uid

        mDatabase = FirebaseDatabase.getInstance().reference
            .child("Registration q")
            .child(userID)

        mDatabase!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var user_status = dataSnapshot!!.child("status").value

                profileStatus.text = user_status.toString()

                val statusColorNum = dataSnapshot!!.child("colorStatus").value.toString()

                Log.d("STATUS NUM!!", "${statusColorNum}")
                if (statusColorNum == "0") {
                    //colorStatusPic.setImageResource(R.drawable.availability_color_green)
                    colorStatusPic?.setImageDrawable(getResources().getDrawable(R.drawable.availability_color_green));
                    //Log.d("STATUS", " available!")
                }
                if (statusColorNum == "1") {
                    //colorStatusPic.setImageResource(R.drawable.availability_color_orange)
                    colorStatusPic?.setImageDrawable(getResources().getDrawable(R.drawable.availability_color_yellow));
                    //Log.d("STATUS", " awayyy!!!!")
                }
                if (statusColorNum == "2") {
                    //colorStatusPic.setImageResource(R.drawable.availability_color_red)
                    colorStatusPic?.setImageDrawable(getResources().getDrawable(R.drawable.availability_color_orange));
                    //Log.d("STATUS", " busy!!!!")
                }

                if (statusColorNum == "3") {
                    //colorStatusPic.setImageResource(R.drawable.availability_color_red)
                    colorStatusPic?.setImageDrawable(getResources().getDrawable(R.drawable.availability_color_red));
                    Log.d("STATUS", " busy!!!!")
                }

                var image = dataSnapshot!!.child("image").value.toString()
                var thumbnail = dataSnapshot!!.child("thumb_image").value

                if (!image!!.equals("null")) {
                    //with(applicationContext)
                    with(getActivity()?.getApplicationContext())
                        .load(image)
                        .placeholder(R.drawable.default_profile_image)
                        .into(profileProfileImage)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })

//        profileUpdateProfileButton.setOnClickListener {
//            var intent = Intent(this, EditProfileActivity::class.java)
//            intent.putExtra("status", profileStatus.text.toString())
//            intent.putExtra("location", profileLocationHeading.text.toString())
//            intent.putExtra("destination", profileDestinationHeading.text.toString())
//            startActivity(intent)
//        }

        val btn: Button = view.findViewById(R.id.profileUpdateProfileButton)

        btn.setOnClickListener {
            val intent = Intent(getActivity(), EditProfileActivity::class.java)
            intent.putExtra("status", profileStatus.text.toString())
            getActivity()?.startActivity(intent)
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)
    }

    override fun onMapReady(map : GoogleMap) {
        MapsInitializer.initialize(context)
        locationMap = map

        // Retrieve switch state from DB
        currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
        currentUserData = FirebaseDatabase.getInstance().reference.child("Registration q").child(currentUserId)
        currentUserData.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                userSwitchState = p0.child("switchState").value

                if (userSwitchState == "off") {

                }
                else {
                    checkPermission()
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                toast("Could not retrieve switch state.")
            }
        })
    }

    /*override fun onStart () {
        super.onStart()
        locationMapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        locationMapView?.onResume()
    }
    override fun onPause() {
        super.onPause()
        locationMapView?.onPause()
    }
    override fun onStop() {
        super.onStop()
        locationMapView?.onStop()
    }
    override fun onDestroy() {
        super.onDestroy()
        locationMapView?.onDestroy()
    }
    override fun onSaveInstanceState(outState : Bundle) {
        super.onSaveInstanceState(outState)
        locationMapView?.onSaveInstanceState(outState)
    }
    override fun onLowMemory() {
        super.onLowMemory()
        locationMapView?.onLowMemory()
    }*/
}


class whatever : AppCompatActivity() {

    var mDatabase: DatabaseReference? = null
    var mCurrentUser: FirebaseUser? = null
    //var mStorageRef:StorageReference?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
      
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

                profileStatus.text = user_status.toString()


                val statusColorNum = dataSnapshot!!.child("colorStatus").value.toString()


                Log.d("STATUS NUM!!", "${statusColorNum}")
                if (statusColorNum == "0") {
                    //colorStatusPic.setImageResource(R.drawable.availability_color_green)
                    colorStatusPic.setImageDrawable(getResources().getDrawable(R.drawable.availability_color_green));
                    //Log.d("STATUS", " available!")
                }
                if (statusColorNum == "1") {
                    //colorStatusPic.setImageResource(R.drawable.availability_color_orange)
                    colorStatusPic.setImageDrawable(getResources().getDrawable(R.drawable.availability_color_yellow));
                    //Log.d("STATUS", " awayyy!!!!")
                }
                if (statusColorNum == "2") {
                    //colorStatusPic.setImageResource(R.drawable.availability_color_red)
                    colorStatusPic.setImageDrawable(getResources().getDrawable(R.drawable.availability_color_orange));
                    //Log.d("STATUS", " busy!!!!")
                }

                if (statusColorNum == "3") {
                    //colorStatusPic.setImageResource(R.drawable.availability_color_red)
                    colorStatusPic.setImageDrawable(getResources().getDrawable(R.drawable.availability_color_red));
                    Log.d("STATUS", " busy!!!!")
                }

                var image = dataSnapshot!!.child("image").value.toString()
                var thumbnail = dataSnapshot!!.child("thumb_image").value

                if (!image!!.equals("null")) {
                    with(applicationContext)
                        .load(image)
                        .placeholder(R.drawable.default_profile_image)
                        .into(profileProfileImage)
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

        profileUpdateProfileButton.setOnClickListener {
            var intent = Intent(this, EditProfileActivity::class.java)
            intent.putExtra("status", profileStatus.text.toString())
            startActivity(intent)
        }
    }
}