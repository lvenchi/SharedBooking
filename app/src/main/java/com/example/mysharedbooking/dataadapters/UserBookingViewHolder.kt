package com.example.mysharedbooking.dataadapters

import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.mysharedbooking.R
import com.example.mysharedbooking.models.Booking

class UserBookingViewHolder(val inflatedViewHolder: ViewGroup): RecyclerView.ViewHolder(inflatedViewHolder){

    fun bind(booking: Booking, clickListener: ItemClickListener){
        val button = itemView.findViewById<Button>(R.id.button_book)
        button.setOnClickListener {
            clickListener.onItemClicked(booking)
        }
    }
    interface ItemClickListener{
        fun onItemClicked(booking: Booking)
    }
}