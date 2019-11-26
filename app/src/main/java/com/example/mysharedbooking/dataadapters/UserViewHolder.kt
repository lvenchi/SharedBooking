package com.example.mysharedbooking.dataadapters

import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.mysharedbooking.R
import com.example.mysharedbooking.models.Booking
import com.example.mysharedbooking.models.User

class UserViewHolder(val inflatedViewHolder: ViewGroup) : RecyclerView.ViewHolder(inflatedViewHolder){

    fun bind(user: User, clickListener: ItemClickListener){
        val entry = itemView.findViewById<ViewGroup>(R.id.userholder)
        entry.setOnClickListener {
            clickListener.onItemClicked(user)
        }
    }
    interface ItemClickListener{
        fun onItemClicked( user : User)
    }
}