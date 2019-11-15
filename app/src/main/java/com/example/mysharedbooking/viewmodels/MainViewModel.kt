package com.example.mysharedbooking.viewmodels

import android.app.Application
import android.view.View
import android.widget.Toast
import com.example.mysharedbooking.RESTOperations
import com.example.mysharedbooking.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import android.graphics.drawable.Drawable
import android.util.JsonReader
import android.util.Xml
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.*
import androidx.recyclerview.widget.RecyclerView
import com.example.mysharedbooking.MainActivity
import com.example.mysharedbooking.R
import com.example.mysharedbooking.SharedBookingRepository
import com.example.mysharedbooking.models.Booking
import com.example.mysharedbooking.models.MySharedBookingDB
import com.example.mysharedbooking.models.UserBooking
import com.facebook.AccessToken
import com.google.android.gms.tasks.Tasks.await
import kotlinx.coroutines.async
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.lang.Exception
import java.lang.StringBuilder
import java.lang.ref.WeakReference
import java.net.MalformedURLException
import java.net.URL
import java.nio.ByteBuffer
import java.nio.charset.Charset
import javax.net.ssl.HttpsURLConnection


@BindingAdapter("android:src")
fun setImageDrawable(view: ImageView, drawable: Drawable?) {
    drawable!= null?: view.setImageDrawable(drawable)
}


class MainViewModel(application: Application) : AndroidViewModel(application){

    private lateinit var sharedBookingRepository: SharedBookingRepository

    val currentUser: MutableLiveData<User> = MutableLiveData()
    val addNewBook: MutableLiveData<Boolean?> = MutableLiveData(false)
    val webResponses: MutableLiveData<String> = MutableLiveData("")
    val login: MutableLiveData<Boolean> = MutableLiveData()
    val logged: MutableLiveData<Boolean> = MutableLiveData()
    val fbAccessToken: MutableLiveData<AccessToken> = MutableLiveData()

    var bookingList : LiveData<List<Booking>> = MutableLiveData()
    var availableBookingList : LiveData<List<Booking>> = MutableLiveData()
    var myBookedBookingList : LiveData<List<Booking>> = MutableLiveData()

    var profileImage: MutableLiveData<WeakReference<Drawable>> = MutableLiveData()

    val myDatabase: MySharedBookingDB = MainActivity.getInMemoryDatabase(application)

    fun initRepo(uid : Long){
        sharedBookingRepository = SharedBookingRepository(this.getApplication(), uid)
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

    fun getLinkProfilePicByUserId(userId: Long): String{
        return sharedBookingRepository.getUserProfilePicUrl(userId)
    }

    fun insertMyBooking(userBooking: UserBooking){
        sharedBookingRepository.insertNewUserBooking(userBooking, viewModelScope)
    }

    fun removeUserBooking( userBooking: UserBooking){
        sharedBookingRepository.removeBookedBooking(userBooking, viewModelScope)
    }

    fun removeBooking( booking: Booking ){
        sharedBookingRepository.removeBooking( booking, viewModelScope)
    }

    fun loginWithGoogle(view: View){
        login.value = true
    }

    fun addNewBooking( view: View ){
        addNewBook.value = addNewBook.value?.not()
    }

    fun callApi( view: View){
        viewModelScope.launch (Dispatchers.IO) {
            val restResponse = RESTOperations.Operations.registerUser( currentUser.value!!.email!! )
            if (restResponse != HttpURLConnection.HTTP_CREATED) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(view.context, "Failed to Insert", Toast.LENGTH_SHORT).show()
                }
            }
        }
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