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
import com.example.mysharedbooking.dataadaptersfragments.MyPublishedBookingsAdapter
import com.example.mysharedbooking.dataadaptersfragments.UserBookingViewHolder
import com.example.mysharedbooking.databinding.MyPublishedBookingFragmentBinding
import com.example.mysharedbooking.models.Booking
import com.example.mysharedbooking.viewmodels.MainViewModel

class MyPublishedBookingsFragment : Fragment(), UserBookingViewHolder.ItemClickListener{

    override fun onItemClicked(booking: Booking) {
        mainViewModel.removeBooking( booking )
    }

    var ownedBookingRecyclerView: RecyclerView? = null
    lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = activity?.run {
            ViewModelProviders.of(activity as MainActivity).get(MainViewModel::class.java)
        }!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val myPublishedBookingFragmentBinding: MyPublishedBookingFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.my_published_booking_fragment, container,false)
        myPublishedBookingFragmentBinding.viewmodel = mainViewModel
        return myPublishedBookingFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ownedBookingRecyclerView = view.findViewById(R.id.my_bookings_recycler_view)
        ownedBookingRecyclerView?.adapter = MyPublishedBookingsAdapter(activity!!, mainViewModel, this)
        ownedBookingRecyclerView?.layoutManager = LinearLayoutManager(activity)
        ownedBookingRecyclerView?.setHasFixedSize(true)

        mainViewModel.getOwnedBookings().observe(this, Observer<List<Booking>> {
            (ownedBookingRecyclerView?.adapter as MyPublishedBookingsAdapter).setData(it)
        })

        super.onViewCreated(view, savedInstanceState)
    }
}