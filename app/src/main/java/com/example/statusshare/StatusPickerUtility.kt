package com.example.statusshare

import java.util.*
internal class Utility {
    companion object {
        val statuses: List<SpinnerItem> = object : ArrayList<SpinnerItem>() {
            init {
                add(SpinnerItem("text, call, and meet"))//green
                add(SpinnerItem("text and call"))//yellow
                add(SpinnerItem("text"))//orange
                add(SpinnerItem("do not disturb"))//red
            }
        }
    }
}
