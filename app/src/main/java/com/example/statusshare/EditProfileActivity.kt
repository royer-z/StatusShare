package com.example.statusshare

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.*
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_edit_profile.*

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
