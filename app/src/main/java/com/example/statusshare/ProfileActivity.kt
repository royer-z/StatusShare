package com.example.statusshare

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_profile.*
import android.util.Log

//import com.google.firebase.storage.StorageReference
class ProfileActivity : AppCompatActivity() {

    var mDatabase: DatabaseReference? = null
    var mCurrentUser: FirebaseUser? = null
    var mStorageRef:StorageReference?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        //colorStatus pic
        val colorStatusPic = findViewById<ImageView>(R.id.profileAvailabilityColor)

        mCurrentUser = FirebaseAuth.getInstance().currentUser

        var userID = mCurrentUser!!.uid

        mDatabase = FirebaseDatabase.getInstance().reference
            .child("Registration q")
            .child(userID)

        mDatabase!!.addValueEventListener(object : ValueEventListener{
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


        profileUpdateProfileButton.setOnClickListener{
            var intent = Intent(this, EditProfileActivity::class.java)
            intent.putExtra("status",profileStatus.text.toString())
            intent.putExtra("location",profileLocationHeading.text.toString())
            intent.putExtra("destination",profileDestinationHeading.text.toString())
            startActivity(intent)
        }
    }
}
