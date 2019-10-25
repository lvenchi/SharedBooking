package com.example.mysharedbooking.models

import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.room.*

@Entity(
    indices = [Index(value = ["userid"])],
    tableName = "Booking",
    foreignKeys = [ForeignKey(
    entity = User::class,
    parentColumns = ["uid"],
    childColumns = ["userid"],
    onDelete = ForeignKey.CASCADE)])
data class Booking (
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "date") val date: Long,
    @ColumnInfo(name = "done") val done: Boolean,
    @ColumnInfo(name = "userid") val userid: Long

)