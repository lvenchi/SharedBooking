package com.example.mysharedbooking.models

import androidx.room.*

@Dao
interface SharedBookingDBDao {
    @Query("SELECT * FROM user")
    fun getAllUsers(): List<User>

    @Query("SELECT * FROM booking")
    fun getAllBooks(): List<Booking>

    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<User>

    @Query("SELECT * FROM user INNER JOIN booking ON user.uid = booking.userid")
    fun loadUserBookings(): List<UserBooking>

    @Query("SELECT * FROM user WHERE first_name LIKE :first AND last_name LIKE :last LIMIT 1")
    fun findUserByName(first: String, last: String): User

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertUsers(vararg users: User)

    @Query("SELECT * FROM User")
    fun getUsersBookings(): List<UserBooking>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBooking( booking: Booking)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBookings(vararg serviceBook: Booking)

    @Delete
    fun delete(user: User)
}