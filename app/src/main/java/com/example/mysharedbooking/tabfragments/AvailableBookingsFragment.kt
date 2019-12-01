package com.example.mysharedbooking.tabfragments

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Rect
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
import com.example.mysharedbooking.dataadapters.BookableBookingAdapter
import com.example.mysharedbooking.dataadapters.UserBookingViewHolder
import com.example.mysharedbooking.databinding.AvailableBookingsFragmentBinding
import com.example.mysharedbooking.helpers.setItemDecoratorWithPadding
import com.example.mysharedbooking.models.Booking
import com.example.mysharedbooking.models.UserBooking
import com.example.mysharedbooking.viewmodels.MainViewModel

class AvailableBookingsFragment : Fragment(), UserBookingViewHolder.ItemClickListener {

    private var alarmMgr: AlarmManager? = null
    private lateinit var alarmIntent: PendingIntent
    var availableBookingsRecyclerView: RecyclerView? = null
    lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = activity?.run {
            ViewModelProviders.of(activity as MainActivity).get(MainViewModel::class.java)
        }!!
        alarmMgr = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    override fun onItemClicked(booking: Booking) {
        mainViewModel.insertMyBooking(UserBooking(mainViewModel.currentUser.value!!.email, booking.id))

        alarmIntent = Intent(activity, RememberMeBroadcastReceiver::class.java).let { intent ->
            intent.action = "com.example.MySharedBooking.REMEMBER_ME"
            intent.putExtra("booking_type", booking.type)
                .putExtra("owner_email", booking.ownerEmail)
                .putExtra("appointment_date", booking.date)
            PendingIntent.getBroadcast(context, 0, intent, 0)
        }

        alarmMgr?.set(
            AlarmManager.RTC_WAKEUP,
            booking.date,
            alarmIntent
        )

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

        if(availableBookingsRecyclerView?.adapter == null) availableBookingsRecyclerView.also {
            it?.adapter = BookableBookingAdapter(activity!!, mainViewModel, this )
            it?.layoutManager = LinearLayoutManager(activity)
            it?.setHasFixedSize(true)
            it?.setItemDecoratorWithPadding(6)
        }


        mainViewModel.getAvailableBookings().observe(this, Observer<List<Booking>> {
            (availableBookingsRecyclerView?.adapter as BookableBookingAdapter).setData(it)
        })

        super.onViewCreated(view, savedInstanceState)
    }
}