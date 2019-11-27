package com.example.mysharedbooking.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.viewModelScope
import com.example.mysharedbooking.MainActivity
import com.example.mysharedbooking.R
import com.example.mysharedbooking.databinding.FragmentNewBookingFormBinding
import com.example.mysharedbooking.models.Booking
import com.example.mysharedbooking.viewmodels.MainViewModel
import com.example.mysharedbooking.viewmodels.NewBookingViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.common.io.Resources
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [NewBookingForm.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [NewBookingForm.newInstance] factory method to
 * create an instance of this fragment.
 */
class NewBookingForm : Fragment(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var nbfviewmodel: NewBookingViewModel
    private var userEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userEmail = arguments?.getString("userEmail")
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        val d1 = Calendar.getInstance()
        d1.set(p1,p2,p3)
        nbfviewmodel.calendar.value = d1
        //val oldFrag = activity!!.supportFragmentManager.getFragment(Bundle(), "timePicker")
        //if(oldFrag == null){
        TimePickerFragment(
            activity as MainActivity,
            this
        ).show(activity!!.supportFragmentManager, "timePicker")
        //} else (oldFrag as TimePickerFragment).dialog?.show()

    }

    override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
        val d1 = nbfviewmodel.calendar.value
        d1?.set(Calendar.HOUR_OF_DAY, p1)
        d1?.set(Calendar.MINUTE, p2)
        nbfviewmodel.calendar.value = d1
        nbfviewmodel.updateDate(d1?.time.toString())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentNewBookingFormBinding: FragmentNewBookingFormBinding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_new_booking_form, container, false)
        fragmentNewBookingFormBinding.lifecycleOwner = this
        nbfviewmodel = ViewModelProviders.of(this).get(NewBookingViewModel::class.java)
        nbfviewmodel.userEmail.postValue(userEmail)
        fragmentNewBookingFormBinding.viewmodel = nbfviewmodel

        nbfviewmodel.newBooking.observe(this, goHome)
        nbfviewmodel.showDatePick.observe(this, showDatePickerDialog)
        // Inflate the layout for this fragment
        return fragmentNewBookingFormBinding.root
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    val goHome = Observer<Booking>{
        nbfviewmodel.viewModelScope.launch(Dispatchers.IO) {
            val newId : Long = MainActivity.getInMemoryDatabase(
                activity!!.baseContext
            ).myDao().insertBooking(it)
            activity?.run {
                ViewModelProviders.of(activity as MainActivity).get(MainViewModel::class.java).insertBooking(
                    it.copy(id = newId)
                )
                Snackbar.make(this.findViewById<DrawerLayout>(R.id.drawerLayout), "Success", Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(resources.getColor(R.color.colorPrimary, theme)).show()
            }!!
        }
        activity?.onBackPressed()
    }

    private val showDatePickerDialog = Observer<Int> {
        val newFragment =
            DatePickerFragment(
                activity as MainActivity,
                this
            )
        newFragment.show(activity!!.supportFragmentManager, "datePicker")
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
         * @return A new instance of fragment NewBookingForm.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NewBookingForm().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
