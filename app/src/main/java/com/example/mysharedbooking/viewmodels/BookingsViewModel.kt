package com.example.mysharedbooking.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mysharedbooking.models.Booking
import com.example.mysharedbooking.models.UserBooking

class BookingsViewModel(application: Application) : AndroidViewModel(application){
    val bookingList: MutableLiveData<List<UserBooking>> = MutableLiveData()


    init {

    }
}