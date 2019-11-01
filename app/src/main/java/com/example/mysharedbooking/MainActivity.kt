package com.example.mysharedbooking

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUI.navigateUp
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.mysharedbooking.dataadaptersfragments.BookingListFragment
import com.example.mysharedbooking.dataadaptersfragments.UserListFragment
import com.example.mysharedbooking.models.MySharedBookingDB

import kotlinx.android.synthetic.main.main_layout.*

class MainActivity : AppCompatActivity(),
    HomeFrag.OnFragmentInteractionListener, UserListFragment.OnFragmentInteractionListener,
    NewBookingForm.OnFragmentInteractionListener, BookingListFragment.OnFragmentInteractionListener,
    BookExistingBookingFragment.OnFragmentInteractionListener, LoginFragment.OnFragmentInteractionListener{

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

    //private val menuListener:  = this::onMenuItemSelected

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)

        //setSupportActionBar(toolbar)

        setupNavigation()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navigateUp(findNavController(this, R.id.nav_host_fragment), drawerLayout)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun setupNavigation() {
        val navController = findNavController(this, R.id.nav_host_fragment)

        // Update action bar to reflect navigation

        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        setupWithNavController(navigationView, navController)
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
            }
            menuItem.isChecked = true
            drawerLayout.closeDrawers()
            true
        }
    }
}