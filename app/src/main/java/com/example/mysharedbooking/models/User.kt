package com.example.mysharedbooking.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@Entity(tableName = "User", indices = [Index(value = ["username"], unique = true)])
data class User (
    @Json(name = "id") @PrimaryKey(autoGenerate = true) val uid :Long,
    @ColumnInfo(name="first_name") val firstName: String,
    @ColumnInfo(name="last_name") val lastName: String,
    @ColumnInfo( name="username" ) val username: String,
    @ColumnInfo(name="role") val role: String
)