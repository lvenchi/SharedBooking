package com.example.mysharedbooking.models

import androidx.room.Entity
import androidx.room.ForeignKey
import com.squareup.moshi.Json


@Entity(tableName = "userclient_booking_join",
    primaryKeys = arrayOf("uid","id"),
    foreignKeys = arrayOf(
        ForeignKey(entity = User::class,
            parentColumns = arrayOf("uid"),
            childColumns = arrayOf("id")),
        ForeignKey(entity = Booking::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("uid"))
    )
)
data class UserBooking (
    @Json(name = "UserId") val uid: Long,
    @Json(name = "BookingId") val id: Long
)