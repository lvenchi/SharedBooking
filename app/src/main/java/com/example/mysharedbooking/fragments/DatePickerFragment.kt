package com.example.mysharedbooking.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.mysharedbooking.MainActivity
import java.util.*

class DatePickerFragment() : DialogFragment(){

    lateinit var mainactivity: MainActivity
    lateinit var listener: DatePickerDialog.OnDateSetListener

    constructor(mainctivity: MainActivity, Listener: DatePickerDialog.OnDateSetListener) : this() {
        mainactivity = mainctivity
        listener = Listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val dpd = DatePickerDialog(mainactivity, null, year, month, day)
        dpd.setOnDateSetListener(listener)
        // Create a new instance of DatePickerDialog and return it
        return dpd
    }

}