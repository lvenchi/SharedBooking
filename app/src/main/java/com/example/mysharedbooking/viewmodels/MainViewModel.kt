package com.example.mysharedbooking.viewmodels

import android.app.Application
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import androidx.lifecycle.AndroidViewModel
import com.example.mysharedbooking.MainActivity
import com.example.mysharedbooking.R
import com.example.mysharedbooking.models.MySharedBookingDB
import com.facebook.AccessToken
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

    val user: MutableLiveData<User> = MutableLiveData()
    val newUser : MutableLiveData<String> = MutableLiveData("")
    val insertUser : MutableLiveData<String> = MutableLiveData()
    val showAllUsers: MutableLiveData<Boolean?> = MutableLiveData()
    val addNewBook: MutableLiveData<Boolean?> = MutableLiveData(false)
    val webResponses: MutableLiveData<String> = MutableLiveData("")
    val login: MutableLiveData<Boolean> = MutableLiveData()
    val logged: MutableLiveData<Boolean> = MutableLiveData()
    val fbAccessToken: MutableLiveData<AccessToken> = MutableLiveData()
    var fbId: String = application.getString(R.string.facebook_app_id)

    var profileImage: MutableLiveData<WeakReference<Drawable>> = MutableLiveData()

    val myDatabase: MySharedBookingDB = MainActivity.getInMemoryDatabase(application)

    fun loginWithGoogle(view: View){
        login.value = true
    }

    fun clicked( view: View ){
        insertUser.value = newUser.value
    }

    fun showall( view: View){
        showAllUsers.value = (showAllUsers.value)?.not()
    }

    fun addNewBooking( view: View ){
        addNewBook.value = addNewBook.value?.not()
    }

    fun callApi( view: View){
        viewModelScope.launch (Dispatchers.IO) {
            val restResponse = RESTOperations.Operations.registerUser( newUser.value!! )
            if (restResponse != HttpURLConnection.HTTP_CREATED) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(view.context, "Failed to Insert", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}