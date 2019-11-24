package com.example.mysharedbooking.viewmodels

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mysharedbooking.helpers.RESTOperations
import com.example.mysharedbooking.models.Booking
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserBookingViewModel(application: Application) :AndroidViewModel(application){

    val bookingList: MutableLiveData<List<Booking>> = MutableLiveData()
    val userId: MutableLiveData<String> = MutableLiveData("0")
    val bookingId: MutableLiveData<String> = MutableLiveData("0")

    fun getBookingsByUserId( view: View){
        viewModelScope.launch(Dispatchers.IO) { bookingList.postValue(RESTOperations.Operations.getUserClientBooking(userId = userId.value!!.toLong())) }
    }

    fun insertBookBooking( view: View){
        viewModelScope.launch(Dispatchers.IO) {
            RESTOperations.Operations.registerUserBooking(userId = userId.value!!.toLong(), bookingId = bookingId.value!!.toLong())
        }
    }

}