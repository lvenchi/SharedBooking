package com.example.mysharedbooking

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUI.navigateUp
import androidx.room.Room
import com.example.mysharedbooking.databinding.DrawerHeaderBinding
import com.example.mysharedbooking.databinding.MainLayoutBinding
import com.example.mysharedbooking.fragments.*
import com.example.mysharedbooking.models.MySharedBookingDB
import com.example.mysharedbooking.viewmodels.MainViewModel
import com.facebook.CallbackManager
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.main_layout.*


class MainActivity : AppCompatActivity(),
    HomeFrag.OnFragmentInteractionListener, UserListFragment.OnFragmentInteractionListener,
    NewBookingForm.OnFragmentInteractionListener, LoginFragment.OnFragmentInteractionListener, UserDetailFragment.OnFragmentInteractionListener{

    var googleSignInClient: GoogleSignInClient? = null
    lateinit var mainViewmodel: MainViewModel
    lateinit var callbackManager: CallbackManager
    lateinit var appBarConfiguration: AppBarConfiguration

    override fun onFragmentInteraction(uri: Uri) {
    }

    companion object {
        private var INSTANCE: MySharedBookingDB? = null

        fun getInMemoryDatabase(context: Context) : MySharedBookingDB {

            INSTANCE = INSTANCE ?: Room.databaseBuilder(context, MySharedBookingDB::class.java, "MyDB")
                .fallbackToDestructiveMigration() //temporary
                .build()
            return INSTANCE!!
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewmodel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        val binding: MainLayoutBinding = DataBindingUtil.setContentView(this, R.layout.main_layout)
        binding.viewmodel = mainViewmodel
        setupNavigation( binding )
        callbackManager = CallbackManager.Factory.create()
    }

    override fun onSupportNavigateUp(): Boolean {
       return navigateUp(findNavController(this, R.id.nav_host_fragment), appBarConfiguration)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            val currFrag: Int? = findNavController(this, R.id.nav_host_fragment).currentDestination?.id
            if( currFrag != R.id.homeFrag && currFrag != R.id.loginFragment ) super.onBackPressed()
            else {
                moveTaskToBack(true)
            }
        }
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        createChannel()
        return super.onCreateView(name, context, attrs)
    }

    private fun setupNavigation(binding: MainLayoutBinding) {
        val navController = findNavController(this, R.id.nav_host_fragment)

        appBarConfiguration = AppBarConfiguration.Builder(navController.graph).setDrawerLayout(drawerLayout).build()
        appBarConfiguration.topLevelDestinations.addAll(setOf(R.id.homeFrag, R.id.loginFragment, R.id.nav_host_fragment))
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)

        // Handle nav drawer item clicks
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when(menuItem.title){
                getString(R.string.users) -> {
                    val action = HomeFragDirections.actionHomeFragToUserListFragment()
                    navController.navigate(action)}
                getString(R.string.logout) -> {
                    FirebaseAuth.getInstance().signOut()
                    if( googleSignInClient != null ) googleLogout() else{ facebookLogout() }
                    mainViewmodel.resetLists(this)
                    mainViewmodel.currentUser.postValue(null)
                }
            }

            menuItem.isChecked = false
            drawerLayout.closeDrawers()
            true
        }

        val imageBinding: DrawerHeaderBinding = DrawerHeaderBinding.bind(binding.navigationView.getHeaderView(0))
        imageBinding.lifecycleOwner = this
        imageBinding.viewmodel = mainViewmodel
    }

    fun googleLogout(){
        mainViewmodel.logged.value = false
        mainViewmodel.login.value = false
        googleSignInClient?.signOut()?.addOnCompleteListener {
            val action = HomeFragDirections.actionHomeFragToLoginFragment()
            findNavController(this, R.id.nav_host_fragment).navigate(action)
            googleSignInClient = null
        }
    }

    fun facebookLogout(){
        mainViewmodel.logged.value = false
        mainViewmodel.login.value = false
        LoginManager.getInstance().logOut()
        val action = HomeFragDirections.actionHomeFragToLoginFragment()
        findNavController(this, R.id.nav_host_fragment).navigate(action)
    }

    fun createChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                getString(R.string.book_channel_id),
                getString(R.string.book_channel_id),
                NotificationManager.IMPORTANCE_LOW
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Booking Notification"

            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(notificationChannel)
        }
    }
}