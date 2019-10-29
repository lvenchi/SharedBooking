package com.example.mysharedbooking.dataadaptersfragments

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mysharedbooking.R
import com.example.mysharedbooking.models.Booking
import java.lang.StringBuilder
import java.util.*

class BookingAdapter(private val clientBookingList: List<Booking>): RecyclerView.Adapter<BookingAdapter.UserViewHolder>(){

    class UserViewHolder(val tv: TextView): RecyclerView.ViewHolder(tv)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_holder, parent, false) as TextView

        return UserViewHolder(
            textView
        )
    }

    override fun getItemCount(): Int {
        return clientBookingList.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val el = clientBookingList[position]
        val stringBuilder = StringBuilder()
        //for( book in el.bookingList){
        stringBuilder.append( el.id.toString())
        //}
        holder.tv.text= stringBuilder.toString()
    }
}