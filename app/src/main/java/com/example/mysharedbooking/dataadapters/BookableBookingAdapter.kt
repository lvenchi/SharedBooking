package com.example.mysharedbooking.dataadapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.example.mysharedbooking.R
import com.example.mysharedbooking.models.Booking
import com.example.mysharedbooking.viewmodels.MainViewModel
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*


class BookableBookingAdapter(context: Context, private val mainViewModel: MainViewModel,
                             private val itemClickListener: UserBookingViewHolder.ItemClickListener): RecyclerView.Adapter<UserBookingViewHolder>(){


    private var clientBookingList: List<Booking>? = null
    private val layoutInflater = LayoutInflater.from(context)
    var formatter = SimpleDateFormat("dd-MMM-yyyy 'at' HH:mm:ss", Locale.ITALIAN)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserBookingViewHolder {
        val inflatedViewHolder = layoutInflater
            .inflate(R.layout.bookable_book_viewholder, parent, false) as ViewGroup

        return UserBookingViewHolder(
            inflatedViewHolder
        )
    }

    fun setData(newData: List<Booking>) {
        clientBookingList = newData
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return if(clientBookingList != null) clientBookingList!!.size else 0
    }

    override fun onBindViewHolder(holder: UserBookingViewHolder, position: Int) {
        if(clientBookingList != null) {
            val el = clientBookingList!![position]
            val stringBuilder = StringBuilder()

            stringBuilder.append(el.id.toString()).append("  " + el.ownerEmail)
                .append(" " + formatter.format(el.date))

            var url: String? = null
            mainViewModel.viewModelScope.launch (Dispatchers.IO){
                url = mainViewModel.getLinkProfilePicByUserId(el.ownerEmail)
            }.invokeOnCompletion {
                mainViewModel.viewModelScope.launch(Dispatchers.Main) {
                    if(!url.isNullOrEmpty()) Picasso.get().load(url).resize(100, 100)
                        .into(holder.inflatedViewHolder.findViewById<ImageView>(R.id.bookable_user_image))
                }
            }

            holder.inflatedViewHolder.findViewById<TextView>(R.id.booking_title).text =
                stringBuilder.toString()
            holder.bind(el, itemClickListener)
        }
    }
}