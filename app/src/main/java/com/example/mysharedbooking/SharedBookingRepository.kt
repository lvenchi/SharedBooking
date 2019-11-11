package com.example.mysharedbooking

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.mysharedbooking.models.Booking
import com.example.mysharedbooking.models.daos.UserBookingDao
import android.os.AsyncTask
import androidx.lifecycle.viewModelScope
import com.example.mysharedbooking.models.UserBooking
import com.example.mysharedbooking.models.daos.SharedBookingDBDao
import com.example.mysharedbooking.viewmodels.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class SharedBookingRepository(application: Application, uid: Long) {

    private var userBookingDao: UserBookingDao
    private var myDatabaseDao: SharedBookingDBDao

    private var userBookedBookings: LiveData<List<Booking>>
    private var owningBookings : LiveData<List<Booking>>
    private var bookableBookings : LiveData<List<Booking>>

    init {
        val myDataBase = MainActivity.getInMemoryDatabase(application)
        userBookingDao = myDataBase.userBookingDao()
        myDatabaseDao = myDataBase.myDao()
        userBookedBookings = userBookingDao.getBookingsOfUserClient(uid)
        owningBookings = userBookingDao.getOwningBookings(uid)
        bookableBookings = userBookingDao.getNotOwningBookings(uid)
    }

    fun getUserProfilePicUrl( userId: Long) : String{
        return myDatabaseDao.getPhotoLinkByUserId(userId)
    }

    fun getBookingsOfUser(): LiveData<List<Booking>>{
        return userBookedBookings
    }

    fun getOwningBookings(): LiveData<List<Booking>>{
        return owningBookings
    }

    fun getBookableBookings(): LiveData<List<Booking>>{
        return bookableBookings
    }

    fun insertBookedBooking( userBooking: UserBooking){
        insertUserBookingAsyncTask(userBookingDao).execute(userBooking)
    }

    fun insertNewBooking( newBooking: Booking){
        insertNewBookingAsyncTask(myDatabaseDao).execute(newBooking)
    }

    fun insertNewUserBooking(userBooking: UserBooking, mainViewModelScope: CoroutineScope){
        mainViewModelScope.launch (Dispatchers.IO){
            userBookingDao.insert(userBooking = userBooking)
        }
    }

    private class insertUserBookingAsyncTask internal constructor(private val mAsyncTaskDao: UserBookingDao) :
        AsyncTask<UserBooking, Void, Void>() {

        override fun doInBackground(vararg params: UserBooking): Void? {
            mAsyncTaskDao.insert(params[0])
            return null
        }
    }

    private class insertNewBookingAsyncTask internal constructor(private val mAsyncTaskDao: SharedBookingDBDao) :
        AsyncTask<Booking, Void, Void>() {

        override fun doInBackground(vararg params: Booking): Void? {
            mAsyncTaskDao.insertBooking(params[0])
            return null
        }
    }
}