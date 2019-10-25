package com.example.mysharedbooking.viewmodels


import android.app.Application
import androidx.lifecycle.*
import com.example.mysharedbooking.MainActivity
import com.example.mysharedbooking.models.MySharedBookingDB
import com.example.mysharedbooking.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UsersViewModel(application: Application) : AndroidViewModel(application) {

    private val myDatabase: MySharedBookingDB = MainActivity.getInMemoryDatabase(application)

    private val userList: MutableLiveData<List<User>> = MutableLiveData()

    fun getUserList(): MutableLiveData<List<User>> {
        if(userList.value.isNullOrEmpty()) loadUsers()
        return userList
    }

    //Loads Users from local Database
    private fun loadUsers() {
        viewModelScope.launch(Dispatchers.Main) { userList.value =
            withContext(Dispatchers.IO) {
                myDatabase.myDao().getAllUsers()
            }
        }
    }
}