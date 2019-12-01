package com.example.mysharedbooking.helpers

import com.example.mysharedbooking.models.Booking
import com.example.mysharedbooking.models.User
import com.example.mysharedbooking.models.UserBooking
import com.example.mysharedbooking.viewmodels.MainViewModel
import com.google.firebase.database.*

class FirebaseUtils(val mainViewModel: MainViewModel) {


    private val firebaseDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference

    private lateinit var userUpdateListener: ValueEventListener
    private lateinit var bookingUpdateListener: ValueEventListener
    private lateinit var userbookingUpdateListener: ValueEventListener

    fun updateUserToken( token: String?, user: User ){

        val updatedUser = HashMap<String, Any>()
        updatedUser["email"] = user.email
        updatedUser["firebaseId"] = user.firebaseId!!
        updatedUser["firstName"] = user.firstName!!
        updatedUser["lastName"] = user.lastName!!
        updatedUser["username"] = user.username!!
        updatedUser["profilePic"] = user.profilePic!!
        updatedUser["role"] = user.role!!
        updatedUser["firebasetoken"] = token!!

        firebaseDatabase.child("users/"+user.firebaseId).updateChildren(
            updatedUser
        )
    }

    fun unListenUsersUpdates(){
        firebaseDatabase.child("users").removeEventListener(userUpdateListener)
    }

    fun unListenBookingUpdates(){
        firebaseDatabase.child("users").removeEventListener(bookingUpdateListener)
    }

    fun unListenUserBookingUpdates(){
        firebaseDatabase.child("userbookings").removeEventListener(userbookingUpdateListener)
    }

    fun listenUsersUpdates(){

        userUpdateListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val userList = ArrayList<User>()
                for( dsn: DataSnapshot in p0.children) {
                    val newUser = dsn.value as Map<*, *>
                    userList.add(
                        User(
                            email = newUser["email"] as String,
                            firebaseId = newUser["firebaseId"] as String,
                            firstName = newUser["firstName"] as String,
                            lastName = newUser["lastName"] as String,
                            username = newUser["username"] as String,
                            profilePic = newUser["profilePic"] as String,
                            role = newUser["role"] as String,
                            firebaseToken = newUser["firebasetoken"] as String
                        )
                    )
                }
                mainViewModel.insertUsers(userList)
            }
        }
        firebaseDatabase.child("users").addValueEventListener(userUpdateListener)
    }

    fun listenBookingsUpdates(){
        bookingUpdateListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val bookingList = ArrayList<Booking>()
                for( dsn: DataSnapshot in p0.children) {
                    val newBooking = dsn.value as Map<*, *>
                    bookingList.add(
                        Booking(
                            id = (newBooking["id"])!! as Long,
                            date = (newBooking["date"] )!! as Long,
                            ownerEmail = newBooking["ownerEmail"] as String,
                            type = newBooking["type"] as String,
                            done = newBooking["done"] as Boolean
                        )
                    )
                }
                mainViewModel.insertBookings( bookingList )
            }
        }
        firebaseDatabase.child("bookings").addValueEventListener(bookingUpdateListener)
    }

    fun listenUserBookingsUpdates(){
        userbookingUpdateListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                //TODO: filter results
                val bookingList = ArrayList<UserBooking>()
                for( dsn: DataSnapshot in p0.children) {
                    for(dsn1: DataSnapshot in dsn.children){
                        for(dsn2: DataSnapshot in dsn1.children){
                            val newBooking = dsn2.value as Map<*, *>
                            bookingList.add(
                                UserBooking(
                                    bookingId = (newBooking["bookingId"])!! as Long,
                                    userEmail = newBooking["userEmail"] as String
                                )
                            )
                        }
                    }
                }
                mainViewModel.insertUserBookings( bookingList )
            }
        }
        firebaseDatabase.child("userbookings").addValueEventListener(userbookingUpdateListener)
    }

}