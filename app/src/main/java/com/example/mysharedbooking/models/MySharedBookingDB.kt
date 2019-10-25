package com.example.mysharedbooking.models

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [User::class, Booking::class], version = 3)
abstract class MySharedBookingDB : RoomDatabase() {

    abstract fun myDao(): SharedBookingDBDao

}