package com.example.mysharedbooking.viewmodels

import android.app.Application
import android.view.View
import android.widget.Toast
import com.example.mysharedbooking.helpers.RESTOperations
import com.example.mysharedbooking.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.*
import com.example.mysharedbooking.MainActivity
import com.example.mysharedbooking.SharedBookingRepository
import com.example.mysharedbooking.models.Booking
import com.example.mysharedbooking.models.MySharedBookingDB
import com.example.mysharedbooking.models.UserBooking
import com.facebook.AccessToken
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.lang.ref.WeakReference
import kotlin.coroutines.coroutineContext


@BindingAdapter("android:src")
fun setImageDrawable(view: ImageView, drawable: Drawable?) {
    drawable!= null?: view.setImageDrawable(drawable)
}


class MainViewModel(application: Application) : AndroidViewModel(application){

    private lateinit var sharedBookingRepository: SharedBookingRepository

    val firebaseDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference

    val currentUser: MutableLiveData<User> = MutableLiveData()
    val addNewBook: MutableLiveData<Boolean?> = MutableLiveData(false)
    val login: MutableLiveData<Boolean> = MutableLiveData()
    val logged: MutableLiveData<Boolean> = MutableLiveData()
    val fbAccessToken: MutableLiveData<AccessToken> = MutableLiveData()
    val firebaseUser: MutableLiveData<FirebaseUser?> = MutableLiveData()

    var bookingList : LiveData<List<Booking>> = MutableLiveData()
    var availableBookingList : LiveData<List<Booking>> = MutableLiveData()
    var myBookedBookingList : LiveData<List<Booking>> = MutableLiveData()

    var profileImage: MutableLiveData<WeakReference<Drawable>> = MutableLiveData()


    fun initRepo(userEmail : String){
        sharedBookingRepository = SharedBookingRepository(this.getApplication(), userEmail)
        myBookedBookingList = sharedBookingRepository.getBookingsOfUser()
        bookingList = sharedBookingRepository.getOwningBookings()
        availableBookingList = sharedBookingRepository.getBookableBookings()

    }

    fun getMyBookedBookings(): LiveData<List<Booking>>{
        return myBookedBookingList
    }

    fun getOwnedBookings(): LiveData<List<Booking>>{
        return bookingList
    }

    fun getAvailableBookings(): LiveData<List<Booking>>{
        return availableBookingList
    }

    fun insertUsers( userList: ArrayList<User>){
        sharedBookingRepository.insertUsers(userList, viewModelScope)
    }

    fun insertUserBookings( userList: ArrayList<UserBooking>){
        val currentUserBooking = ArrayList<UserBooking>()

        sharedBookingRepository.insertUserBookings(userList, viewModelScope)
    }

    fun insertBookings( bookingList: ArrayList<Booking>){
        sharedBookingRepository.insertNewBookings(bookingList, viewModelScope)
    }

    fun getLinkProfilePicByUserId(userEmail: String): String{
        return sharedBookingRepository.getUserProfilePicUrl(userEmail)
    }

    fun insertMyBooking(userBooking: UserBooking){
        sharedBookingRepository.insertNewUserBooking(userBooking, viewModelScope)

        if(firebaseUser.value?.uid != null) firebaseDatabase.child("userbookings")
            .child(userBooking.bookingId.toString()+"/"+currentUser.value!!.firebaseId).push().setValue(userBooking )
    }

    fun removeUserBooking( userBooking: UserBooking){
        sharedBookingRepository.removeBookedBooking(userBooking, viewModelScope)
        if(firebaseUser.value?.uid != null) firebaseDatabase.child("userbookings")
            .child(userBooking.bookingId.toString()+"/"+currentUser.value!!.firebaseId)
            .removeValue()
    }

    fun removeBooking( booking: Booking ){
        sharedBookingRepository.removeBooking( booking, viewModelScope)
        if(firebaseUser.value?.uid != null) firebaseDatabase.child("bookings")
            .child(firebaseUser.value?.uid.toString()+booking.id)
            .removeValue()
        if(firebaseUser.value?.uid != null) firebaseDatabase.child("userbookings")
            .child(booking.id.toString()).removeValue()

    }

    fun addNewBooking( view: View ){
        addNewBook.value = addNewBook.value?.not()
    }

    fun insertUser( user: User){
        if(user.firebaseId != null) firebaseDatabase.child("users").child(user.firebaseId).setValue(user)
    }

    fun insertBooking( booking: Booking){
        if(firebaseUser.value?.uid != null) firebaseDatabase.child("bookings")
            .child(firebaseUser.value?.uid.toString()+booking.id)
            .setValue(booking)
    }

    //clears the components and observers
    fun resetLists( lifecycleOwner: LifecycleOwner ){
        bookingList.removeObservers(lifecycleOwner)
        availableBookingList.removeObservers(lifecycleOwner)
        myBookedBookingList.removeObservers(lifecycleOwner)

        bookingList.value?.toMutableSet()?.clear()
        availableBookingList.value?.toMutableSet()?.clear()
        myBookedBookingList.value?.toMutableSet()?.clear()

    }
}