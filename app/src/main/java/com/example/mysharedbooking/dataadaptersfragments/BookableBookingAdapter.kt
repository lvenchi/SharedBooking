package com.example.mysharedbooking.dataadaptersfragments

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.example.mysharedbooking.MainActivity
import com.example.mysharedbooking.R
import com.example.mysharedbooking.models.Booking
import com.example.mysharedbooking.models.UserBooking
import com.example.mysharedbooking.viewmodels.MainViewModel
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.StringBuilder
import java.util.*


class BookableBookingAdapter(context: Context, private val mainViewModel: MainViewModel): RecyclerView.Adapter<BookableBookingAdapter.BookableBookingViewHolder>(){

    class BookableBookingViewHolder(val inflatedViewHolder: RelativeLayout): RecyclerView.ViewHolder(inflatedViewHolder)

    private var clientBookingList: List<Booking>? = null
    private val layoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookableBookingViewHolder {
        val inflatedViewHolder = layoutInflater
            .inflate(R.layout.bookable_book_viewholder, parent, false) as RelativeLayout

        return BookableBookingViewHolder(
            inflatedViewHolder
        )
    }

    fun setData(newData: List<Booking>) {
        clientBookingList = newData
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        if(clientBookingList != null) return clientBookingList!!.size else return 0
    }

    override fun onBindViewHolder(holder: BookableBookingViewHolder, position: Int) {
        if(clientBookingList != null) {
            val el = clientBookingList!![position]
            val stringBuilder = StringBuilder()
            //for( book in el.bookingList){
            stringBuilder.append(el.id.toString()).append("  " + el.ownerId)
                .append(" " + Date(el.date))
            //}
            var url: String? = null
            mainViewModel.viewModelScope.launch (Dispatchers.IO){
                url = mainViewModel.getLinkProfilePicByUserId(el.ownerId)
            }.invokeOnCompletion {
                mainViewModel.viewModelScope.launch(Dispatchers.Main) {
                    if(!url.isNullOrEmpty()) Picasso.get().load(url).resize(50, 50)
                        .into(holder.inflatedViewHolder.findViewById<ImageView>(R.id.bookable_user_image))
                }
            }

            holder.inflatedViewHolder.findViewById<TextView>(R.id.booking_title).text =
                stringBuilder.toString()
            holder.inflatedViewHolder.findViewById<Button>(R.id.button_book).setOnClickListener {
                mainViewModel.insertMyBooking(UserBooking(MainActivity.currentUser.uid, el.id))
            }
        }
    }
}