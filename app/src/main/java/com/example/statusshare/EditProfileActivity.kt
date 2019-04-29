package com.example.statusshare

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
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
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
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

    private var currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
    private var currentUserData = FirebaseDatabase.getInstance().reference.child("Registration q").child(currentUserId)

    private var customLocationMarker : Marker? = null
    private var customDestinationMarker : Marker? = null

    private var customLocation : Any? = null
    private var customDestination : Any? = null
    private var liveLocation : LatLng? = null

    private var userSwitchState : Any? =  null
    private var initialSwitchState : String? = ""
    private lateinit var locationSwitch : Switch
    private var locationPermission : Boolean = false

    private lateinit var locationMapFragment : SupportMapFragment
    private lateinit var locationGoogleMap : GoogleMap
    private lateinit var destinationMapFragment : SupportMapFragment
    private lateinit var destinationGoogleMap : GoogleMap

    private lateinit var fusedLocationClient : FusedLocationProviderClient

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

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

    @SuppressLint("MissingPermission")
    fun setUpLocationMap() {
        if (userSwitchState == "on") {
            locationGoogleMap.isMyLocationEnabled = true

            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

            fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    liveLocation = currentLatLng
                    val titleStr = getAddressFromLL(currentLatLng)
                    locationGoogleMap.uiSettings.isZoomControlsEnabled = true
                    if (customLocationMarker != null) {
                        customLocationMarker?.remove()
                    }
                    customLocationMarker = locationGoogleMap.addMarker(MarkerOptions().position(currentLatLng).title(titleStr))
                    locationGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                    // Save live location to Firebase
                    currentUserData.child("liveLocation").setValue(liveLocation)
                }
            }
        }
        else {
            // Retrieve custom location latitude and longitude
            val customLocationText = findViewById<EditText>(R.id.editProfileLocation).text.toString()
            val customLocationLL = getLLFromText(customLocationText)
            locationGoogleMap.uiSettings.isZoomControlsEnabled = true
            if (customLocationMarker != null) {
                customLocationMarker?.remove()
            }
            customLocationMarker = locationGoogleMap.addMarker(MarkerOptions().position(customLocationLL).title(customLocationText))
            locationGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(customLocationLL, 15f))
        }
    }

    private fun setUpDestinationMap() {
        val customDestinationText = findViewById<EditText>(R.id.editProfileDestination).text.toString()
        val customDestinationLL = getLLFromText(customDestinationText)
        destinationGoogleMap.uiSettings.isZoomControlsEnabled = true
        if (customDestinationMarker != null) {
            customDestinationMarker?.remove()
        }
        customDestinationMarker = destinationGoogleMap.addMarker(MarkerOptions().position(customDestinationLL).title(customDestinationText))
        destinationGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(customDestinationLL, 15f))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission IS granted
                    locationPermission = true
                    currentUserData.child("switchState").setValue("on")
                    setUpLocationMap()
                }
                else {
                    // Permission NOT granted
                    locationPermission = false
                    currentUserData.child("switchState").setValue("off")
                    setUpLocationMap()
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

    private var mAdapter: FirebaseRecyclerAdapter<EventItemModel, EventViewHolder>? = null

    var statusColorNum = 9999

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_edit_profile)
        val mainLayout = layoutInflater.inflate(R.layout.activity_edit_profile, null)
        setContentView(mainLayout)

        locationSwitch = findViewById(R.id.editProfileLocationSwitch)

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
                    ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), EditProfileActivity.LOCATION_PERMISSION_REQUEST_CODE
                    )
                }
            }
            else if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // Permission IS granted
                locationPermission = true
                setUpLocationMap()
            }
        }

        // Pre-fill custom location editTextView
        currentUserData.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                customLocation = p0.child("customLocation").value

                if (customLocation == null || customLocation == "") {
                    currentUserData.child("customLocation").setValue("Newark, New Jersey")
                }
                else {
                    findViewById<EditText>(R.id.editProfileLocation).setText(customLocation.toString())
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                toast("Could not retrieve custom location.")
            }
        })

        // Pre-fill custom destination editTextView
        currentUserData.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                customDestination = p0.child("customDestination").value

                if (customDestination == null || customDestination == "") {
                    currentUserData.child("customDestination").setValue("Newark, New Jersey")
                }
                else {
                    findViewById<EditText>(R.id.editProfileDestination).setText(customDestination.toString())
                    setUpDestinationMap()
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                toast("Could not retrieve custom destination.")
            }
        })

        locationMapFragment = supportFragmentManager.findFragmentById(R.id.editProfileLocationMap) as SupportMapFragment
        locationMapFragment.getMapAsync(OnMapReadyCallback {
            locationGoogleMap = it
            checkPermission()
        })

        destinationMapFragment = supportFragmentManager.findFragmentById(R.id.editProfileDestinationMap) as SupportMapFragment
        destinationMapFragment.getMapAsync(OnMapReadyCallback {
            destinationGoogleMap = it
            setUpDestinationMap()
        })

        currentUserData.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                userSwitchState = p0.child("switchState").value

                if (userSwitchState == null) {
                    currentUserData.child("switchState").setValue("off")
                }
                if (initialSwitchState != null && userSwitchState == "on") {
                    locationSwitch.isChecked = true
                    initialSwitchState = null
                }
                checkPermission()
            }

            override fun onCancelled(p0: DatabaseError) {
                toast("Could not retrieve switch state.")
            }
        })

        locationSwitch.setOnCheckedChangeListener { _, isChecked: Boolean ->
            if (isChecked) {
                // Switched to live location
                if (locationPermission) {
                    currentUserData.child("switchState").setValue("on")
                    // Save live location to Firebase
                    currentUserData.child("liveLocation").setValue(liveLocation)
                }
                else {
                    toast("Location permission was denied. Please allow location permission.")
                    locationSwitch.isChecked = false
                    checkPermission()
                }
            }
            else {
                // Switched to custom location
                currentUserData.child("switchState").setValue("off")
                // Save custom location to Firebase
                currentUserData.child("customLocation").setValue(findViewById<EditText>(R.id.editProfileLocation).text.toString())
            }
        }

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

        editProfileFAB.setOnClickListener {
            mCurrentUser = FirebaseAuth.getInstance().currentUser
            var userId = mCurrentUser!!.uid

            mDatabase = FirebaseDatabase.getInstance().reference.child("Registration q").child(userId)

            var status = editProfileStatus.text.toString()
            mDatabase!!.child("status").setValue(status).addOnCompleteListener { task: Task<Void> ->
                if (task.isSuccessful) {
                    //Toast.makeText(this,"Status Updated Successfully!",Toast.LENGTH_LONG).show()
                    startActivity(Intent(this, BottomNavActivityToMyProfile::class.java))
                } else {
                    //Toast.makeText(this,"Status Not Updated",Toast.LENGTH_LONG).show()
                }
            }

            var location = editProfileLocation.text.toString()
            mDatabase!!.child("customLocation").setValue(location).addOnCompleteListener { task: Task<Void> ->
                if (task.isSuccessful) {
                    //Toast.makeText(this,"Location Updated Successfully!",Toast.LENGTH_LONG).show()
                } else {
                    //Toast.makeText(this,"Location Not Updated",Toast.LENGTH_LONG).show()
                }
            }

            var destination = editProfileDestination.text.toString()
            mDatabase!!.child("customDestination").setValue(destination).addOnCompleteListener { task: Task<Void> ->
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

        mDatabase = FirebaseDatabase.getInstance().reference.child("Registration q").child(userId).child("events")

        mDatabase!!.keepSynced(true)

        val recyclerView = findViewById(R.id.recyclerView2) as RecyclerView
        recyclerView.setNestedScrollingEnabled(false)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val mQuery = mDatabase!!.orderByKey()
        val mOptions = FirebaseRecyclerOptions.Builder<EventItemModel>()
            .setQuery(mQuery, EventItemModel::class.java)
            .setLifecycleOwner(this)
            .build()
        mAdapter = object : FirebaseRecyclerAdapter<EventItemModel, EventViewHolder>(mOptions) {
            override fun getItem(position: Int): EventItemModel {
                return super.getItem(position)
            }
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
                val view = LayoutInflater.from(parent!!.context)
                    .inflate(R.layout.event_row_list, parent, false)
                return EventViewHolder(view)
            }
            override fun onBindViewHolder(viewHolder: EventViewHolder, position: Int, model: EventItemModel) {
                viewHolder.setModel(model)
            }
        }
        recyclerView.adapter = mAdapter
        recyclerView.addOnItemTouchListener(EventLongPressListener(this,
            recyclerView!!, object : EventLongPressListener.ClickListener {
                override fun onClick(view: View, position: Int) {}
                override fun onLongClick(view: View?, position: Int) {
                    showActionsDialog(position)
                }
            }))
        //val fab = findViewById<View>(R.id.fab) as FloatingActionButton
        val fab = findViewById<View>(R.id.editProfileAddEventTextView)
        fab.setOnClickListener { showEntryDialog(false, null, -1) }

    }

    private fun createEventItem(eventTitle: String,eventAddress: String,eventDate: String,eventTime: String,eventDesciption: String) {
        val pushId = mDatabase!!.push().key
        val eventItem = EventItemModel(pushId!!, eventTitle, eventAddress,eventDate,eventTime,eventDesciption)//Root and child id will be same
        mDatabase!!.child(pushId).setValue(eventItem)
    }
    private fun updateToDoItem(eventTitle: String,eventAddress: String,eventDate: String,eventTime: String,eventDesciption: String,eventId: String) {
        mDatabase!!.child(eventId).child("eventTitle").setValue(eventTitle)
        mDatabase!!.child(eventId).child("eventAddress").setValue(eventAddress)
        mDatabase!!.child(eventId).child("eventDate").setValue(eventDate)
        mDatabase!!.child(eventId).child("eventTime").setValue(eventTime)
        mDatabase!!.child(eventId).child("eventDesciption").setValue(eventDesciption)
    }
    private fun deleteToDoItem(eventId: String) {
        mDatabase!!.child(eventId).setValue(null)
    }
    private fun deleteAllToDoItems() {
        //Caution - This is considered as a bad practice
        mDatabase!!.removeValue()
    }

    private fun showActionsDialog(position: Int) {
        val options = arrayOf<CharSequence>("Edit This Event", "Delete This Event", "Delete All Events")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Action")
        builder.setItems(options) { dialog, itemIndex ->
            // add more options check index
            when (itemIndex) {
                0 -> showEntryDialog(true, mAdapter!!.getItem(position), position)
                1 -> deleteToDoItem(mAdapter!!.getItem(position).eventId.toString())
                2 -> deleteAllToDoItems()
                else -> Toast.makeText(applicationContext, "never gonna load", Toast.LENGTH_SHORT).show()
            }
        }
        builder.show()
    }
    private fun showEntryDialog(shouldUpdate: Boolean, EventItemModel: EventItemModel?, position: Int) {
        val layoutInflaterAndroid = LayoutInflater.from(applicationContext)
        val view = layoutInflaterAndroid.inflate(R.layout.event_entry_dialog, null)
        val alertDialogBuilderUserInput = AlertDialog.Builder(this@EditProfileActivity)
        alertDialogBuilderUserInput.setView(view)

        val eventName = view.findViewById<EditText>(R.id.event_name)
        val eventAddress = view.findViewById<EditText>(R.id.event_address)
        val eventDate = view.findViewById<EditText>(R.id.event_date)
        val eventTime = view.findViewById<EditText>(R.id.event_time)
        val eventDescription = view.findViewById<EditText>(R.id.event_description)



        val dialogTitle = view.findViewById<TextView>(R.id.dialog_header)
        dialogTitle.text = if (!shouldUpdate) getString(R.string.dialog_new_entry_title) else getString(R.string.dialog_edit_entry_title)
        if (shouldUpdate && EventItemModel != null) {
            eventName.setText(EventItemModel!!.eventTitle)
            eventAddress.setText(EventItemModel!!.eventAddress)
            eventDate.setText(EventItemModel!!.eventDate)
            eventTime.setText(EventItemModel!!.eventTime)
            eventDescription.setText(EventItemModel!!.eventDesciption)
        }
        alertDialogBuilderUserInput
            .setCancelable(false)
            .setPositiveButton(if (shouldUpdate) "update" else "save") { dialogBox, id -> }
            .setNegativeButton("cancel"
            ) { dialogBox, id -> dialogBox.cancel() }
        val alertDialog = alertDialogBuilderUserInput.create()
        alertDialog.show()
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(View.OnClickListener {
            // Show toast message when no text is entered
            if (TextUtils.isEmpty(eventName.text.toString())) {

                Toast.makeText(this@EditProfileActivity, "Enter dialog_entry!", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            } else {
                alertDialog.dismiss()
            }
            if (shouldUpdate && EventItemModel != null) {
                updateToDoItem(eventName.text.toString(),eventAddress.text.toString(),eventDate.text.toString(),eventTime.text.toString(),eventDescription.text.toString(), EventItemModel.eventId!!)
            } else {
                createEventItem(eventName.text.toString(),eventAddress.text.toString(),eventTime.text.toString(),eventDate.text.toString(),eventDescription.text.toString())
            }
        })
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


