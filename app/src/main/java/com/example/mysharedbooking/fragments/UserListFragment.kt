package com.example.mysharedbooking.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.ImageViewCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager

import androidx.recyclerview.widget.RecyclerView
import com.example.mysharedbooking.MainActivity
import com.example.mysharedbooking.R
import com.example.mysharedbooking.dataadapters.UserBookingViewHolder
import com.example.mysharedbooking.dataadapters.UserViewHolder
import com.example.mysharedbooking.databinding.FragmentUserListBinding
import com.example.mysharedbooking.models.MySharedBookingDB
import com.example.mysharedbooking.models.User
import com.example.mysharedbooking.viewmodels.UsersViewModel
import com.squareup.picasso.Picasso

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM3 = "param1"
private const val ARG_PARAM4 = "param2"
lateinit var myDatabase: MySharedBookingDB

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [UserListFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [UserListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var userViewModel: UsersViewModel
    private var userList: List<User> = ArrayList()
    private var userListObservable: MutableLiveData<List<User>> = MutableLiveData()
    var usersRecyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM3)
            param2 = it.getString(ARG_PARAM4)
        }
    }

    override fun onStart() {
        super.onStart()

        val downloadUsers = Observer<List<User>> { newList ->
            usersRecyclerView?.adapter =
                UserAdapter(
                    newList, activity!! as MainActivity, userViewModel
                )
        }
        userViewModel.getUserList().observe(this, downloadUsers)
        usersRecyclerView = activity?.findViewById<RecyclerView>(R.id.usersListView)?.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            UserAdapter(
                userList, activity!! as MainActivity, userViewModel
            )
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val userListFragmentBinding: FragmentUserListBinding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_user_list, container, false)
        userViewModel = activity!!.run {ViewModelProviders.of(this).get(UsersViewModel::class.java)}
        userListFragmentBinding.viewmodel = userViewModel

        return userListFragmentBinding.root
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
            myDatabase =
                MainActivity.getInMemoryDatabase(activity!!.applicationContext)
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    class UserAdapter(private val userList: List<User>, private val activity: MainActivity, val usersViewModel: UsersViewModel): RecyclerView.Adapter<UserViewHolder>(), UserViewHolder.ItemClickListener{


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
            val inflatedView = LayoutInflater.from(parent.context)
                .inflate(R.layout.user_holder, parent, false) as ViewGroup

            return UserViewHolder(
                inflatedView
            )
        }

        override fun getItemCount(): Int {
            return userList.size
        }

        override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
            holder.inflatedViewHolder.findViewById<TextView>( R.id.username ).text = userList[position].username
            Picasso.get().load(userList[position].profilePic).resize(100, 100)
                .into(holder.inflatedViewHolder.findViewById<ImageView>( R.id.user_image))
            holder.bind(userList[position], this )
        }

        override fun onItemClicked(user: User) {
            usersViewModel.currentUser = user
            val navController = Navigation.findNavController(activity, R.id.nav_host_fragment)
            val action = UserListFragmentDirections.actionUserListFragmentToUserDetailFragment(userList.indexOf(user))
            navController.navigate(action)
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
         * @return A new instance of fragment UserListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM3, param1)
                    putString(ARG_PARAM4, param2)
                }
            }
    }
}
