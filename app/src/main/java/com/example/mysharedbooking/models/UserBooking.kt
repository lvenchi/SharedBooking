package com.example.mysharedbooking.models

import androidx.room.Entity
import androidx.room.ForeignKey
import com.squareup.moshi.Json


@Entity(tableName = "userclient_booking_join",
    primaryKeys = ["userEmail", "bookingId"],
    foreignKeys = [
        ForeignKey(entity = User::class,
        parentColumns = arrayOf("email"),
        childColumns = arrayOf("userEmail")),
        ForeignKey(entity = Booking::class, onDelete = ForeignKey.CASCADE,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("bookingId"))]
)
data class UserBooking (
    @Json(name = "UserEmail") val userEmail: String,
    @Json(name = "BookingId") val bookingId: Long
)