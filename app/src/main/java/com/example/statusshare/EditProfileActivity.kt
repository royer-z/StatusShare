package com.example.statusshare

import android.app.Activity
import android.content.Intent
import android.location.Address
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.schibstedspain.leku.*
import com.schibstedspain.leku.locale.SearchZoneRect
import kotlinx.android.synthetic.main.activity_edit_profile.*

private const val MAP_BUTTON_REQUEST_CODE = 1

class EditProfileActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    var mDatabase: DatabaseReference? = null
    var mCurrentUser: FirebaseUser? = null

    //0 = available, 1 = away, 2 = busy
    var statuses = arrayOf("Available","Away","Busy")
    var spinner: Spinner? = null
    var textView_msg: TextView? = null
    var statusColorNum = 9999

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val locationPickerIntent = LocationPickerActivity.Builder()
            .withLocation(41.4036299, 2.1743558)
            .withGeolocApiKey("AIzaSyDWFSzwAPBtt3KZd3fcKpdhWwGgROCkWTs")
            .withSearchZone("es_ES")
            .withSearchZone(SearchZoneRect(LatLng(26.525467, -18.910366), LatLng(43.906271, 5.394197)))
            .withDefaultLocaleSearchZone()
            .shouldReturnOkOnBackPressed()
            .withStreetHidden()
            .withCityHidden()
            .withZipCodeHidden()
            .withSatelliteViewHidden()
            .withGooglePlacesEnabled()
            .withGoogleTimeZoneEnabled()
            .withVoiceSearchHidden()
            .withUnnamedRoadHidden()
            .build(applicationContext)

        startActivityForResult(locationPickerIntent, MAP_BUTTON_REQUEST_CODE)

        fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                Log.d("RESULT****", "OK")
                if (requestCode == 1) {
                    val latitude = data.getDoubleExtra(LATITUDE, 0.0)
                    Log.d("LATITUDE****", latitude.toString())
                    val longitude = data.getDoubleExtra(LONGITUDE, 0.0)
                    Log.d("LONGITUDE****", longitude.toString())
                    val address = data.getStringExtra(LOCATION_ADDRESS)
                    Log.d("ADDRESS****", address.toString())
                    val postalcode = data.getStringExtra(ZIPCODE)
                    Log.d("POSTALCODE****", postalcode.toString())
                    val bundle = data.getBundleExtra(TRANSITION_BUNDLE)
                    Log.d("BUNDLE TEXT****", bundle.getString("test"))
                    val fullAddress = data.getParcelableExtra<Address>(ADDRESS)
                    if (fullAddress != null) {
                        Log.d("FULL ADDRESS****", fullAddress.toString())
                    }
                    val timeZoneId = data.getStringExtra(TIME_ZONE_ID)
                    Log.d("TIME ZONE ID****", timeZoneId)
                    val timeZoneDisplayName = data.getStringExtra(TIME_ZONE_DISPLAY_NAME)
                    Log.d("TIME ZONE NAME****", timeZoneDisplayName)
                } else if (requestCode == 2) {
                    val latitude = data.getDoubleExtra(LATITUDE, 0.0)
                    Log.d("LATITUDE****", latitude.toString())
                    val longitude = data.getDoubleExtra(LONGITUDE, 0.0)
                    Log.d("LONGITUDE****", longitude.toString())
                    val address = data.getStringExtra(LOCATION_ADDRESS)
                    Log.d("ADDRESS****", address.toString())
                    val lekuPoi = data.getParcelableExtra<LekuPoi>(LEKU_POI)
                    Log.d("LekuPoi****", lekuPoi.toString())
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Log.d("RESULT****", "CANCELLED")
            }
        }

        spinner = this.spinner_sample
        spinner!!.setOnItemSelectedListener(this)
        val aa = ArrayAdapter(this,R.layout.spinner_item,statuses)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner!!.setAdapter(aa)

        if(intent.extras!=null){
            var oldStatus = intent.extras.get("status")
            var oldLocation = intent.extras.get("location")
            var oldDestination = intent.extras.get("destination")

            editProfileStatus.setText(oldStatus.toString())
//            editProfileLocation.setText(oldLocation.toString())
            editProfileDestination.setText(oldDestination.toString())
        }

        editProfileSaveButton.setOnClickListener{
            mCurrentUser = FirebaseAuth.getInstance().currentUser
            var userId = mCurrentUser!!.uid

            mDatabase = FirebaseDatabase.getInstance().reference.child("Registration q").child(userId)

            var status = editProfileStatus.text.toString()
            mDatabase!!.child("status").setValue(status).addOnCompleteListener{
                task: Task<Void> ->
                if (task.isSuccessful){
                    Toast.makeText(this,"Status Updated Successfully!",Toast.LENGTH_LONG).show()
                    startActivity(Intent(this,whatever::class.java))
                } else {
                    Toast.makeText(this,"Status Not Updated",Toast.LENGTH_LONG).show()
                }
            }

//            var location = editProfileLocation.text.toString()
//            mDatabase!!.child("location").setValue(location).addOnCompleteListener{
//                    task: Task<Void> ->
//                if (task.isSuccessful){
//                    Toast.makeText(this,"Location Updated Successfully!",Toast.LENGTH_LONG).show()
//                } else {
//                    Toast.makeText(this,"Location Not Updated",Toast.LENGTH_LONG).show()
//                }
//            }

            var destination = editProfileDestination.text.toString()
            mDatabase!!.child("destination").setValue(destination).addOnCompleteListener{
                    task: Task<Void> ->
                if (task.isSuccessful){
                    Toast.makeText(this,"Destination Updated Successfully!",Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this,"Destination Not Updated",Toast.LENGTH_LONG).show()
                }
            }

            mDatabase!!.child("colorStatus").setValue(statusColorNum)

        }
    }
    override fun onItemSelected(arg0: AdapterView<*>, arg1: View, position: Int, id: Long) {
        //textView_msg!!.text = "Selected : "+languages[position]
        statusColorNum = position
    }

    override fun onNothingSelected(arg0: AdapterView<*>) {

    }
}
