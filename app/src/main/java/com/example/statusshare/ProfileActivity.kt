package com.example.statusshare

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_profile.*
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.IOException
import java.util.*

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btn: Button = view.findViewById(R.id.profileUpdateProfileButton)

        btn.setOnClickListener {
            val intent = Intent(getActivity(), EditProfileActivity::class.java)
            getActivity()?.startActivity(intent)
        }
    }
}


class whatever: AppCompatActivity() {

    var mDatabase: DatabaseReference? = null
    var mCurrentUser: FirebaseUser? = null
    //var mStorageRef:StorageReference?= null

    private lateinit var locationMapFragment : SupportMapFragment
    private lateinit var locationGoogleMap : GoogleMap

    private lateinit var destinationMapFragment : SupportMapFragment
    private lateinit var destinationGoogleMap : GoogleMap

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        fun Context.toast(message: String) =
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

        fun getAddress(latLng: LatLng): String {
            val geocoder = Geocoder(this, Locale.US)
            val addresses: List<Address>?
            var addressText = ""

            try {
                addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

                if (addresses.isNotEmpty()) {
                    val address = addresses[0]
                    addressText = address.getAddressLine(0).toString()
                }
            } catch (e: IOException) {
                toast("Could not get address")
            }

            return addressText
        }

        fun setUpLocationMap() {
            // Permission request
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
                return
            }

            locationGoogleMap.isMyLocationEnabled = true

            fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    val titleStr = getAddress(currentLatLng)
                    locationGoogleMap.addMarker(MarkerOptions().position(currentLatLng).title(titleStr))
                    locationGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                }
            }

        }

        fun setUpDestinationMap() {
            // Permission request
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
                return
            }

            destinationGoogleMap.isMyLocationEnabled = true

            fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    val titleStr = getAddress(currentLatLng)
                    destinationGoogleMap.addMarker(MarkerOptions().position(currentLatLng).title(titleStr))
                    destinationGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                }
            }
        }

        locationMapFragment = supportFragmentManager.findFragmentById(R.id.profileLocationMap) as SupportMapFragment
        locationMapFragment.getMapAsync(OnMapReadyCallback {
            locationGoogleMap = it
            locationGoogleMap.getUiSettings().setZoomControlsEnabled(true)
            setUpLocationMap()
        })

        destinationMapFragment = supportFragmentManager.findFragmentById(R.id.profileDestinationMap) as SupportMapFragment
        destinationMapFragment.getMapAsync(OnMapReadyCallback () {
            destinationGoogleMap = it
            destinationGoogleMap.getUiSettings().setZoomControlsEnabled(true)
            setUpDestinationMap()
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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }
}