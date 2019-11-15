package com.example.mysharedbooking

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavGraph
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUI.navigateUp
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.mysharedbooking.dataadaptersfragments.BookingListFragment
import com.example.mysharedbooking.dataadaptersfragments.UserListFragment
import com.example.mysharedbooking.databinding.DrawerHeaderBinding
import com.example.mysharedbooking.databinding.MainLayoutBinding
import com.example.mysharedbooking.models.MySharedBookingDB
import com.example.mysharedbooking.models.User
import com.example.mysharedbooking.viewmodels.MainViewModel
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout

import kotlinx.android.synthetic.main.main_layout.*

class MainActivity : AppCompatActivity(),
    HomeFrag.OnFragmentInteractionListener, UserListFragment.OnFragmentInteractionListener,
    NewBookingForm.OnFragmentInteractionListener, BookingListFragment.OnFragmentInteractionListener,
    BookExistingBookingFragment.OnFragmentInteractionListener, LoginFragment.OnFragmentInteractionListener{

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
        setupNavigation(binding, mainViewmodel)
        callbackManager = CallbackManager.Factory.create()

    }

   override fun onSupportNavigateUp(): Boolean {
       return navigateUp(findNavController(this, R.id.nav_host_fragment), appBarConfiguration)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun setupNavigation(binding: MainLayoutBinding, mainViewModel: MainViewModel) {
        val navController = findNavController(this, R.id.nav_host_fragment)

        appBarConfiguration = AppBarConfiguration.Builder(navController.graph).setDrawerLayout(drawerLayout).build()
        appBarConfiguration.topLevelDestinations.addAll(setOf(R.id.homeFrag, R.id.loginFragment, R.id.nav_host_fragment,
            R.id.userListFragment, R.id.bookingListFragment, R.id.bookExistingBookingFragment))
        NavigationUI.setupActionBarWithNavController(this,
                                             navController,
                                             appBarConfiguration)

        // Handle nav drawer item clicks
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when(menuItem.title){
                getString(R.string.users) -> {
                    val action = HomeFragDirections.actionHomeFragToUserListFragment()
                    navController.navigate(action)}
                getString(R.string.new_booking) -> {
                    val action = HomeFragDirections.actionHomeFragToNewBookingForm()
                    navController.navigate(action)
                }
                getString(R.string.list_booking) -> {
                    val action = HomeFragDirections.actionHomeFragToBookingListFragment()
                    navController.navigate(action)
                }
                getString(R.string.manage_links) -> {
                    val action = HomeFragDirections.actionHomeFragToBookExistingBookingFragment()
                    navController.navigate(action)
                }
                getString(R.string.logout) -> {
                    if( googleSignInClient != null ) googleLogout() else{ facebookLogout() }
                    mainViewmodel.resetLists(this)
                    mainViewModel.currentUser.postValue(null)
                }
            }
            menuItem.isChecked = true
            drawerLayout.closeDrawers()
            true
        }
        val imageBinding: DrawerHeaderBinding = DrawerHeaderBinding.bind(binding.navigationView.getHeaderView(0))
        imageBinding.lifecycleOwner = this
        binding.navigationView.getHeaderView(0)
        imageBinding.viewmodel = mainViewModel
    }

    fun googleLogout(){
        mainViewmodel.logged.value = false
        mainViewmodel.login.value = false
        googleSignInClient?.signOut()?.addOnCompleteListener {
            findNavController(this, R.id.nav_host_fragment).popBackStack(R.id.homeFrag, true)
            googleSignInClient = null
        }
    }

    fun facebookLogout(){
        mainViewmodel.logged.value = false
        mainViewmodel.login.value = false
        LoginManager.getInstance().logOut()
        findNavController(this, R.id.nav_host_fragment).popBackStack(R.id.homeFrag, true)
    }

}