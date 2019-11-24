package com.example.mysharedbooking

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.mysharedbooking.models.Booking
import com.example.mysharedbooking.models.User
import com.example.mysharedbooking.models.daos.UserBookingDao
import com.example.mysharedbooking.models.UserBooking
import com.example.mysharedbooking.models.daos.SharedBookingDBDao
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SharedBookingRepository(application: Application, userEmail: String) {

    private var userBookingDao: UserBookingDao
    private var myDatabaseDao: SharedBookingDBDao


    private var userBookedBookings: LiveData<List<Booking>>
    private var owningBookings : LiveData<List<Booking>>
    private var bookableBookings : LiveData<List<Booking>>
    private val firebaseDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference

    init {

        val myDataBase = MainActivity.getInMemoryDatabase(application)
        userBookingDao = myDataBase.userBookingDao()
        myDatabaseDao = myDataBase.myDao()
        userBookedBookings = userBookingDao.getBookingsOfUserClient(userEmail)
        owningBookings = userBookingDao.getOwningBookings(userEmail)
        bookableBookings = userBookingDao.getNotOwningBookings(userEmail)

    }

    fun insertUsers( userList: ArrayList<User>, mainViewModelScope: CoroutineScope ){
        mainViewModelScope.launch( Dispatchers.IO) {
            for( u: User in userList){
                myDatabaseDao.insertUsers(u)
            }
        }
    }

    fun insertNewBookings( bookingList: ArrayList<Booking>, mainViewModelScope: CoroutineScope ){
        mainViewModelScope.launch( Dispatchers.IO) {
            val currentBookings = myDatabaseDao.getAllBooks()

            for( b: Booking in currentBookings){
                if(!bookingList.contains(b)){
                    myDatabaseDao.deleteBooking(b)
                }
            }

            for( b: Booking in bookingList){
                myDatabaseDao.insertBooking(b)
            }
        }
    }

    fun insertUserBookings( bookingList: ArrayList<UserBooking>, mainViewModelScope: CoroutineScope ){
        mainViewModelScope.launch( Dispatchers.IO) {
            for( b: UserBooking in bookingList){
                userBookingDao.insert(b)
            }
        }
    }

    fun getUserProfilePicUrl( userEmail: String) : String{
        return myDatabaseDao.getPhotoLinkByUserEmail(userEmail)
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

    fun removeBookedBooking( userBooking: UserBooking, mainViewModelScope: CoroutineScope){
        mainViewModelScope.launch (Dispatchers.IO){
            userBookingDao.removeUserBooking(userBooking)


        }
    }

    fun removeBooking( booking: Booking, mainViewModelScope: CoroutineScope){
        mainViewModelScope.launch (Dispatchers.IO){
            myDatabaseDao.deleteBooking(booking)
            //firebaseDatabase.child("booking")
        }
    }

    fun insertNewBooking( newBooking: Booking, mainViewModelScope: CoroutineScope){
        mainViewModelScope.launch (Dispatchers.IO) {
            myDatabaseDao.insertBooking(newBooking)
        }
    }

    fun insertNewUserBooking(userBooking: UserBooking, mainViewModelScope: CoroutineScope){
        mainViewModelScope.launch (Dispatchers.IO){
            userBookingDao.insert(userBooking = userBooking)
        }
    }
}