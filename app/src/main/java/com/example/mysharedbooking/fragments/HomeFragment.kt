package com.example.mysharedbooking.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.*

import androidx.navigation.Navigation.findNavController
import androidx.viewpager.widget.ViewPager

import com.example.mysharedbooking.MainActivity
import com.example.mysharedbooking.R
import com.example.mysharedbooking.databinding.FragmentHomeBinding
import com.example.mysharedbooking.helpers.FirebaseUtils
import com.example.mysharedbooking.tabfragments.AvailableBookingsFragment
import com.example.mysharedbooking.tabfragments.MyBookedBookingsFragment
import com.example.mysharedbooking.tabfragments.MyPublishedBookingsFragment
import com.example.mysharedbooking.viewmodels.MainViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.tabs.TabLayout
import com.google.firebase.iid.FirebaseInstanceId


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val PARAM1 = "param1"
private const val PARAM2 = "param2"


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
    lateinit var tabLayout: TabLayout
    lateinit var viewPager: ViewPager
    private lateinit var fireBaseUtils: FirebaseUtils

    private lateinit var mainViewModel: MainViewModel
    private var bookingsCollectionPagerAdapter : BookingsCollectionPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(PARAM1)
            param2 = it.getString(PARAM2)
        }

        mainViewModel = activity?.run {
            ViewModelProviders.of(activity as MainActivity).get(MainViewModel::class.java)
        }!!

        goToBookingForm = Observer {
            if( it == true ) {
                val action =
                    HomeFragDirections.actionHomeFragToNewBookingForm(
                        userEmail = mainViewModel.currentUser.value!!.email
                    )
                findNavController(activity as MainActivity,
                    R.id.nav_host_fragment
                ).navigate(action)
                mainViewModel.addNewBook.value = false
            }
        }
        mainViewModel.addNewBook.observe(this, goToBookingForm)

        bookingsCollectionPagerAdapter =
            BookingsCollectionPagerAdapter(
                childFragmentManager
            )

        mainViewModel.initRepo(mainViewModel.currentUser.value!!.email)
        fireBaseUtils = FirebaseUtils(mainViewModel)

        bookingsCollectionPagerAdapter?.addFragment(AvailableBookingsFragment(), "Available")
        bookingsCollectionPagerAdapter?.addFragment(MyPublishedBookingsFragment(), "Published")
        bookingsCollectionPagerAdapter?.addFragment(MyBookedBookingsFragment(), "My Booked")

        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token
                fireBaseUtils.updateUserToken(token, mainViewModel.currentUser.value!!)
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentHomeBinding: FragmentHomeBinding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_home, container, false)
        fragmentHomeBinding.viewmodel = mainViewModel

        (activity as MainActivity).supportActionBar?.setDefaultDisplayHomeAsUpEnabled(true)
        return fragmentHomeBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tabLayout = view.findViewById(R.id.tab_layout)
        viewPager = view.findViewById(R.id.pager)
        viewPager.adapter = bookingsCollectionPagerAdapter
        viewPager.offscreenPageLimit = 2
        tabLayout.setupWithViewPager(viewPager)

    }


    override fun onStart() {
        fireBaseUtils.listenBookingsUpdates()
        fireBaseUtils.listenUserBookingsUpdates()
        fireBaseUtils.listenUsersUpdates()
        super.onStart()
    }

    override fun onStop() {
        fireBaseUtils.unListenBookingUpdates()
        fireBaseUtils.unListenUserBookingUpdates()
        fireBaseUtils.unListenUsersUpdates()
        super.onStop()
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
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
                    putString(PARAM1, param1)
                    putString(PARAM2, param2)
                }
            }
    }



    class BookingsCollectionPagerAdapter(fragmentManager: FragmentManager) :
        FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        private var frList: ArrayList<Fragment> = ArrayList()
        private var titleList: ArrayList<String> = ArrayList()

        fun addFragment(fragment: Fragment, title: String){
            frList.add(fragment)
            titleList.add(title)
        }
        override fun getCount(): Int  = frList.size

        override fun getItem(i: Int): Fragment {
            return frList[i]
        }

        override fun getPageTitle(position: Int): CharSequence {
            return titleList[position]
        }

        fun resetList(){
            frList.clear()
            titleList.clear()
        }
    }
}
