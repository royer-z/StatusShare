package com.example.statusshare

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
//import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.activity_edit_profile.view.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.*

class EditProfileActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener{

    private lateinit var locationSwitch : Switch
    private lateinit var switchState: String

    private lateinit var locationMapFragment : SupportMapFragment
    private lateinit var locationGoogleMap : GoogleMap

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    private fun toast(message: String) =
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

    private fun getAddress(latLng: LatLng): String {
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
            toast("Could not get address.")
        }

        return addressText
    }

    @SuppressLint("MissingPermission")
    fun setUpLocationMap(switchState : String) {

        if (switchState == "on") {
            locationGoogleMap.isMyLocationEnabled = true

            fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    val titleStr = getAddress(currentLatLng)
                    locationGoogleMap.uiSettings.isZoomControlsEnabled = true
                    locationGoogleMap.addMarker(MarkerOptions().position(currentLatLng).title(titleStr))
                    locationGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                }
            }
        }
        else {
            // TODO:  Retrieve custom location latitude and longitude

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission IS granted
                    toast("Location permission granted.")
                    setUpLocationMap(switchState)
                } else {
                    // Permission NOT granted
                    toast("Location permission denied.")
                }
                return
            }
        }
    }

    private var mDatabase: DatabaseReference? = null
    private var mCurrentUser: FirebaseUser? = null
    var GALLERY_ID: Int = 1
    private var mStorageRef: StorageReference? = null
    lateinit var statusSpinner: Spinner
    lateinit var status_name_code: String

    var statusColorNum = 9999

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_edit_profile)
        val mainLayout = layoutInflater.inflate(R.layout.activity_edit_profile, null)
        setContentView(mainLayout)

        fun checkPermission() {
            // Permissions check
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                // Permission NOT granted
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                    // Need to show explanation
                    toast("Location permission is needed to show your current location on a map.")
                    // Request permission again
                    ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), EditProfileActivity.LOCATION_PERMISSION_REQUEST_CODE
                    )
                }
                else {
                    // Request permission
                    toast("Requesting permission.")
                    ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), EditProfileActivity.LOCATION_PERMISSION_REQUEST_CODE
                    )
                }
            }
            else if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // Permission IS granted
                toast("Setting up map.")
                setUpLocationMap(switchState)
            }
        }

        // TODO: Retrieve switch state from FireBase

        locationSwitch = findViewById(R.id.editProfileLocationSwitch)

        // TODO: Set switch state
        switchState = "off"

        locationSwitch.setOnCheckedChangeListener { _, isChecked: Boolean ->
            if (isChecked) {
                // Show current live location on map
                toast("Show current live location.")
                switchState = "on"
                setUpLocationMap(switchState)
            }
            else {
                // Show custom location on map
                toast("Show custom location.")
                switchState = "off"
                setUpLocationMap(switchState)
            }
        }

        locationMapFragment = supportFragmentManager.findFragmentById(R.id.editProfileLocationMap) as SupportMapFragment
        locationMapFragment.onCreate(null)
        locationMapFragment.onResume()
        locationMapFragment.getMapAsync(OnMapReadyCallback {
            locationGoogleMap = it
            checkPermission()
        })

        mCurrentUser = FirebaseAuth.getInstance().currentUser
        mStorageRef = FirebaseStorage.getInstance().reference

        var userId = mCurrentUser!!.uid

        mDatabase = FirebaseDatabase.getInstance().reference
            .child("Registration q")
            .child(userId)

        mDatabase!!.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                var image = dataSnapshot!!.child("image").value.toString()
                var thumbnail = dataSnapshot!!.child("thumb_image").value

                if (!image!!.equals("null")) {
                    Picasso.with(applicationContext)
                        .load(image)
                        .placeholder(R.drawable.default_profile_image)
                        .into(editProfileProfileImageButton)
                }
            }

            override fun onCancelled(databaseErrorSnapshot: DatabaseError) {
            }
        })

        statusSpinner = mainLayout.spinner_statuses

        val countryPickerData = Utility.statuses
        val pickerAdapter =
            CustomAdapter(this@EditProfileActivity, R.layout.status_availability_spinner, countryPickerData)
        statusSpinner.setAdapter(pickerAdapter)

        statusSpinner.setSelection(1)

        status_name_code = countryPickerData.get(0).statusWord!!

        if (intent.extras != null) {
            var oldStatus = intent.extras.get("status")
            var oldLocation = intent.extras.get("location")
            var oldDestination = intent.extras.get("destination")

            editProfileStatus.setText(oldStatus.toString())
//            editProfileLocation.setText(oldLocation.toString())
//            editProfileDestination.setText(oldDestination.toString())
        }

        editProfileSaveButton.setOnClickListener {
            mCurrentUser = FirebaseAuth.getInstance().currentUser
            var userId = mCurrentUser!!.uid

            mDatabase = FirebaseDatabase.getInstance().reference.child("Registration q").child(userId)

            var status = editProfileStatus.text.toString()
            mDatabase!!.child("status").setValue(status).addOnCompleteListener { task: Task<Void> ->
                if (task.isSuccessful) {
                    //Toast.makeText(this,"Status Updated Successfully!",Toast.LENGTH_LONG).show()
                    startActivity(Intent(this, whatever::class.java))
                } else {
                    //Toast.makeText(this,"Status Not Updated",Toast.LENGTH_LONG).show()
                }
            }

            var location = editProfileLocation.text.toString()
            mDatabase!!.child("location").setValue(location).addOnCompleteListener { task: Task<Void> ->
                if (task.isSuccessful) {
                    //Toast.makeText(this,"Location Updated Successfully!",Toast.LENGTH_LONG).show()
                } else {
                    //Toast.makeText(this,"Location Not Updated",Toast.LENGTH_LONG).show()
                }
            }

            var destination = editProfileDestination.text.toString()
            mDatabase!!.child("destination").setValue(destination).addOnCompleteListener { task: Task<Void> ->
                if (task.isSuccessful) {
                    //Toast.makeText(this,"Destination Updated Successfully!",Toast.LENGTH_LONG).show()
                } else {
                    //Toast.makeText(this,"Destination Not Updated",Toast.LENGTH_LONG).show()
                }
            }

            mDatabase!!.child("colorStatus").setValue(statusColorNum)

        }

        editProfileProfileImageButton.setOnClickListener {
            var galleryIntent = Intent()
            galleryIntent.type = "image/*"
            galleryIntent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(galleryIntent, "SELECT_IMAGE"), GALLERY_ID)
        }


        statusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                status_name_code = countryPickerData.get(i).statusWord!!
                statusColorNum = i
                //Toast.makeText(this@MainActivity, "You selected $countryNameValue with id $countryIdValue and code $countryCodeValue", Toast.LENGTH_SHORT).show()
                statusSpinner.setSelection(i)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {
                //handle when no item selected
            }
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }


    override fun onItemSelected(arg0: AdapterView<*>, arg1: View, position: Int, id: Long) {
        //textView_msg!!.text = "Selected : "+languages[position]

        //statusColorNum = position
    }

    override fun onNothingSelected(arg0: AdapterView<*>) {
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == GALLERY_ID && resultCode == Activity.RESULT_OK) {

            var image: Uri = data!!.data

            CropImage.activity(image)
                .setAspectRatio(1, 1)
                .start(this)

        }

        if (requestCode === CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)

            if (resultCode === Activity.RESULT_OK) {
                val resultUri = result.uri
                var userID = mCurrentUser!!.uid
                var thumbFile = File(resultUri.path)

                var thumbBitmap = Compressor(this)
                    .setMaxWidth(200)
                    .setMaxHeight(200)
                    .setQuality(65)
                    .compressToBitmap(thumbFile)

                //Upload images to Firebase

                var byteArray = ByteArrayOutputStream()
                thumbBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArray)
                var thumbByteArray: ByteArray
                thumbByteArray = byteArray.toByteArray()

                var filePath = mStorageRef!!.child("profile_image")
                    .child(userID + ".jpg")

                //Create another directory for compressed/smaller images

                var thumbFilePath = mStorageRef!!.child("profile_image")
                    .child("thumb")
                    .child(userID + ".jpg")

                //filePath.putFile(resultUri)
                //var uploadTask: UploadTask = thumbFilePath.putBytes(thumbByteArray)


                //uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                filePath.putFile(resultUri).continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    return@Continuation filePath.downloadUrl
                }).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        //Let's get the pic url
                        val downloadUriOrgiinal = task.result.toString()

                        //Upload Task
                        var uploadTask: UploadTask = thumbFilePath.putBytes(thumbByteArray)


                        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                            if (!task.isSuccessful) {
                                task.exception?.let {
                                    throw it
                                }
                            }
                            return@Continuation thumbFilePath.downloadUrl
                        }).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val downloadUriThumb = task.result.toString()

                                var updateObj = HashMap<String, Any>()
                                updateObj.put("image", downloadUriOrgiinal)
                                updateObj.put("thumb_image", downloadUriThumb)


                                mDatabase!!.updateChildren(updateObj)
                                    .addOnCompleteListener {
                                            task: Task<Void> ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(this, "Profile Image Saved!",
                                                Toast.LENGTH_LONG)
                                                .show()

                                        }else {

                                        }
                                    }
                            } else {
                                // Handle failures
                                // ...
                            }
                        }
                    } else {
                        // Handle failures
                        // ...
                    }
                }

            }

        }
    }
}


