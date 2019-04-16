package com.example.statusshare

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
class CustomAdapter : ArrayAdapter<SpinnerItem> {
    private var CustomSpinnerItems: List<SpinnerItem>
    constructor(context: Context, resource: Int, pickerItems: List<SpinnerItem>) : super(context, resource, pickerItems){this.CustomSpinnerItems = pickerItems}
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return CustomSpinnerView(position, convertView, parent)
    }
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return CustomSpinnerView(position, convertView, parent)
    }
    private fun CustomSpinnerView(position: Int, convertView: View?, parent: ViewGroup): View {
        //Getting the Layout Inflater Service from the system
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        //Inflating out custom spinner view
        val customView = layoutInflater.inflate(R.layout.status_availability_spinner, parent, false)
        //Declaring and initializing the widgets in custom layout
        val imageView = customView.findViewById(R.id.status_color) as ImageView
        val textView = customView.findViewById(R.id.pickerText) as TextView
        //displaying the data
        //drawable items are mapped with name prefixed with 'zx_' also image names are containing underscore instead of spaces.
        //val imageRef = "zx_" + CustomSpinnerItems[position].name!!.toLowerCase().replace(" ", "_")

        var imageRef = ""
        if(CustomSpinnerItems[position].statusWord == "text, call, and meet")
            imageRef = "availability_color_green"
        if(CustomSpinnerItems[position].statusWord == "text and call")
            imageRef = "availability_color_yellow"
        if(CustomSpinnerItems[position].statusWord == "text")
            imageRef = "availability_color_orange"
        if(CustomSpinnerItems[position].statusWord == "do not disturb")
            imageRef = "availability_color_red"
        val resID = context.resources.getIdentifier(imageRef, "drawable", context.packageName)

        imageView.setImageResource(resID)
        //imageView.setImageDrawable();geResource(getApplicationContext(),getImageId(CustomSpinnerItems.get(position).getName()));
        textView.setText(CustomSpinnerItems[position].statusWord)
        return customView
    }
}