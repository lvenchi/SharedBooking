package com.example.mysharedbooking

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.example.mysharedbooking.databinding.FragmentLoginBinding
import com.example.mysharedbooking.viewmodels.MainViewModel
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.tasks.Task
import android.content.ContentValues.TAG
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.viewModelScope
import com.example.mysharedbooking.helpers.SocialFunctions
import com.example.mysharedbooking.models.User
import com.facebook.*
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.Result
import com.google.android.material.navigation.NavigationView
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URL


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [LoginFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var mainViewModel: MainViewModel
    private lateinit var gso: GoogleSignInOptions
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .build()
        googleSignInClient = GoogleSignIn.getClient(activity as MainActivity, gso)
        callbackManager = CallbackManager.Factory.create()
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        mainViewModel = activity?.run {
            ViewModelProviders.of(activity as MainActivity).get(MainViewModel::class.java)
        }!!

        val loginResult = Observer<Boolean>{
            if(it) {
                signIn(googleSignInClient)
            }
        }

        mainViewModel.login.observe(this, loginResult)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fragmentLoginBinding: FragmentLoginBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        //activity's ViewModel shared with some fragments

        fragmentLoginBinding.viewmodel = mainViewModel

        val loginButton: LoginButton = fragmentLoginBinding.root.findViewById(R.id.facebook_button)
        loginButton.setPermissions("email")
        loginButton.fragment = this
        loginButton.registerCallback(callbackManager, MyFacebookCallBack(mainViewModel))

        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        return fragmentLoginBinding.root
    }

    private fun signIn( inputGoogleSignInClient : GoogleSignInClient) {
        val signInIntent = inputGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, 2)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 2 && resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            SocialFunctions.SocialFunctionsHelpers.handleGoogleSignInResult(task, activity!! as MainActivity, mainViewModel, googleSignInClient)
        } else {
            if( requestCode == 64206 && resultCode == FacebookActivity.RESULT_OK) {
                callbackManager.onActivityResult(requestCode, resultCode, data)
                mainViewModel.logged.value = true
                val action = LoginFragmentDirections.actionLoginFragmentToHomeFrag()
                Navigation.findNavController(activity as MainActivity, R.id.nav_host_fragment).navigate(action)
            }
        }
    }

    class MyFacebookCallBack( val mainViewModel: MainViewModel) : FacebookCallback<LoginResult>{

        override fun onCancel() {

        }

        override fun onError(error: FacebookException?) {

        }

        override fun onSuccess(result: LoginResult?) {
            mainViewModel.fbAccessToken.value = result?.accessToken
            SocialFunctions.SocialFunctionsHelpers.downloadFBUserInfo(result?.accessToken!!.userId, mainViewModel)
        }
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
         * @return A new instance of fragment LoginFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
