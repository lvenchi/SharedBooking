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
import kotlinx.coroutines.withContext

class BookingsViewModel(application: Application) : AndroidViewModel(application){
    val clientBookingList: MutableLiveData<List<Booking>> = MutableLiveData()
    val userId: MutableLiveData<String> = MutableLiveData("0")



    init {

    }

    fun getBookingsByUserId( view: View ){
        when {
            userId.value!!.toLongOrNull() != 0L && userId.value!!.toLongOrNull() != null -> viewModelScope.launch { getAllBookings( userId = userId.value!!.toLong() ) }
        }
    }

    private suspend fun getAllBookings( userId: Long ) = withContext(Dispatchers.IO){
        clientBookingList.postValue( RESTOperations.Operations.getUserOwnerBooking(userId))//myDatabase.userBookingDao().getBookingsOfUserClient(userId).toList())
    }
}