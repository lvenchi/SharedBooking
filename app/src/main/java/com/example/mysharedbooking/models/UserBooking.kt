package com.example.mysharedbooking.models

import androidx.room.Entity
import androidx.room.ForeignKey
import com.squareup.moshi.Json


@Entity(tableName = "userclient_booking_join",
    primaryKeys = ["userId", "bookingId"],
    foreignKeys = [
        ForeignKey(entity = User::class,
        parentColumns = arrayOf("uid"),
        childColumns = arrayOf("userId")),
        ForeignKey(entity = Booking::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("bookingId"))]
)
data class UserBooking (
    @Json(name = "UserId") val userId: Long,
    @Json(name = "BookingId") val bookingId: Long
)