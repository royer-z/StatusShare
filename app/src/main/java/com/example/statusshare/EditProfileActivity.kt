package com.example.statusshare

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
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

class EditProfileActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    var mDatabase: DatabaseReference? = null
    var mCurrentUser: FirebaseUser? = null
    var GALLERY_ID: Int = 1
    var mStorageRef: StorageReference? = null
    lateinit var statusSpinner: Spinner
    lateinit var status_name_code: String
    private var mAdapter: FirebaseRecyclerAdapter<EventItemModel, EventViewHolder>? = null

    var statusColorNum = 9999


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_edit_profile)
        val mainLayout = layoutInflater.inflate(R.layout.activity_edit_profile, null)
        setContentView(mainLayout)


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
        val fab = findViewById<View>(R.id.editProfilePlusButton)
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


