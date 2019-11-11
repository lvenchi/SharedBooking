package com.example.mysharedbooking.models

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mysharedbooking.models.daos.SharedBookingDBDao
import com.example.mysharedbooking.models.daos.UserBookingDao

@Database(entities = [User::class, Booking::class, UserBooking::class], version = 11)
abstract class MySharedBookingDB : RoomDatabase() {

    abstract fun myDao(): SharedBookingDBDao
    abstract fun userBookingDao(): UserBookingDao

}