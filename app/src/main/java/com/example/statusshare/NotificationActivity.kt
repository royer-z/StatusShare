package com.example.statusshare

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
//import androidx.fragment.app.Fragment


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [NotificationActivity.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [NotificationActivity.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class NotificationActivity : Fragment() {
    // TODO: Rename and change types of parameters

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.activity_notify, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val plusButton = view.findViewById<Button>(R.id.notificationsCreateButton)

        plusButton.setOnClickListener(){
            val intent = Intent(getActivity(), EditNotificationActivity::class.java)
            getActivity()?.startActivity(intent)
        }
    }

}
