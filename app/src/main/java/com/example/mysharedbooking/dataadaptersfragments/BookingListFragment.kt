package com.example.mysharedbooking.dataadaptersfragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mysharedbooking.MainActivity
import com.example.mysharedbooking.R
import com.example.mysharedbooking.databinding.FragmentBookingListBinding
import com.example.mysharedbooking.models.Booking
import com.example.mysharedbooking.viewmodels.BookingsViewModel
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [BookingListFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [BookingListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BookingListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var bookingsViewModel: BookingsViewModel
    private var clientBookingList: List<Booking> = ArrayList()
    var bookingRecyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onStart() {
        super.onStart()
        val downloadBooking = Observer<List<Booking>> { newList ->
            bookingRecyclerView?.adapter =
                BookingAdapter(
                    newList
                )
        }
        bookingsViewModel.clientBookingList.observe(this, downloadBooking)

        bookingRecyclerView = activity?.findViewById<RecyclerView>(R.id.booking_recycler_view)?.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            BookingAdapter(clientBookingList)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val bookingListBinding: FragmentBookingListBinding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_booking_list, container, false)
        bookingsViewModel = ViewModelProviders.of(this).get(BookingsViewModel::class.java)
        bookingListBinding.viewmodel = bookingsViewModel

        return bookingListBinding.root
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
            myDatabase = MainActivity.getInMemoryDatabase(activity!!.applicationContext)
        } else {
            throw RuntimeException(" must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BookingListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BookingListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
