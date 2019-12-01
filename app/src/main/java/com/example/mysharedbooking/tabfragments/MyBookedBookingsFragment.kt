package com.example.mysharedbooking.tabfragments

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
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
import com.example.mysharedbooking.RememberMeBroadcastReceiver
import com.example.mysharedbooking.dataadapters.MyBookedBookingAdapter
import com.example.mysharedbooking.dataadapters.UserBookingViewHolder
import com.example.mysharedbooking.databinding.MyBookedBookingsFragmentBinding
import com.example.mysharedbooking.helpers.setItemDecoratorWithPadding
import com.example.mysharedbooking.models.Booking
import com.example.mysharedbooking.models.UserBooking
import com.example.mysharedbooking.viewmodels.MainViewModel

class MyBookedBookingsFragment :Fragment() , UserBookingViewHolder.ItemClickListener{

    private var alarmMgr: AlarmManager? = null
    private lateinit var alarmIntent: PendingIntent
    var myBookedBookingsRecyclerView: RecyclerView? = null
    lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        alarmMgr = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
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
        if(myBookedBookingsRecyclerView?.adapter == null) myBookedBookingsRecyclerView?.also {
            it.adapter = MyBookedBookingAdapter(activity!!, mainViewModel, this)
            it.layoutManager = LinearLayoutManager(activity)
            it.setHasFixedSize(true)
            it.setItemDecoratorWithPadding(6)
        }

        mainViewModel.getMyBookedBookings().observe(this, Observer<List<Booking>> {
            (myBookedBookingsRecyclerView?.adapter as MyBookedBookingAdapter).setData(it)
        })

        super.onViewCreated(view, savedInstanceState)
    }

    //remove booking
    override fun onItemClicked(booking: Booking) {
        mainViewModel.removeUserBooking( UserBooking( mainViewModel.currentUser.value!!.email, booking.id))

        alarmIntent = Intent(activity, RememberMeBroadcastReceiver::class.java).let { intent ->
            intent.action = "com.example.MySharedBooking.REMEMBER_ME"
            intent.putExtra("booking_type", booking.type)
                .putExtra("owner_email", booking.ownerEmail)
                .putExtra("appointment_date", booking.date)
            PendingIntent.getBroadcast(context, 0, intent, 0)
        }

        alarmMgr?.cancel( alarmIntent )
    }
}