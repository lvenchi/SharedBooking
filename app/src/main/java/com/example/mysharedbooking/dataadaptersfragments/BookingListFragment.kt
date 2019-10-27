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
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mysharedbooking.MainActivity
import com.example.mysharedbooking.R
import com.example.mysharedbooking.databinding.FragmentBookingListBinding
import com.example.mysharedbooking.databinding.FragmentUserListBinding
import com.example.mysharedbooking.models.Booking
import com.example.mysharedbooking.models.User
import com.example.mysharedbooking.models.UserBooking
import com.example.mysharedbooking.viewmodels.BookingsViewModel
import com.example.mysharedbooking.viewmodels.NewBookingViewModel
import com.example.mysharedbooking.viewmodels.UsersViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    private var bookingList: List<UserBooking> = ArrayList()
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
        val downloadBooking = Observer<List<UserBooking>> { newList ->
            bookingRecyclerView?.adapter =
                BookingAdapter(
                    newList
                )
        }
        bookingsViewModel.bookingList.observe(this, downloadBooking)

        bookingRecyclerView = activity?.findViewById<RecyclerView>(R.id.booking_recycler_view)?.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            BookingAdapter(bookingList)
        }
        bookingsViewModel.viewModelScope.launch { getAllBookings() }
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

    private suspend fun getAllBookings() = withContext(Dispatchers.IO){
        bookingsViewModel.bookingList.postValue(myDatabase.myDao().getUsersBookings())
    }

    class BookingAdapter(private val bookingList: List<UserBooking>): RecyclerView.Adapter<BookingAdapter.UserViewHolder>(){

        class UserViewHolder(val tv: TextView): RecyclerView.ViewHolder(tv)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
            val textView = LayoutInflater.from(parent.context)
                .inflate(R.layout.user_holder, parent, false) as TextView

            return UserViewHolder(
                textView
            )
        }

        override fun getItemCount(): Int {
            return bookingList.size
        }

        override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
            val el = bookingList[position]
            val stringBuilder = StringBuilder()
            stringBuilder.append(el.user.username +"\n")
            for( book in el.bookingList){
                stringBuilder.append(book.type+" "+Date(book.date).toString())
            }
            holder.tv.text= stringBuilder.toString()
        }
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
