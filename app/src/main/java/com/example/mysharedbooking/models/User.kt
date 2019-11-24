package com.example.mysharedbooking.models


import androidx.annotation.NonNull
import androidx.room.*

@Entity(tableName = "User", indices = [Index(value = ["username"], unique = true), Index(value = ["email"], unique = true)])
data class User (
    @PrimaryKey
    @NonNull
    @ColumnInfo(name="email") val email: String,
    @ColumnInfo(name="first_name") val firstName: String?,
    @ColumnInfo(name="last_name") val lastName: String?,
    @ColumnInfo(name="username" ) val username: String?,
    @ColumnInfo(name="role") val role: String?,
    @ColumnInfo(name="profile_pic") val profilePic: String?,
    @ColumnInfo(name = "firebase_id") val firebaseId: String?,
    @ColumnInfo(name = "firebase_token") val firebaseToken: String?
)