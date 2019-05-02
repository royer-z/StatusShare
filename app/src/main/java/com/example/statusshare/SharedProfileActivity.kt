package com.example.statusshare

import android.location.Address
import android.location.Geocoder
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.IOException
import java.util.*

class SharedProfileActivity : AppCompatActivity() {

    // TODO: Receive currentUser
    private var currentUserId = FirebaseAuth.getInstance().currentUser!!.uid // This should be the uid value
    private var currentUserData = FirebaseDatabase.getInstance().reference.child("Registration q").child(currentUserId)

    private var customLocationMarker : Marker? = null
    private var customDestinationMarker : Marker? = null

    private var customLocation : Any? = null
    private var customDestination : Any? = null
    private var liveLocation : LatLng? = null

    private var userSwitchState : Any? =  null

    private lateinit var locationMapFragment : SupportMapFragment
    private lateinit var locationGoogleMap : GoogleMap
    private lateinit var destinationMapFragment : SupportMapFragment
    private lateinit var destinationGoogleMap : GoogleMap

    private fun toast(message: String) =
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

    private fun getAddressFromLL(latLng: LatLng): String {
        val gCoder = Geocoder(this, Locale.US)
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
        val gCoder = Geocoder(this, Locale.US)
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
            val titleStr = getAddressFromLL(liveLocation!!)
            locationGoogleMap.uiSettings.isZoomControlsEnabled = true
            if (customLocationMarker != null) {
                customLocationMarker?.remove()
            }
            customLocationMarker = locationGoogleMap.addMarker(MarkerOptions().position(liveLocation!!).title(titleStr))
            locationGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(liveLocation, 15f))
        }
        else {
            // Retrieve custom location latitude and longitude
            val customLocationLL = getLLFromText(customLocation.toString())
            locationGoogleMap.uiSettings.isZoomControlsEnabled = true
            if (customLocationMarker != null) {
                customLocationMarker?.remove()
            }
            customLocationMarker = locationGoogleMap.addMarker(MarkerOptions().position(customLocationLL).title(customLocation.toString()))
            locationGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(customLocationLL, 15f))
        }
    }

    private fun setUpDestinationMap() {
        val customDestinationLL = getLLFromText(customDestination.toString())
        destinationGoogleMap.uiSettings.isZoomControlsEnabled = true
        if (customDestinationMarker != null) {
            customDestinationMarker?.remove()
        }
        customDestinationMarker = destinationGoogleMap.addMarker(MarkerOptions().position(customDestinationLL).title(customDestination.toString()))
        destinationGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(customDestinationLL, 15f))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shared_profile)

        currentUserData.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                // Fetch contact's switch state
                userSwitchState = p0.child("switchState").value
                // Fetch live location latLng
                val tempLatitude = p0.child("liveLocation").child("latitude").value
                val tempLongitude = p0.child("liveLocation").child("longitude").value
                if (tempLatitude != null && tempLongitude != null) {
                    liveLocation = LatLng(tempLatitude as Double, tempLongitude as Double)
                }
                else {
                    liveLocation = LatLng(0.0, 0.0)
                }
                // Fetch custom location text
                customLocation = p0.child("customLocation").value
                if (customLocation == null) {
                    currentUserData.child("customLocation").setValue("Newark, New Jersey")
                }
                // Fetch custom destination text
                customDestination = p0.child("customDestination").value
                if (customDestination == null) {
                    currentUserData.child("customDestination").setValue("Newark, New Jersey")
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                toast("Could not retrieve data.")
            }
        })

        locationMapFragment = supportFragmentManager.findFragmentById(R.id.sharedProfileLocationMap) as SupportMapFragment
        locationMapFragment.getMapAsync(OnMapReadyCallback {
            locationGoogleMap = it
            setUpLocationMap()
        })

        destinationMapFragment = supportFragmentManager.findFragmentById(R.id.sharedProfileDestinationMap) as SupportMapFragment
        destinationMapFragment.getMapAsync(OnMapReadyCallback {
            destinationGoogleMap = it
            setUpDestinationMap()
        })
    }
}
