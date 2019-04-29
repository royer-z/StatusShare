package com.example.statusshare

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
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
class ProfileActivity : Fragment() {

    private var currentUserId : String = FirebaseAuth.getInstance().currentUser!!.uid
    private var currentUserData: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Registration q").child(currentUserId)

    private var userSwitchState : Any? =  null
    private lateinit var userLiveLocation : LatLng
    private lateinit var userCustomLocation : String
    private lateinit var userCustomDestination : String

    lateinit var profileView : View

    // TODO: Rename and change types of parameters
    var mDatabase: DatabaseReference? = null
    var mCurrentUser: FirebaseUser? = null

    private lateinit var locationMap : GoogleMap
    private lateinit var locationMapView : MapView
    private lateinit var destinationMap : GoogleMap
    private lateinit var destinationMapView : MapView

    private fun toast(message: String) =
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()

    private fun getAddressFromLL(latLng: LatLng): String {
        val gCoder = Geocoder(context, Locale.US)
        val addresses: List<Address>?
        var addressText = ""

        try {
            addresses = gCoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

            if (addresses.isNotEmpty()) {
                val address = addresses[0]
                addressText = address.getAddressLine(0).toString()
            }
        } catch (e: IOException) {
            toast("Could not get text from $latLng")
        }

        return addressText
    }

    private fun getLLFromText(text: String): LatLng {
        val gCoder = Geocoder(context, Locale.US)
        var addresses: List<Address>? = null
        var addressLL = LatLng(0.0, 0.0)

        try {
            addresses = gCoder.getFromLocationName(text, 1)
        } catch (e: IOException) {
            toast("Could not get LatLng from $text")
        }

        if (addresses != null && addresses.isNotEmpty()) {
            addressLL = LatLng(addresses[0].latitude, addresses[0].longitude)
        }

        return addressLL
    }

    private fun setUpLocationMap() {
        if (userSwitchState == "on") {
            val currentLatLng = LatLng(userLiveLocation.latitude, userLiveLocation.longitude)
            val titleStr = getAddressFromLL(currentLatLng)
            locationMap.uiSettings.isZoomControlsEnabled = true
            locationMap.addMarker(MarkerOptions().position(currentLatLng).title(titleStr))
            locationMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
        }
        else {
            val customLatLng = getLLFromText(userCustomLocation!!)
            locationMap.uiSettings.isZoomControlsEnabled = true
            locationMap.addMarker(MarkerOptions().position(customLatLng).title(userCustomLocation))
            locationMap.animateCamera(CameraUpdateFactory.newLatLngZoom(customLatLng, 15f))
        }
    }

    private fun setUpDestinationMap() {
        val customLatLng = getLLFromText(userCustomDestination!!)
        destinationMap.uiSettings.isZoomControlsEnabled = true
        destinationMap.addMarker(MarkerOptions().position(customLatLng).title(userCustomDestination))
        destinationMap.animateCamera(CameraUpdateFactory.newLatLngZoom(customLatLng, 15f))
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

        currentUserData.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                userSwitchState = p0.child("switchState").value
                val tempLatitude = p0.child("liveLocation").child("latitude").value
                val tempLongitude = p0.child("liveLocation").child("longitude").value
                if (tempLatitude != null && tempLongitude != null) {
                    userLiveLocation = LatLng(tempLatitude as Double, tempLongitude as Double)
                }
                else {
                    userLiveLocation = LatLng(0.0, 0.0)
                }
                userCustomLocation = p0.child("customLocation").value as String
                userCustomDestination = p0.child("customDestination").value as String
            }

            override fun onCancelled(p0: DatabaseError) {
                toast("Could not retrieve switch state.")
            }
        })

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
        val profileImage = getView()?.findViewById<ImageView>(R.id.profileProfileImage)

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
                    profileImage?.setBackgroundColor(Color.parseColor("#17A42F"))
                    //Log.d("STATUS", " available!")
                }
                if (statusColorNum == "1") {
                    //colorStatusPic.setImageResource(R.drawable.availability_color_orange)
                    colorStatusPic?.setImageDrawable(getResources().getDrawable(R.drawable.availability_color_yellow));
                    profileImage?.setBackgroundColor(Color.parseColor("#FFFF00"))

                    //Log.d("STATUS", " awayyy!!!!")
                }
                if (statusColorNum == "2") {
                    //colorStatusPic.setImageResource(R.drawable.availability_color_red)
                    colorStatusPic?.setImageDrawable(getResources().getDrawable(R.drawable.availability_color_orange));
                    //profileImage?.setBackgroundColor(Color.parseColor("#FF9800"))
                    profileImage?.setBackgroundColor(Color.parseColor("#FFA500"))
                    //Log.d("STATUS", " busy!!!!")
                }

                if (statusColorNum == "3") {
                    //colorStatusPic.setImageResource(R.drawable.availability_color_red)
                    colorStatusPic?.setImageDrawable(getResources().getDrawable(R.drawable.availability_color_red));
                    //profileImage?.setBackgroundColor(Color.parseColor("#FF0000"))
                    profileImage?.setBackgroundColor(Color.RED)
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

//        profileUpdateProfileTextView.setOnClickListener {
//            var intent = Intent(this, EditProfileActivity::class.java)
//            intent.putExtra("status", profileStatus.text.toString())
//            intent.putExtra("location", profileLocationHeading.text.toString())
//            intent.putExtra("destination", profileDestinationHeading.text.toString())
//            startActivity(intent)
//        }

        val updateProfileTextView: View = view.findViewById(R.id.profileUpdateProfileTextView)

        updateProfileTextView.setOnClickListener {
            val intent = Intent(getActivity(), EditProfileActivity::class.java)
            intent.putExtra("status", profileStatus.text.toString())
            getActivity()?.startActivity(intent)
        }

        locationMapView = profileView.findViewById(R.id.profileLocationMapView)
        locationMapView.onCreate(null)
        locationMapView.onResume()
        locationMapView.getMapAsync(OnMapReadyCallback {
            MapsInitializer.initialize(context)
            locationMap = it
            setUpLocationMap()
        })

        destinationMapView = profileView.findViewById(R.id.profileDestinationMapView)
        destinationMapView.onCreate(null)
        destinationMapView.onResume()
        destinationMapView.getMapAsync(OnMapReadyCallback {
            MapsInitializer.initialize(context)
            destinationMap = it
            setUpDestinationMap()
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
        val profileImage = findViewById<ImageView>(R.id.profileProfileImage)
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
                    profileImage?.setBackgroundColor(Color.parseColor("#17A42F"))
                    //Log.d("STATUS", " available!")
                }
                if (statusColorNum == "1") {
                    //colorStatusPic.setImageResource(R.drawable.availability_color_orange)
                    colorStatusPic?.setImageDrawable(getResources().getDrawable(R.drawable.availability_color_yellow));
                    profileImage?.setBackgroundColor(Color.YELLOW)

                    //Log.d("STATUS", " awayyy!!!!")
                }
                if (statusColorNum == "2") {
                    //colorStatusPic.setImageResource(R.drawable.availability_color_red)
                    colorStatusPic?.setImageDrawable(getResources().getDrawable(R.drawable.availability_color_orange));
                    //profileImage?.setBackgroundColor(Color.parseColor("#FF9800"))
                    profileImage?.setBackgroundColor(Color.parseColor("#FFA500"))
                    //Log.d("STATUS", " busy!!!!")
                }

                if (statusColorNum == "3") {
                    //colorStatusPic.setImageResource(R.drawable.availability_color_red)
                    colorStatusPic?.setImageDrawable(getResources().getDrawable(R.drawable.availability_color_red));
                    //profileImage?.setBackgroundColor(Color.parseColor("#FF0000"))
                    profileImage?.setBackgroundColor(Color.RED)
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

        profileUpdateProfileTextView.setOnClickListener {
            var intent = Intent(this, EditProfileActivity::class.java)
            intent.putExtra("status", profileStatus.text.toString())
            startActivity(intent)
        }
    }
}