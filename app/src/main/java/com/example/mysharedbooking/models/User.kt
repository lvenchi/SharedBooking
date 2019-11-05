package com.example.mysharedbooking.models

import androidx.room.*
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.net.URL


@Entity(tableName = "User", indices = [Index(value = ["username"], unique = true), Index(value = ["email"], unique = true)])
data class User (
    @Json(name = "id") @PrimaryKey(autoGenerate = true) val uid :Long,
    @ColumnInfo(name="first_name") val firstName: String?,
    @ColumnInfo(name="last_name") val lastName: String?,
    @ColumnInfo(name="username" ) val username: String?,
    @ColumnInfo(name="email") val email: String?,
    @ColumnInfo(name="role") val role: String?,
    @ColumnInfo(name="profile_pic") val profilePic: String?,
    @ColumnInfo(name = "provider_id") val providerId: String?
)