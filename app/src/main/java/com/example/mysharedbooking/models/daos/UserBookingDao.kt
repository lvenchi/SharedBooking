package com.example.mysharedbooking.models.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.mysharedbooking.models.Booking
import com.example.mysharedbooking.models.User
import com.example.mysharedbooking.models.UserBooking

@Dao
interface UserBookingDao {

    @Insert
    fun insert(userBooking: UserBooking)

    @Query("""
           SELECT * FROM booking
           INNER JOIN userclient_booking_join
           ON booking.id=userclient_booking_join.id
           WHERE userclient_booking_join.uid=:clientId
           """)
    fun getBookingsOfUserClient(clientId: Long): Array<Booking>

    @Query("""
           SELECT * FROM user
           INNER JOIN userclient_booking_join
           ON user.uid=userclient_booking_join.uid
           WHERE userclient_booking_join.id =:bookingId
           """)
    fun getUsersOfBooking(bookingId: Long): Array<User>
}