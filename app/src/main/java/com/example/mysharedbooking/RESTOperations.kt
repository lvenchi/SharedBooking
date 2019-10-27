package com.example.mysharedbooking

import android.app.Application
import android.content.Context
import android.widget.Toast
import com.example.mysharedbooking.models.Booking
import com.example.mysharedbooking.models.User
import com.example.mysharedbooking.models.UserBooking
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.Moshi
import org.json.JSONArray
import java.io.ByteArrayInputStream
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL
import java.nio.ByteBuffer
import java.nio.charset.Charset

class RESTOperations {

    object Operations{
        @JvmStatic
        fun registerUser(newUser: String): Int {
            var response = 0
            val url = URL("http://192.168.1.8:44300/api/Users/")
            val moshi = Moshi.Builder().build()

            val adapter: JsonAdapter<User> = moshi.adapter(User::class.java)
            var connection: HttpURLConnection? = null
            try {
                connection = (url.openConnection() as? HttpURLConnection)
                connection?.run {
                    readTimeout = 3000
                    connectTimeout = 3000
                    // For this use case, set HTTP method to GET.
                    requestMethod = "POST"
                    doInput = true
                    setRequestProperty("Content-Type","application/json")
                    outputStream.write( adapter.toJson(User(0, newUser,"franco", newUser, "ruolone" )).toByteArray(Charsets.UTF_8) )
                    connection.outputStream?.close()
                    // Open communications link (network traffic occurs here).
                    connect()
                    //publishProgress(CONNECT_SUCCESS)
                    response = responseCode
                    // Retrieve the response body as an InputStream.
                    //publishProgress(GET_INPUT_STREAM_SUCCESS, 0)
                    /*inputStream?.let { stream ->
                        // Converts Stream to String with max length of 500.
                        print(String(stream.readBytes()))
                    }*/
                }
            } finally {
                // Close Stream and disconnect HTTP connection.
                //connection?.inputStream?.close()
                connection?.disconnect()
            }
            return response
        }

        @JvmStatic
        fun registerBooking(newBooking: Booking): Int {
            var response = 0
            val url = URL("http://192.168.1.8:44300/api/Bookings/")
            val moshi = Moshi.Builder().build()

            val adapter: JsonAdapter<Booking> = moshi.adapter(Booking::class.java)
            var connection: HttpURLConnection? = null
            try {
                connection = (url.openConnection() as? HttpURLConnection)
                connection?.run {
                    readTimeout = 3000
                    connectTimeout = 3000
                    requestMethod = "POST"
                    doInput = true
                    setRequestProperty("Content-Type","application/json")
                    outputStream.write( adapter.toJson(newBooking).toByteArray(Charsets.UTF_8) )
                    connection.outputStream?.close()
                    // Open communications link (network traffic occurs here).
                    connect()
                    //publishProgress(CONNECT_SUCCESS)
                    response = responseCode
                    // Retrieve the response body as an InputStream.
                    //publishProgress(GET_INPUT_STREAM_SUCCESS, 0)
                    /*inputStream?.let { stream ->
                        // Converts Stream to String with max length of 500.
                        print(String(stream.readBytes()))
                    }*/
                }
            } finally {
                // Close Stream and disconnect HTTP connection.
                //connection?.inputStream?.close()
                connection?.disconnect()
            }
            return response
        }

        @JvmStatic
        fun getUsers(): List<User> {
            var response = 0
            val url = URL("http://192.168.1.8:44300/api/Users/")
            val moshi = Moshi.Builder().build()
            lateinit var userList: List<User>
            val adapter: JsonAdapter<Array<User>> = moshi.adapter(Array<User>::class.java)//adapter(User::class.java)
            var connection: HttpURLConnection? = null
            try {
                connection = (url.openConnection() as? HttpURLConnection)
                connection?.run {
                    readTimeout = 3000
                    connectTimeout = 3000
                    requestMethod = "GET"
                    doInput = true
                    // Open communications link (network traffic occurs here).
                    connect()
                    //publishProgress(CONNECT_SUCCESS)
                    response = responseCode
                    // Retrieve the response body as an InputStream.
                    //publishProgress(GET_INPUT_STREAM_SUCCESS, 0)
                    inputStream?.let { stream ->
                        val byteArr: ByteArray = ByteArray(1000)
                        val byteBuffer = ByteBuffer.wrap(byteArr)
                        //val stringBuilder = StringBuilder()
                        val read = 0
                        /*while( stream.buffered().read(byteArr) > 0 ){
                            stringBuilder.append(String(byteArr))
                        }*/
                        //stringBuilder.append(stream.readBytes())
                        val str = String(stream.readBytes())
                        println(str)
                        userList = adapter.fromJson(str)!!.toList()
                        //println(adapter.fromJson(stringBuilder.toString()).toString())

                    }
                }
            } finally {
                // Close Stream and disconnect HTTP connection.
                //connection?.inputStream?.close()
                connection?.disconnect()
            }
            return userList
        }

        @JvmStatic
        fun getUsersBooking(): UserBooking {
            var response = 0
            val url = URL("http://192.168.1.8:44300/api/UsersBooking/")
            val moshi = Moshi.Builder().build()
            lateinit var usersBooking: UserBooking
            val adapter: JsonAdapter<UserBooking> = moshi.adapter(UserBooking::class.java)//adapter(User::class.java)
            var connection: HttpURLConnection? = null
            try {
                connection = (url.openConnection() as? HttpURLConnection)
                connection?.run {
                    readTimeout = 3000
                    connectTimeout = 3000
                    requestMethod = "GET"
                    doInput = true
                    // Open communications link (network traffic occurs here).
                    connect()
                    //publishProgress(CONNECT_SUCCESS)
                    response = responseCode
                    // Retrieve the response body as an InputStream.
                    //publishProgress(GET_INPUT_STREAM_SUCCESS, 0)
                    inputStream?.let { stream ->
                        val byteArr: ByteArray = ByteArray(1000)
                        val byteBuffer = ByteBuffer.wrap(byteArr)
                        //val stringBuilder = StringBuilder()
                        val read = 0
                        /*while( stream.buffered().read(byteArr) > 0 ){
                            stringBuilder.append(String(byteArr))
                        }*/
                        //stringBuilder.append(stream.readBytes())
                        val str = String(stream.readBytes())
                        println(str)
                        usersBooking = adapter.fromJson(str)!!
                        //println(adapter.fromJson(stringBuilder.toString()).toString())

                    }
                }
            } finally {
                // Close Stream and disconnect HTTP connection.
                //connection?.inputStream?.close()
                connection?.disconnect()
            }
            return usersBooking
        }
    }
}