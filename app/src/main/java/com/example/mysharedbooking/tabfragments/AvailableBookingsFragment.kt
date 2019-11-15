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
import com.example.mysharedbooking.dataadaptersfragments.UserBookingViewHolder
import com.example.mysharedbooking.databinding.AvailableBookingsFragmentBinding
import com.example.mysharedbooking.models.Booking
import com.example.mysharedbooking.models.UserBooking
import com.example.mysharedbooking.viewmodels.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

class AvailableBookingsFragment : Fragment(), UserBookingViewHolder.ItemClickListener {


    var availableBookingsRecyclerView: RecyclerView? = null
    lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = activity?.run {
            ViewModelProviders.of(activity as MainActivity).get(MainViewModel::class.java)
        }!!
    }

    override fun onItemClicked(booking: Booking) {
        mainViewModel.insertMyBooking(UserBooking(mainViewModel.currentUser.value!!.uid, booking.id))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val availableBookingsFragmentBinding: AvailableBookingsFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.available_bookings_fragment, container,false)
        availableBookingsFragmentBinding.viewmodel = mainViewModel
        return availableBookingsFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        availableBookingsRecyclerView = view.findViewById(R.id.available_bookings_recycler_view)
        availableBookingsRecyclerView?.adapter = BookableBookingAdapter(activity!!, mainViewModel, this )
        availableBookingsRecyclerView?.layoutManager = LinearLayoutManager(activity)
        availableBookingsRecyclerView?.setHasFixedSize(true)

        mainViewModel.getAvailableBookings().observe(this, Observer<List<Booking>> {
            (availableBookingsRecyclerView?.adapter as BookableBookingAdapter).setData(it)
        })

        super.onViewCreated(view, savedInstanceState)
    }
}