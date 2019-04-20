package com.example.statusshare

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup



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
}
