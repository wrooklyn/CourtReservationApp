package it.polito.mad.courtreservationapp.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.databinding.ActivityMainBinding
import it.polito.mad.courtreservationapp.db.relationships.ReservationWithSportCenter
import it.polito.mad.courtreservationapp.models.Reservation
import it.polito.mad.courtreservationapp.models.User
import it.polito.mad.courtreservationapp.view_model.ReservationBrowserViewModel
import it.polito.mad.courtreservationapp.view_model.UserViewModel
import it.polito.mad.courtreservationapp.views.homeManager.HomeFragment
import it.polito.mad.courtreservationapp.views.reservationManager.BrowseReservationsFragment
import it.polito.mad.courtreservationapp.views.reservationManager.CreateReservationActivity
import it.polito.mad.courtreservationapp.views.reservationManager.ReservationBrowserActivity

class MainActivity : AppCompatActivity() {

    lateinit var userViewModel: UserViewModel
    lateinit var reservationBrowserViewModel: ReservationBrowserViewModel
    lateinit var user: User
    lateinit var userReservations: List<Reservation>
    lateinit var userReservationsLocations: List<ReservationWithSportCenter>
    lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /* Setting the logged user */
        /* TODO: login function? */
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        userViewModel.setCurrentUser(1)
        userViewModel.user.observe(this) {
            user = it
            Log.i("DBG", "Logged user")
            Log.i("DBG", user.toString())
        }
        /* ------------------- */

        /* Getting current user's reservations */
        reservationBrowserViewModel =
            ViewModelProvider(this)[ReservationBrowserViewModel::class.java]
        reservationBrowserViewModel.initUserReservations(1) // TODO: use actual user ID

        reservationBrowserViewModel.userReservations.observe(this) {
            userReservations = it
        }

        reservationBrowserViewModel.userReservationsLocations.observe(this) {
            userReservationsLocations = it
        }
        /* -------------------- */

        replaceFragment(HomeFragment())
        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    replaceFragment(HomeFragment())
                }
                R.id.explore -> {
                    Log.i("DBG", "Explore button pressed")
                    replaceFragment(ShowUnimplementedFragment())
                }
//                R.id.calendar -> {
//                    val reservationBrowserIntent = Intent(this, ReservationBrowserActivity::class.java)
//                    startActivity(reservationBrowserIntent)
//                }
                R.id.calendar -> {
                    replaceFragment(BrowseReservationsFragment())
                }
                R.id.chat -> {
                    replaceFragment(ShowUnimplementedFragment())
                    testLaunchGabry()
                }
                R.id.profile -> {
                    replaceFragment(ShowProfileFragment())
                }
                else -> {
                    Log.i("DBG", "Invalid")
                    replaceFragment(ShowUnimplementedFragment())
                }
            }
            true
        }
    }

    private fun testLaunchGabry() {
        val createReservationIntent: Intent = Intent(this, CreateReservationActivity::class.java)
        createReservationIntent.putExtra("courtId",1L)
        startActivity(createReservationIntent)
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.commit()
    }


}
