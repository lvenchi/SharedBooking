package com.example.mysharedbooking.models

data class RemoteUserBooking (
    var userId: Long,
    var User: User?,
    var bookingId: Long,
    var Booking: Booking?
)

