package com.example.mysharedbooking.viewmodels


import android.app.Application
import androidx.lifecycle.*
import com.example.mysharedbooking.MainActivity
import com.example.mysharedbooking.models.MySharedBookingDB
import com.example.mysharedbooking.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UsersViewModel(application: Application) : AndroidViewModel(application) {

    private val userList: MutableLiveData<List<User>> = MutableLiveData()
    private val myDatabase : MySharedBookingDB = MainActivity.getInMemoryDatabase(application)

    fun getUserList(): MutableLiveData<List<User>> {
        if(userList.value.isNullOrEmpty()) loadUsers()
        return userList
    }

    //Loads Users from local Database
    private fun loadUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            userList.postValue( myDatabase.myDao().getAllUsers() )
                //RESTOperations.Operations.getUsers()
        }
    }
}