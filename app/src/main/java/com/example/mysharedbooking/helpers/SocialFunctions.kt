package com.example.mysharedbooking.helpers

import android.app.Activity
import android.content.ContentValues
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import com.example.mysharedbooking.MainActivity
import com.example.mysharedbooking.R
import com.example.mysharedbooking.fragments.LoginFragmentDirections
import com.example.mysharedbooking.models.User
import com.example.mysharedbooking.viewmodels.MainViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedInputStream
import java.lang.Exception
import java.lang.ref.WeakReference
import java.net.MalformedURLException
import java.net.URL
import java.nio.charset.Charset
import javax.net.ssl.HttpsURLConnection

class SocialFunctions {

    object SocialFunctionsHelpers{

        @JvmStatic
        fun downloadFBUserInfo(userId: String?, activity: MainActivity, mainViewModel: MainViewModel,
                               firebaseId: String, firebaseToken: String){

            mainViewModel.viewModelScope.launch(Dispatchers.IO) {
                var connection: HttpsURLConnection? = null
                var response: Int = 0

                try {

                    connection = (URL(
                        "https://graph.facebook.com/"+
                            userId+"?fields=id,picture,name,first_name,last_name,email&access_token="+
                                mainViewModel.fbAccessToken.value?.token)
                        .openConnection() as? HttpsURLConnection)
                    connection?.run {
                        readTimeout = 3000
                        connectTimeout = 3000
                        requestMethod = "GET"
                        doInput = true
                        connect()
                        response = responseCode
                        if (response == HttpsURLConnection.HTTP_OK) {
                            inputStream?.let { stream ->
                                val serverResponse = JSONObject(
                                    String(
                                        BufferedInputStream(stream).readBytes(),
                                        Charset.forName("iso-8859-1")
                                    )
                                )
                                val profileURL: String = serverResponse
                                    .getJSONObject("picture")
                                    ?.getJSONObject("data")
                                    ?.get("url") as String

                                val insertedUser = User(
                                    email = serverResponse.getString("email"),
                                    username = serverResponse.getString("name"),
                                    firstName = serverResponse.getString("first_name"),
                                    lastName = serverResponse.getString("last_name"),
                                    role = "",
                                    profilePic = profileURL,
                                    firebaseId = firebaseId,
                                    firebaseToken = firebaseToken)
                                if(MainActivity.getInMemoryDatabase(activity.baseContext).myDao()
                                        .findUserByEmail( serverResponse.getString("email")) == null) {
                                    MainActivity.getInMemoryDatabase(activity.baseContext).myDao()
                                        .insertUsers(insertedUser)
                                    mainViewModel.insertUser(insertedUser)
                                }
                                mainViewModel.currentUser.postValue(insertedUser)
                                downloadUserProfilePic(
                                    URL(
                                        serverResponse
                                            .getJSONObject("picture")
                                            ?.getJSONObject("data")
                                            ?.get("url") as String
                                    ), mainViewModel
                                )
                            }
                        }
                    }
                } catch (exception: Exception){
                    println(exception.toString())
                } finally {
                    // Close Stream and disconnect HTTP connection.
                    //connection?.inputStream?.close()
                    connection?.disconnect()
                }
            }.invokeOnCompletion {
                mainViewModel.logged.postValue(true)
                mainViewModel.loading.postValue(View.INVISIBLE)
                activity.runOnUiThread {
                    val action = LoginFragmentDirections.actionLoginFragmentToHomeFrag()
                    Navigation.findNavController(activity, R.id.nav_host_fragment).navigate(action)
                }
            }
        }

        fun handleGoogleSignInResult(account :GoogleSignInAccount, activity: MainActivity,
                                             mainViewModel: MainViewModel, googleSignInClient: GoogleSignInClient,
                                     firebaseId: String, firebaseToken: String){

            try {
                (activity).googleSignInClient = googleSignInClient

                mainViewModel.viewModelScope.launch(Dispatchers.IO) {
                    var newUser = MainActivity.getInMemoryDatabase(activity.baseContext).myDao().findUserByEmail(account.email!!)
                    if( newUser == null) {
                        newUser = User(
                            email = account.email!!,
                            firstName = account.givenName,
                            lastName = account.familyName,
                            username = account.displayName,
                            role = "",
                            profilePic = account.photoUrl.toString(),
                            firebaseId = firebaseId,
                            firebaseToken = firebaseToken
                            )
                        MainActivity.getInMemoryDatabase(activity.baseContext).myDao().insertUsers(newUser)
                        mainViewModel.insertUser(newUser)
                    }
                    mainViewModel.currentUser.postValue(newUser)
                    downloadUserProfilePic(URL(account.photoUrl?.toString()), mainViewModel)
                }.invokeOnCompletion {
                    mainViewModel.loading.postValue(View.INVISIBLE)
                    mainViewModel.logged.postValue(true)
                    activity.runOnUiThread {
                        val action = LoginFragmentDirections.actionLoginFragmentToHomeFrag()
                        Navigation.findNavController(activity, R.id.nav_host_fragment).navigate(action)
                    }
                }

            } catch (e: ApiException) {
                Log.w(ContentValues.TAG, "signInResult:failed code=" + e.statusCode)
            }
        }

        private fun downloadUserProfilePic(userPicURL: URL, mainViewModel: MainViewModel){
            var connection: HttpsURLConnection? = null
            var response: Int = 0
            try {
                connection = userPicURL.openConnection() as? HttpsURLConnection
                connection?.run {
                    readTimeout = 3000
                    connectTimeout = 3000
                    requestMethod = "GET"
                    doInput = true
                    connect()

                    response = responseCode
                    if(response == HttpsURLConnection.HTTP_OK) {
                        inputStream?.let { stream ->
                            mainViewModel.profileImage.postValue(
                                WeakReference(
                                    Drawable.createFromStream(
                                        stream,
                                        "profilePic"
                                    )
                                )
                            )
                        }
                    }
                }
            } catch (exception: Exception){
                Log.e("Photo", exception.toString())
            } finally {
                // Close Stream and disconnect HTTP connection.
                //connection?.inputStream?.close()
                connection?.disconnect()
            }
        }

    }
}