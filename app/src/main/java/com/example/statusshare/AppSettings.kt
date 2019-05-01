package com.example.statusshare

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent



//import androidx.fragment.app.Fragment


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [AppSettings.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [AppSettings.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class AppSettings : Fragment() {
    // TODO: Rename and change types of parameters

    var fbAuth = FirebaseAuth.getInstance()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.app_settings, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var logoutButton = view.findViewById<Button>(R.id.settingsLogoutButton)

        logoutButton.setOnClickListener {
            Toast.makeText(activity,"Logging out",Toast.LENGTH_SHORT).show()
            signOut()
        }

        fbAuth.addAuthStateListener {
            if(fbAuth.currentUser == null){
                val i = Intent(activity, LoginActivity::class.java)
                startActivity(i)
            }
        }

    }

    private fun signOut() {
        fbAuth.signOut()
    }







}
