package com.example.statusshare

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_profile.*
import java.io.IOException
import android.util.Log
import android.widget.*
import com.example.statusshare.Model.Event
import com.squareup.picasso.Picasso
import com.squareup.picasso.Picasso.*


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
class ProfileActivity : Fragment(), GoogleMap.OnMarkerClickListener  {
    // TODO: Rename and change types of parameters
    var mDatabase: DatabaseReference? = null
    var mCurrentUser: FirebaseUser? = null

    override fun onMarkerClick(p0: Marker?) = false

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
//            setContentView(R.layout.activity_profile)
//
//
//
//        val rv = findViewById<RecyclerView>(R.id.recyclerView1)
//        rv.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
//        val events = ArrayList<Event>()
//        events.add(Event("Kitty Party", "Home","today","2:00","zxcz"))
//        events.add(Event("LOLI Party", "Home","today","6:00","dixzcsc"))
//        events.add(Event("Zoo Party", "Work Office","today","12:00","zxc"))
//        events.add(Event("Sleep Over", "NJIT","today","9:00","disc"))
//
//        var adapter = eventAdapter(events)
//        rv.adapter = adapter
//
        }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.activity_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val events = ArrayList<Event>()
        events.add(Event("Kitty Party", "Home","today","2:00","zxcz"))
        events.add(Event("LOLI Party", "Home","today","6:00","dixzcsc"))
        events.add(Event("Zoo Party", "Work Office","today","12:00","zxc"))
        events.add(Event("Sleep Over", "NJIT","today","9:00","disc"))

        recyclerView1.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = eventAdapter(events)

        }
//        val rv = findViewById<RecyclerView>(R.id.recyclerView1)
//        rv.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
//        val events = ArrayList<Event>()
//        events.add(Event("Kitty Party", "Home","today","2:00","zxcz"))
//        events.add(Event("LOLI Party", "Home","today","6:00","dixzcsc"))
//        events.add(Event("Zoo Party", "Work Office","today","12:00","zxc"))
//        events.add(Event("Sleep Over", "NJIT","today","9:00","disc"))
//
//        var adapter = eventAdapter(events)
//        rv.adapter = adapter

        //colorStatus pic
        val colorStatusPic = getView()?.findViewById<ImageView>(R.id.profileAvailabilityColor)

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
                    colorStatusPic?.setImageDrawable(getResources().getDrawable(R.drawable.availability_color_green));
                    //Log.d("STATUS", " available!")
                }
                if(statusColorNum == "1"){
                    //colorStatusPic.setImageResource(R.drawable.availability_color_orange)
                    colorStatusPic?.setImageDrawable(getResources().getDrawable(R.drawable.availability_color_yellow));
                    //Log.d("STATUS", " awayyy!!!!")
                }
                if(statusColorNum=="2"){
                    //colorStatusPic.setImageResource(R.drawable.availability_color_red)
                    colorStatusPic?.setImageDrawable(getResources().getDrawable(R.drawable.availability_color_orange));
                    //Log.d("STATUS", " busy!!!!")
                }

                if(statusColorNum=="3"){
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
//            intent.putExtra("location", profileLocationHeading.text.toString())
//            intent.putExtra("destination", profileDestinationHeading.text.toString())
            getActivity()?.startActivity(intent)
        }
    }
}


class whatever: AppCompatActivity(), GoogleMap.OnMarkerClickListener {
    override fun onMarkerClick(p0: Marker?) = false

    var mDatabase: DatabaseReference? = null
    var mCurrentUser: FirebaseUser? = null
    //var mStorageRef:StorageReference?= null

    lateinit var locationMapFragment : SupportMapFragment
    lateinit var locationGoogleMap : GoogleMap

    lateinit var destinationMapFragment : SupportMapFragment
    lateinit var destinationGoogleMap : GoogleMap

    // 1
    private lateinit var locationCallback: LocationCallback
    // 2
    private lateinit var locationRequest: LocationRequest
    private var locationUpdateState = false

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1

        // for Location updates
        private const val REQUEST_CHECK_SETTINGS = 2

        private const val PLACE_PICKER_REQUEST = 3
    }

    private lateinit var lastLocation: Location

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)


        /*
        val rv = findViewById<RecyclerView>(R.id.recyclerView1)
        rv.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        val events = ArrayList<Event>()
        events.add(Event("Kitty Party", "Home","today","2:00","zxcz"))
        events.add(Event("LOLI Party", "Home","today","6:00","dixzcsc"))
        events.add(Event("Zoo Party", "Work Office","today","12:00","zxc"))
        events.add(Event("Sleep Over", "NJIT","today","9:00","disc"))

        var adapter = eventAdapter(events)
        rv.adapter = adapter
        */
        fun loadPlacePicker() {
            val builder = PlacePicker.IntentBuilder()

            try {
                startActivityForResult(builder.build(this@whatever), PLACE_PICKER_REQUEST)
            } catch (e: GooglePlayServicesRepairableException) {
                e.printStackTrace()
            } catch (e: GooglePlayServicesNotAvailableException) {
                e.printStackTrace()
            }
        }

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            loadPlacePicker()
        }

        fun getAddress(latLng: LatLng): String {
            // 1
            val geocoder = Geocoder(this)
            val addresses: List<Address>?
            val address: Address?
            var addressText = ""

            try {
                // 2
                addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                // 3
                if (null != addresses && !addresses.isEmpty()) {
                    address = addresses[0]
                    for (i in 0 until address.maxAddressLineIndex) {
                        addressText += if (i == 0) address.getAddressLine(i) else "\n" + address.getAddressLine(i)
                    }
                }
            } catch (e: IOException) {
                Log.e("MapsActivity", e.localizedMessage)
            }

            return addressText
        }

        fun placeMarkerOnMap(location: LatLng) {
            // 1
            val markerOptions = MarkerOptions().position(location)

            // for Geocoding
            val titleStr = getAddress(location)
            markerOptions.title(titleStr)

            // 2
            locationGoogleMap.addMarker(markerOptions)
        }

        // for Location updates
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)

                lastLocation = p0.lastLocation
                placeMarkerOnMap(LatLng(lastLocation.latitude, lastLocation.longitude))
            }
        }

        // for Location updates
        fun startLocationUpdates() {
            //1
            if (ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE)
                return
            }
            //2
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null /* Looper */)
        }

        // for Location updates
        // 1
        fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
            super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == REQUEST_CHECK_SETTINGS) {
                if (resultCode == Activity.RESULT_OK) {
                    locationUpdateState = true
                    startLocationUpdates()
                }
            }

            if (requestCode == PLACE_PICKER_REQUEST) {
                if (resultCode == RESULT_OK) {
                    val place = PlacePicker.getPlace(this, data)
                    var addressText = place.name.toString()
                    addressText += "\n" + place.address.toString()

                    placeMarkerOnMap(place.latLng)
                }
            }
        }

        // 2
        fun onPause() {
            super.onPause()
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }

        // 3
        fun onResume() {
            super.onResume()
            if (!locationUpdateState) {
                startLocationUpdates()
            }
        }

        // for Location updates
        fun createLocationRequest() {
            // 1
            locationRequest = LocationRequest()
            // 2
            locationRequest.interval = 30000
            // 3
            locationRequest.fastestInterval = 25000
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

            val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)

            // 4
            val client = LocationServices.getSettingsClient(this)
            val task = client.checkLocationSettings(builder.build())

            // 5
            task.addOnSuccessListener {
                locationUpdateState = true
                startLocationUpdates()
            }
            task.addOnFailureListener { e ->
                // 6
                if (e is ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        e.startResolutionForResult(this@whatever,
                            REQUEST_CHECK_SETTINGS)
                    } catch (sendEx: IntentSender.SendIntentException) {
                        // Ignore the error.
                    }
                }
            }
        }

        fun setUpMap() {
            // Permission request
            if (ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
                return
            }

            // 1
            locationGoogleMap.isMyLocationEnabled = true

            // 2
            fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
                // Got last known location. In some rare situations this can be null.
                // 3
                if (location != null) {
                    lastLocation = location
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    placeMarkerOnMap(currentLatLng)
                    locationGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                }
            }
        }

        locationMapFragment = supportFragmentManager.findFragmentById(R.id.profileLocationMap) as SupportMapFragment
        locationMapFragment.getMapAsync(OnMapReadyCallback {
            locationGoogleMap = it
            // locationGoogleMap.isMyLocationEnabled = true

            // val defaultLocation = LatLng(40.743920, -74.178079)
            // val defaultLocationTitle = "NJIT Library"
            // locationGoogleMap.addMarker(MarkerOptions().position(defaultLocation).title(defaultLocationTitle))
            // locationGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15f))

            locationGoogleMap.getUiSettings().setZoomControlsEnabled(true)
            locationGoogleMap.setOnMarkerClickListener(this)

            setUpMap()
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
                    //Log.d("STATUS", " available!")
                }
                if(statusColorNum == "1"){
                    //colorStatusPic.setImageResource(R.drawable.availability_color_orange)
                    colorStatusPic.setImageDrawable(getResources().getDrawable(R.drawable.availability_color_yellow));
                    //Log.d("STATUS", " awayyy!!!!")
                }
                if(statusColorNum=="2"){
                    //colorStatusPic.setImageResource(R.drawable.availability_color_red)
                    colorStatusPic.setImageDrawable(getResources().getDrawable(R.drawable.availability_color_orange));
                    //Log.d("STATUS", " busy!!!!")
                }

                if(statusColorNum=="3"){
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
            intent.putExtra("location", profileLocationHeading.text.toString())
            intent.putExtra("destination", profileDestinationHeading.text.toString())
            startActivity(intent)
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // for Location updates
        createLocationRequest()
    }
}