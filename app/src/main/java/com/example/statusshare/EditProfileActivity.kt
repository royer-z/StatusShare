package com.example.statusshare

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_edit_profile.*

class EditProfileActivity : AppCompatActivity() {

    var mDatabase: DatabaseReference? = null
    var mCurrentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

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
                    Toast.makeText(this,"Status Updated Successfully!",Toast.LENGTH_LONG).show()
                    startActivity(Intent(this,ProfileActivity::class.java))
                } else {
                    Toast.makeText(this,"Status Not Updated",Toast.LENGTH_LONG).show()
                }
            }

            var location = editProfileLocation.text.toString()
            mDatabase!!.child("location").setValue(location).addOnCompleteListener{
                    task: Task<Void> ->
                if (task.isSuccessful){
                    Toast.makeText(this,"Location Updated Successfully!",Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this,"Location Not Updated",Toast.LENGTH_LONG).show()
                }
            }

            var destination = editProfileDestination.text.toString()
            mDatabase!!.child("destination").setValue(destination).addOnCompleteListener{
                    task: Task<Void> ->
                if (task.isSuccessful){
                    Toast.makeText(this,"Destination Updated Successfully!",Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this,"Destination Not Updated",Toast.LENGTH_LONG).show()
                }
            }



        }
    }
}
