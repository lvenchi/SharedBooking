package com.example.mysharedbooking.models

import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.room.*

@Entity(tableName = "Booking", foreignKeys = [
    ForeignKey(entity = User::class, onDelete = ForeignKey.CASCADE,
    parentColumns = arrayOf("email"),
    childColumns = arrayOf("owner_email"))]
)
data class Booking (
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "date") val date: Long,
    @ColumnInfo(name = "done") val done: Boolean,
    @ColumnInfo(name = "owner_email") val ownerEmail: String
)