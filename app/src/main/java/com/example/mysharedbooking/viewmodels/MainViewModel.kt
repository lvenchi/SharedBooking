package com.example.mysharedbooking.viewmodels

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mysharedbooking.models.User

class MainViewModel : ViewModel(){

    val user: MutableLiveData<User> = MutableLiveData()
    val newUser : MutableLiveData<String> = MutableLiveData("")
    val insertUser : MutableLiveData<String> = MutableLiveData()
    val showAllUsers: MutableLiveData<Boolean?> = MutableLiveData()
    val addNewBook: MutableLiveData<Boolean?> = MutableLiveData(false)

    fun clicked( view: View){
        insertUser.value = newUser.value
    }

    fun showall( view: View){
        showAllUsers.value = (showAllUsers.value)?.not()
    }

    fun addNewBooking( view: View){
        addNewBook.value = addNewBook.value?.not()
    }
}