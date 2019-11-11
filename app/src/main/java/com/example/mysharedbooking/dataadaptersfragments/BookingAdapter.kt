package com.example.mysharedbooking.dataadaptersfragments

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.mysharedbooking.R
import com.example.mysharedbooking.models.Booking
import java.lang.StringBuilder
import java.util.*
import java.util.zip.Inflater

class BookingAdapter(context: Context): RecyclerView.Adapter<BookingAdapter.UserViewHolder>(){

    class UserViewHolder(val tv: TextView): RecyclerView.ViewHolder(tv)

    private val layoutInflater = LayoutInflater.from(context)
    private var clientBookingList: List<Booking>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {

        val textView = layoutInflater.inflate(R.layout.user_holder, parent, false) as TextView

        return UserViewHolder(
            textView
        )
    }

    fun setData(newData: List<Booking>) {
        clientBookingList = newData
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        if(clientBookingList != null) return clientBookingList!!.size else return 0
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        if(clientBookingList != null){
            val el = clientBookingList?.get(position)
            val stringBuilder = StringBuilder()
            //for( book in el.bookingList){
            stringBuilder.append( el?.id.toString()).append("  "+el?.ownerId)
            //}
            holder.tv.text= stringBuilder.toString()
        } else holder.tv.text= "Loading..."
    }
}