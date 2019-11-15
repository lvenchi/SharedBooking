package com.example.mysharedbooking.dataadaptersfragments

import android.widget.Button
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.mysharedbooking.R
import com.example.mysharedbooking.models.Booking

class UserBookingViewHolder(val inflatedViewHolder: RelativeLayout): RecyclerView.ViewHolder(inflatedViewHolder){

    fun bind(booking: Booking, clickListener: ItemClickListener){
        itemView.findViewById<Button>(R.id.button_book).setOnClickListener {
            clickListener.onItemClicked(booking)
        }
    }
    interface ItemClickListener{
        fun onItemClicked(booking: Booking)
    }
}