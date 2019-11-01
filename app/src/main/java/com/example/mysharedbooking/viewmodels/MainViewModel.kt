package com.example.mysharedbooking.viewmodels

import android.app.Application
import android.util.JsonReader
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mysharedbooking.RESTOperations
import com.example.mysharedbooking.models.User
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import androidx.core.app.ActivityCompat.startActivityForResult
import android.content.Intent
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn


class MainViewModel : ViewModel(){

    val user: MutableLiveData<User> = MutableLiveData()
    val newUser : MutableLiveData<String> = MutableLiveData("")
    val insertUser : MutableLiveData<String> = MutableLiveData()
    val showAllUsers: MutableLiveData<Boolean?> = MutableLiveData()
    val addNewBook: MutableLiveData<Boolean?> = MutableLiveData(false)
    val webResponses: MutableLiveData<String> = MutableLiveData("")
    val login: MutableLiveData<Boolean> = MutableLiveData()
    val logged: MutableLiveData<Boolean> = MutableLiveData(false)

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