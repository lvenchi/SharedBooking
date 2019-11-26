package com.example.mysharedbooking.viewmodels

import android.app.Application
import android.view.View
import androidx.databinding.*
import androidx.lifecycle.MutableLiveData

import com.example.mysharedbooking.models.Booking
import java.util.*
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@BindingAdapter("app:text")
fun setText(view: TextView, text: CharSequence) {
    // long to formatteed date
    view.text = android.text.format.DateFormat.format("dd-MM-yyyy hh:mm a", Date(text.toString()))
}

class NewBookingViewModel(application: Application) : AndroidViewModel(application) {

    val newBooking: MutableLiveData<Booking> = MutableLiveData()
    val showDatePick: MutableLiveData<Int> = MutableLiveData()
    val type: MutableLiveData<String> = MutableLiveData("")
    val date: MutableLiveData<String> = MutableLiveData( Date().toString())
    val userEmail: MutableLiveData<String> = MutableLiveData("")
    val calendar: MutableLiveData<Calendar> = MutableLiveData(Calendar.getInstance())

    fun insertNewBooking( view: View){
        viewModelScope.launch (Dispatchers.IO) {
                val booking = Booking( Calendar.getInstance().timeInMillis, type.value.toString(), calendar.value!!.timeInMillis, false, userEmail.value!!)
            //database.myDao().insertBooking(booking)
                newBooking.postValue(booking)
        }
    }

    fun ShowPick(view: View){
        showDatePick.postValue(0)
    }

    fun updateDate(newDate: String){
        date.value = newDate
    }

    fun longToString(
        oldValue: Long
    ): String {
        // Converts String to long.
        return oldValue.toString()
    }

    @InverseMethod("longToString")
    fun stringToLong(
        oldValue: String
    ): Long {
        // Converts long to String.
        return if(oldValue.isNotEmpty()) oldValue.toLong() else 0
    }
}

