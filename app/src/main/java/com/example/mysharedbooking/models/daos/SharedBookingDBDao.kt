package com.example.mysharedbooking.models.daos

import androidx.room.*
import com.example.mysharedbooking.models.Booking
import com.example.mysharedbooking.models.User

@Dao
interface SharedBookingDBDao {

    @Query("SELECT profile_pic FROM user WHERE email = :userEmail")
    fun getPhotoLinkByUserEmail(userEmail: String) : String

    @Query("SELECT * FROM user")
    fun getAllUsers(): List<User>

    @Query("SELECT * FROM booking")
    fun getAllBooks(): List<Booking>

    @Query("SELECT * FROM user WHERE first_name LIKE :first AND last_name LIKE :last LIMIT 1")
    fun findUserByName(first: String, last: String): User

    @Query("SELECT * FROM user WHERE email LIKE :email LIMIT 1")
    fun findUserByEmail(email: String): User?

    @Query("SELECT * FROM user WHERE firebase_id LIKE :firebaseId LIMIT 1")
    fun findUserByFirebaseId(firebaseId: String): User

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertUsers(vararg users: User)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertBooking( booking: Booking) : Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertBookings(vararg serviceBook: Booking)

    @Delete
    fun deleteBooking(booking: Booking)

    @Delete
    fun deleteUser(user: User)


}