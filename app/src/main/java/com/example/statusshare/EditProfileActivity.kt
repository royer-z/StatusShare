package com.example.statusshare

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.*
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

            override fun onDataChange(dataSnapshot: DataSnapshot?) {

                var image = dataSnapshot!!.child("image").value.toString()
                var thumbnail = dataSnapshot!!.child("thumb_image").value

                if (!image!!.equals("null")) {
                    Picasso.with(applicationContext)
                        .load(image)
                        .placeholder(R.drawable.default_profile_image)
                        .into(editProfileProfileImageButton)



                }


            }

            override fun onCancelled(databaseErrorSnapshot: DatabaseError?) {

            }

        })





        statusSpinner = mainLayout.spinner_statuses

        val countryPickerData = Utility.statuses
        val pickerAdapter = CustomAdapter(this@EditProfileActivity, R.layout.status_availability_spinner, countryPickerData)
        statusSpinner.setAdapter(pickerAdapter)

        statusSpinner.setSelection(1)

        status_name_code = countryPickerData.get(0).statusWord!!

        if(intent.extras!=null){
            var oldStatus = intent.extras.get("status")
            var oldLocation = intent.extras.get("location")
            var oldDestination = intent.extras.get("destination")

            editProfileStatus.setText(oldStatus.toString())
            editProfileLocation.setText(oldLocation.toString())
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
                    //Toast.makeText(this,"Status Updated Successfully!",Toast.LENGTH_LONG).show()
                    startActivity(Intent(this,whatever::class.java))
                } else {
                    //Toast.makeText(this,"Status Not Updated",Toast.LENGTH_LONG).show()
                }
            }

            var location = editProfileLocation.text.toString()
            mDatabase!!.child("location").setValue(location).addOnCompleteListener{
                    task: Task<Void> ->
                if (task.isSuccessful){
                    //Toast.makeText(this,"Location Updated Successfully!",Toast.LENGTH_LONG).show()
                } else {
                    //Toast.makeText(this,"Location Not Updated",Toast.LENGTH_LONG).show()
                }
            }

            var destination = editProfileDestination.text.toString()
            mDatabase!!.child("destination").setValue(destination).addOnCompleteListener{
                    task: Task<Void> ->
                if (task.isSuccessful){
                    //Toast.makeText(this,"Destination Updated Successfully!",Toast.LENGTH_LONG).show()
                } else {
                    //Toast.makeText(this,"Destination Not Updated",Toast.LENGTH_LONG).show()
                }
            }

            mDatabase!!.child("colorStatus").setValue(statusColorNum)

        }

        editProfileProfileImageButton.setOnClickListener{
            var galleryIntent = Intent()
            galleryIntent.type = "image/*"
            galleryIntent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(galleryIntent,"SELECT_IMAGE"),GALLERY_ID)
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

    }


    override fun onItemSelected(arg0: AdapterView<*>, arg1: View, position: Int, id: Long) {
        //textView_msg!!.text = "Selected : "+languages[position]


        //statusColorNum = position
    }

    override fun onNothingSelected(arg0: AdapterView<*>) {

    }





    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == GALLERY_ID &&  resultCode == Activity.RESULT_OK){

            var image: Uri = data!!.data

            CropImage.activity(image)
                .setAspectRatio(1,1)
                .start(this)

        }

        if(requestCode === CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            val result = CropImage.getActivityResult(data)

            if(resultCode===Activity.RESULT_OK){
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
                thumbBitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArray)
                var thumbByteArray: ByteArray
                thumbByteArray = byteArray.toByteArray()

                var filePath = mStorageRef!!.child("profile_image")
                    .child(userID + ".jpg")

                //Create another directory for compressed/smaller images

                var thumbFilePath = mStorageRef!!.child("profile_image")
                    .child("thumb")
                    .child(userID+".jpg")

                filePath.putFile(resultUri)
                    .addOnCompleteListener{
                            task : Task<UploadTask.TaskSnapshot> ->
                        if(task.isSuccessful){

                            //Gets pic url
                            var donwloadUrl = task.result.downloadUrl.toString()
                            //var donwloadUrl =

                            var uploadTask: UploadTask = thumbFilePath
                                .putBytes(thumbByteArray)

                            uploadTask.addOnCompleteListener{
                                    task: Task<UploadTask.TaskSnapshot> ->

                                var thumbUrl = task.result.downloadUrl.toString()
                                if (task.isSuccessful) {
                                    var updateObj = HashMap<String, Any>()
                                    updateObj.put("image", donwloadUrl)
                                    updateObj.put("thumb_image", thumbUrl)

                                    //Saves profile image
                                    mDatabase!!.updateChildren(updateObj)
                                        .addOnCompleteListener{
                                                task: Task<Void> ->
                                            if(task.isSuccessful) {
                                                //Toast.makeText(this,"Profile Image Saved",Toast.LENGTH_LONG).show()
                                            } else {

                                            }
                                        }
                                } else {

                                }
                            }

                        }
                    }


            }
        }
        //super.onActivityResult(requestCode, resultCode, data)
    }
}
