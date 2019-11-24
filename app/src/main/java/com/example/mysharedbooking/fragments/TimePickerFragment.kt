package com.example.mysharedbooking.fragments

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import androidx.fragment.app.DialogFragment
import com.example.mysharedbooking.MainActivity
import java.util.*

class TimePickerFragment(mActivity: MainActivity, Listener: TimePickerDialog.OnTimeSetListener) : DialogFragment() {

    val mainactivity: MainActivity = mActivity
    val listener: TimePickerDialog.OnTimeSetListener = Listener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        // Create a new instance of TimePickerDialog and return it
        return TimePickerDialog(mainactivity, listener, hour, minute, DateFormat.is24HourFormat(activity))
    }
}