package com.example.statusshare

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.statusshare.R
import com.example.statusshare.Utils.Common
//import com.google.android.libraries.places.internal.e

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_main.*

class Registration: AppCompatActivity(){
    /*
    Need to store the following
        first name
        last name
        email
        password
     */

    //debuging
    private val TAG = "Registration Class"

    //global variables
    private var firstName: String? = null
    private var lastName: String? = null
    private var email: String? = null
    private var password: String? = null
    private var confirmPassword: String? = null

    //UI elements
    private lateinit var firstNameView: EditText
    private lateinit var lastNameView: EditText
    private lateinit var emailView: EditText
    private lateinit var passwordView: EditText
    private lateinit var confirmPasswordView: EditText
    private lateinit var registrationButton: Button
    private lateinit var cancelButton: Button



    //Firebase references
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null
    private val firebaseUser = FirebaseAuth.getInstance().currentUser


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
         initialise()

        val allUserButton= findViewById<Button>(R.id.AllUsersButton)

        allUserButton.setOnClickListener {

            val intent = Intent(this, AllUsers::class.java)
            startActivity(intent)
        }

        val searchFriend= findViewById<Button>(R.id.searchFriend)

        searchFriend.setOnClickListener {

            val intent = Intent(this, ActivityAllPeopleDriver::class.java)
            startActivity(intent)
        }



    }

    private fun initialise(){
        firstNameView = findViewById<View>(R.id.registrationFirstName) as EditText
        lastNameView = findViewById<View>(R.id.registrationLastName) as EditText
        emailView = findViewById<View>(R.id.registrationEmail) as EditText
        passwordView = findViewById<View>(R.id.registrationPassword) as EditText
        confirmPasswordView = findViewById<View>(R.id.registrationConfirmPassword) as EditText

        registrationButton = findViewById<View>(R.id.registrationRegisterButton) as Button
        cancelButton = findViewById<View>(R.id.registrationCancelButton) as Button

        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Registration q")
        mAuth = FirebaseAuth.getInstance()

        registrationButton.setOnClickListener { addRegistrationToTable() }
        cancelButton.setOnClickListener { clearButton() }

    }

    private fun addRegistrationToTable() {
        firstName = firstNameView.text.toString().trim()
        lastName = lastNameView.text.toString().trim()
        email = emailView.text.toString().trim()
        password = passwordView.text.toString().trim()
        confirmPassword = confirmPasswordView.text.toString().trim()


        if (TextUtils.isEmpty(firstName)) firstNameView.error = "You must enter your first name"

        if (TextUtils.isEmpty(lastName)) lastNameView.error = "You must enter your last name"

        if (TextUtils.isEmpty(email)) emailView.error = "You must enter an email"

        if (TextUtils.isEmpty(password)) passwordView.error = "You must enter a password"

        if (password!!.length < 6) Toast.makeText(this,"Your password must be more than 6 characters.",Toast.LENGTH_SHORT).show()

        if (TextUtils.isEmpty(confirmPassword)) confirmPasswordView.error = "You must confirm your password"

        if (!TextUtils.equals(password, confirmPassword)) confirmPasswordView.error = "Your passwords do not match"

        if (!TextUtils.isEmpty(firstName) && !TextUtils.isEmpty(lastName) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "No issues", Toast.LENGTH_SHORT).show()

            mAuth!!
                .createUserWithEmailAndPassword(email!!, password!!)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "registrationInfo:success")

                        val userId = mAuth!!.currentUser!!.uid
                        verifyEmail()
                        //update user registration information
                        val currentUserDb = mDatabaseReference!!.child(userId)
                        currentUserDb.child("firstName").setValue(firstName)
                        currentUserDb.child("lastName").setValue(lastName)
                        currentUserDb.child("email").setValue(email)
                        currentUserDb.child("password").setValue(password)
                        currentUserDb.child("switchState").setValue("off")
                        currentUserDb.child("customLocation").setValue("Newark, New Jersey")
                        currentUserDb.child("uid").setValue(userId)

                        updateToken(firebaseUser)
                        updateUserInfoAndUI()

                    } else {
                        // If registration in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(this, "Registration failed.", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "Enter all details", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUserInfoAndUI() {

        //start next activity
        val intent = Intent(this, Registration::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun verifyEmail() {
        val mUser = mAuth!!.currentUser
        mUser!!.sendEmailVerification()
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {
                    Toast.makeText(this,
                        "Verification email sent to " + mUser.email, Toast.LENGTH_SHORT).show()
                } else {
                    Log.e(TAG, "sendEmailVerification", task.exception)
                    Toast.makeText(this, "Failed to send verification email.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun clearButton(){
        firstNameView.setText("")
        lastNameView.setText("")
        emailView.setText("")
        passwordView.setText("")
        confirmPasswordView.setText("")
    }

    private fun updateToken(firebaseUser: FirebaseUser?){
        val tokens = FirebaseDatabase.getInstance().getReference(Common.TOKENS);

        //GET TOKEN
        FirebaseInstanceId.getInstance().instanceId
            .addOnSuccessListener { instanceIdResult ->
                tokens.child(firebaseUser!!.uid)
                    .setValue(instanceIdResult.token)
            }.addOnFailureListener{e -> Toast.makeText(this@Registration,e.message,Toast.LENGTH_SHORT).show()}

    }

}