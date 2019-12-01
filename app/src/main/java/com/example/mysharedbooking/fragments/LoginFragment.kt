package com.example.mysharedbooking.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.mysharedbooking.databinding.FragmentLoginBinding
import com.example.mysharedbooking.viewmodels.MainViewModel
import com.google.android.gms.auth.api.signin.*
import android.widget.Toast
import com.example.mysharedbooking.MainActivity
import com.example.mysharedbooking.R
import com.example.mysharedbooking.helpers.SocialFunctions
import com.facebook.*
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.SignInButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM121 = "param1"
private const val ARG_PARAM2987 = "param2"

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
    private var account: GoogleSignInAccount? = null
    private lateinit var callbackManager: CallbackManager
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var facebookLoginButton: LoginButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestProfile()
            .build()
        googleSignInClient = GoogleSignIn.getClient(activity as MainActivity, gso)
        firebaseAuth = FirebaseAuth.getInstance()
        callbackManager = CallbackManager.Factory.create()

        arguments?.let {
            param1 = it.getString(ARG_PARAM121)
            param2 = it.getString(ARG_PARAM2987)
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

        val accessToken = AccessToken.getCurrentAccessToken()
        val isLoggedIn = accessToken != null && !accessToken.isExpired
        if( isLoggedIn ) {
            mainViewModel.loading.postValue(View.VISIBLE)
            mainViewModel.fbAccessToken.value = accessToken
            firebaseAuthWithFacebookAccessToken(accessToken)
        }

        account = GoogleSignIn.getLastSignedInAccount(activity)
        if(account != null){
            firebaseAuthWithGoogle(account!!)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fragmentLoginBinding: FragmentLoginBinding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_login, container, false)
        //activity's ViewModel shared with some fragments
        fragmentLoginBinding.viewmodel = mainViewModel

        facebookLoginButton = fragmentLoginBinding.root.findViewById(R.id.facebook_button)
        facebookLoginButton.also {
            it.setPermissions("email")
            it.fragment = this
            it.setOnClickListener { View.OnClickListener { mainViewModel.loading.postValue(View.VISIBLE) } }
            it.registerCallback(
                callbackManager, object : FacebookCallback<LoginResult> {
                    override fun onCancel() {

                    }

                    override fun onError(error: FacebookException?) {
                        Toast.makeText(activity, "Error Logging with Facebook", Toast.LENGTH_LONG).show()
                    }

                    override fun onSuccess(result: LoginResult?) {
                        mainViewModel.fbAccessToken.value = result?.accessToken
                        if( result?.accessToken != null ) firebaseAuthWithFacebookAccessToken(result.accessToken)
                    }
                }
            )
        }

        val signInButton :SignInButton = fragmentLoginBinding.root.findViewById(R.id.login_google)
        fragmentLoginBinding.lifecycleOwner = this
        signInButton.setSize(SignInButton.SIZE_WIDE)
        signInButton.setOnClickListener {
            mainViewModel.loading.postValue(View.VISIBLE)
            signIn(googleSignInClient)
        }
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
            val googleSignInAccount = GoogleSignIn.getSignedInAccountFromIntent(data).result

            if(googleSignInAccount != null) {
                account = googleSignInAccount
                firebaseAuthWithGoogle(googleSignInAccount)
            }
        } else {
            if( requestCode == 64206 && resultCode == FacebookActivity.RESULT_OK) {
                callbackManager.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mainViewModel.loading.postValue(View.VISIBLE)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful ) {
                    task.result?.user?.getIdToken(true)?.addOnCompleteListener { task1 ->

                    mainViewModel.firebaseUser.postValue(task.result?.user)
                    SocialFunctions.SocialFunctionsHelpers.handleGoogleSignInResult(
                        acct,
                        activity as MainActivity,
                        mainViewModel,
                        googleSignInClient,
                        task.result?.user!!.uid,
                        task1.result?.token!!)
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("", "signInWithCredential:failure", task.exception)
                }
            }
    }

    private fun firebaseAuthWithFacebookAccessToken(token: AccessToken ) {

        val credential = FacebookAuthProvider.getCredential(token.token)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    task.result?.user?.getIdToken(true)?.addOnCompleteListener { task1 ->
                        mainViewModel.firebaseUser.postValue(task.result!!.user)
                        SocialFunctions.SocialFunctionsHelpers.downloadFBUserInfo(
                            token.userId,
                            activity as MainActivity,
                            mainViewModel,
                            task.result?.user!!.uid,
                            task1.result?.token!!
                        )
                    }
                } else {
                    mainViewModel.loading.postValue(View.INVISIBLE)
                    Snackbar.make(activity?.findViewById(R.id.drawerLayout)!!, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
                }
            }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
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
                    putString(ARG_PARAM121, param1)
                    putString(ARG_PARAM2987, param2)
                }
            }
    }
}
