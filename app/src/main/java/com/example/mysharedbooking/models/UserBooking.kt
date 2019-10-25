package com.example.mysharedbooking.models

import androidx.room.Embedded
import androidx.room.Relation
import com.example.mysharedbooking.models.Booking
import com.example.mysharedbooking.dataadaptersfragments.BookingListFragment

class UserBooking {
    @Embedded
    lateinit var user : User
    //@Relation(parentColumn = "uid", entityColumn = "userid")
    @Relation(parentColumn = "uid", entityColumn = "userid" , entity = Booking::class)
    lateinit var bookingList :List<Booking>
}