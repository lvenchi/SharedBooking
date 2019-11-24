package com.example.mysharedbooking.models.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.mysharedbooking.models.Booking
import com.example.mysharedbooking.models.User
import com.example.mysharedbooking.models.UserBooking

@Dao
interface UserBookingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(userBooking: UserBooking)

    @Query("""
           SELECT * FROM booking
           INNER JOIN userclient_booking_join
           ON booking.id=userclient_booking_join.bookingId
           WHERE userclient_booking_join.userEmail=:userEmail
           """)
    fun getBookingsOfUserClient(userEmail: String): LiveData<List<Booking>>

    @Query("""
           SELECT * FROM user
           INNER JOIN userclient_booking_join
           ON user.email=userclient_booking_join.userEmail
           WHERE userclient_booking_join.bookingId =:bookingId
           """)
    fun getUsersOfBooking(bookingId: Long): Array<User>

    @Query("SELECT * FROM booking WHERE owner_email = :userEmail")
    fun getOwningBookings(userEmail :String): LiveData<List<Booking>>

    @Query("""
        SELECT * FROM booking WHERE id NOT IN (
        SELECT bookingId FROM userclient_booking_join
         WHERE userclient_booking_join.userEmail = :userEmail ) AND owner_email != :userEmail
        """)
    fun getNotOwningBookings(userEmail :String): LiveData<List<Booking>>

    @Delete
    fun removeUserBooking( userBooking: UserBooking)
}