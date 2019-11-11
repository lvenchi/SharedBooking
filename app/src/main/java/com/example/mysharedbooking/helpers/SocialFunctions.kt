package com.example.mysharedbooking.helpers

import android.graphics.drawable.Drawable
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.mysharedbooking.MainActivity
import com.example.mysharedbooking.models.User
import com.example.mysharedbooking.viewmodels.MainViewModel
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
        fun downloadFBUserInfo(userId: String?, viewModel: MainViewModel){

            viewModel.viewModelScope.launch(Dispatchers.IO) {
                var connection: HttpsURLConnection? = null
                var response: Int = 0

                try {

                    connection = (URL(
                        "https://graph.facebook.com/"+
                            userId+"?fields=id,picture,name,first_name,last_name,email&access_token="+
                                viewModel.fbAccessToken.value?.token)
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
                                viewModel.myDatabase.myDao().insertUsers(
                                    User(0,
                                        username = serverResponse.getString("name"),
                                        firstName = serverResponse.getString("first_name"),
                                        lastName = serverResponse.getString("last_name"),
                                        email = serverResponse.getString("email"),
                                        role = "",
                                        profilePic = profileURL,
                                        providerId = serverResponse.getString("id") )
                                )
                                viewModel.currentUser.postValue(viewModel.myDatabase.myDao().findUserByEmail( serverResponse.getString("email")))
                                downloadUserProfilePic(
                                    URL(
                                        serverResponse
                                            .getJSONObject("picture")
                                            ?.getJSONObject("data")
                                            ?.get("url") as String
                                    ), viewModel
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
            }
        }

        fun downloadUserProfilePic(userPicURL: URL, mainViewModel: MainViewModel){
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