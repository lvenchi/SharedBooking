package com.example.mysharedbooking

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.*

import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mysharedbooking.dataadaptersfragments.BookableBookingAdapter
import com.example.mysharedbooking.dataadaptersfragments.BookingAdapter
import com.example.mysharedbooking.databinding.FragmentHomeBinding
import com.example.mysharedbooking.models.Booking
import com.example.mysharedbooking.models.MySharedBookingDB
import com.example.mysharedbooking.models.User
import com.example.mysharedbooking.viewmodels.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [HomeFrag.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [HomeFrag.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFrag : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    lateinit var goToBookingForm: Observer<Boolean?>
    lateinit var myDatabase: MySharedBookingDB
    var ownedBookingRecyclerView: RecyclerView? = null
    var availableBookingRecyclerView: RecyclerView? = null
    var myBookedBookingsBookingRecyclerView: RecyclerView? = null

    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        mainViewModel = activity?.run {
            ViewModelProviders.of(activity as MainActivity).get(MainViewModel::class.java)
        }!!

        goToBookingForm = Observer {
            if( it == true ) {
                val action = HomeFragDirections.actionHomeFragToNewBookingForm()
                findNavController(activity as MainActivity, R.id.nav_host_fragment).navigate(action)
                mainViewModel.addNewBook.value = false
            }
        }
        mainViewModel.addNewBook.observe(this, goToBookingForm)

        val loadBookings = Observer<User> { user ->
            MainActivity.currentUser = mainViewModel.currentUser.value!!

            mainViewModel.initRepo(user.uid)

            mainViewModel.getMyBookedBookings().observe(this, Observer<List<Booking>> {
                (myBookedBookingsBookingRecyclerView?.adapter as BookingAdapter).setData(it)
            })
            mainViewModel.getAvailableBookings().observe(this, Observer<List<Booking>> {
                (availableBookingRecyclerView?.adapter as BookableBookingAdapter).setData(it)
            })
            mainViewModel.getOwnedBookings().observe(this, Observer<List<Booking>> {
                (ownedBookingRecyclerView?.adapter as BookingAdapter).setData(it)
            })
        }

        mainViewModel.currentUser.observe(this, loadBookings)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentHomeBinding: FragmentHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        fragmentHomeBinding.viewmodel = mainViewModel

        (activity as MainActivity).supportActionBar?.setDefaultDisplayHomeAsUpEnabled(true)
        return fragmentHomeBinding.root
    }


    private suspend fun printAllUsers() = withContext(Dispatchers.IO){
        val userList: List<User> = myDatabase.myDao().getAllUsers()

        for (user:User in userList){
            println(user.username+"")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ownedBookingRecyclerView =  activity?.findViewById<RecyclerView>(R.id.my_bookings_recycler_view)
        availableBookingRecyclerView =  activity?.findViewById<RecyclerView>(R.id.available_bookings_recycler_view)
        myBookedBookingsBookingRecyclerView =  activity?.findViewById<RecyclerView>(R.id.my_booked_bookings_recycler_view)

        myBookedBookingsBookingRecyclerView?.adapter = BookingAdapter(activity!!)
        myBookedBookingsBookingRecyclerView?.layoutManager = LinearLayoutManager(activity)

        availableBookingRecyclerView?.adapter = BookableBookingAdapter(activity!!, mainViewModel)
        availableBookingRecyclerView?.layoutManager = LinearLayoutManager(activity)

        ownedBookingRecyclerView?.adapter = BookingAdapter(activity!!)
        ownedBookingRecyclerView?.layoutManager = LinearLayoutManager(activity)

        ownedBookingRecyclerView?.setHasFixedSize(true)
        availableBookingRecyclerView?.setHasFixedSize(true)
        myBookedBookingsBookingRecyclerView?.setHasFixedSize(true)

    }

    override fun onStart() {
        super.onStart()
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
           throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
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
         * @return A new instance of fragment HomeFrag.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFrag().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
