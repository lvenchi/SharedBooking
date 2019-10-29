package com.example.mysharedbooking.models.daos

import androidx.room.*
import com.example.mysharedbooking.models.Booking
import com.example.mysharedbooking.models.User

@Dao
interface SharedBookingDBDao {
    @Query("SELECT * FROM user")
    fun getAllUsers(): List<User>

    @Query("SELECT * FROM booking")
    fun getAllBooks(): List<Booking>

    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<User>

    @Query("SELECT * FROM user WHERE first_name LIKE :first AND last_name LIKE :last LIMIT 1")
    fun findUserByName(first: String, last: String): User

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertUsers(vararg users: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBooking( booking: Booking)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBookings(vararg serviceBook: Booking)

    @Delete
    fun deleteUser(user: User)


}