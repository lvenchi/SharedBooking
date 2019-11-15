package com.example.mysharedbooking.tabfragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mysharedbooking.MainActivity
import com.example.mysharedbooking.R
import com.example.mysharedbooking.dataadaptersfragments.BookableBookingAdapter
import com.example.mysharedbooking.dataadaptersfragments.MyBookedBookingAdapter
import com.example.mysharedbooking.dataadaptersfragments.UserBookingViewHolder
import com.example.mysharedbooking.databinding.MyBookedBookingsFragmentBinding
import com.example.mysharedbooking.models.Booking
import com.example.mysharedbooking.models.UserBooking
import com.example.mysharedbooking.viewmodels.MainViewModel

class MyBookedBookingsFragment :Fragment() , UserBookingViewHolder.ItemClickListener{

    var myBookedBookingsRecyclerView: RecyclerView? = null
    lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainViewModel = activity?.run {
            ViewModelProviders.of(activity as MainActivity).get(MainViewModel::class.java)
        }!!
        val myBookedBookingFragmentBinding: MyBookedBookingsFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.my_booked_bookings_fragment, container,false)
        myBookedBookingFragmentBinding.viewmodel =  mainViewModel
        return myBookedBookingFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        myBookedBookingsRecyclerView = view.findViewById(R.id.my_booked_bookings_recycler_view)
        myBookedBookingsRecyclerView?.adapter = MyBookedBookingAdapter(activity!!, mainViewModel, this)
        myBookedBookingsRecyclerView?.layoutManager = LinearLayoutManager(activity)
        myBookedBookingsRecyclerView?.setHasFixedSize(true)

        mainViewModel.getMyBookedBookings().observe(this, Observer<List<Booking>> {
            (myBookedBookingsRecyclerView?.adapter as MyBookedBookingAdapter).setData(it)
        })

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onItemClicked(booking: Booking) {
        mainViewModel.removeUserBooking( UserBooking( mainViewModel.currentUser.value!!.uid, booking.id))
    }
}