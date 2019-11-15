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
           WHERE userclient_booking_join.userId=:clientId
           """)
    fun getBookingsOfUserClient(clientId: Long): LiveData<List<Booking>>

    @Query("""
           SELECT * FROM user
           INNER JOIN userclient_booking_join
           ON user.uid=userclient_booking_join.userId
           WHERE userclient_booking_join.bookingId =:bookingId
           """)
    fun getUsersOfBooking(bookingId: Long): Array<User>

    @Query("SELECT * FROM booking WHERE ownerid = :userid")
    fun getOwningBookings(userid :Long): LiveData<List<Booking>>

    @Query("""
        SELECT * FROM booking WHERE id NOT IN (
        SELECT bookingId FROM userclient_booking_join
         WHERE userclient_booking_join.userId = :userid ) AND ownerid != :userid
        """)
    fun getNotOwningBookings(userid :Long): LiveData<List<Booking>>

    @Delete
    fun removeUserBooking( userBooking: UserBooking)
}