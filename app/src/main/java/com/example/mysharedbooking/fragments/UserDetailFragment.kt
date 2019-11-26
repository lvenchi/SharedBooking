package com.example.mysharedbooking.fragments

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.example.mysharedbooking.R
import com.example.mysharedbooking.databinding.FragmentUserDetailBinding
import com.example.mysharedbooking.viewmodels.UsersViewModel
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.lang.Exception

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1_ = "param1"
private const val ARG_PARAM2_ = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [UserDetailFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [UserDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserDetailFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private var userIndex: Int = 0
    private lateinit var userViewModel: UsersViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            //param1 = it.getString(ARG_PARAM1_)
            //param2 = it.getString(ARG_PARAM2_)
            userIndex = it.getInt("userIndex")
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val userDetailFragmentBinding: FragmentUserDetailBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_user_detail, container, false)
        userDetailFragmentBinding.lifecycleOwner = this
        userViewModel = activity!!.run {ViewModelProviders.of(this).get(UsersViewModel::class.java)}
        Picasso.get().load(userViewModel.currentUser!!.profilePic).resize(200, 200).into(
            object: Target{
                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

                }

                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                    userViewModel.profilePicture.value = resources.getDrawable(R.drawable.app_icon)
                }

                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    userViewModel.profilePicture.value = (BitmapDrawable(resources, bitmap))
                }
            }
        )
        userDetailFragmentBinding.viewmodel = userViewModel
        return userDetailFragmentBinding.root
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
         * @return A new instance of fragment UserDetailFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1_, param1)
                    putString(ARG_PARAM2_, param2)
                }
            }
    }
}
