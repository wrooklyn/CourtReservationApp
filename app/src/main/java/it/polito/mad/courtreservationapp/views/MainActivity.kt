package it.polito.mad.courtreservationapp.views

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.databinding.ActivityMainBinding
import it.polito.mad.courtreservationapp.db.relationships.ReservationWithServices
import it.polito.mad.courtreservationapp.db.relationships.ReservationWithSportCenter
import it.polito.mad.courtreservationapp.db.relationships.SportCenterWithCourtsAndServices
import it.polito.mad.courtreservationapp.db.relationships.UserWithSportMasteriesAndName
import it.polito.mad.courtreservationapp.models.Reservation
import it.polito.mad.courtreservationapp.models.User
import it.polito.mad.courtreservationapp.view_model.ReservationBrowserViewModel
import it.polito.mad.courtreservationapp.view_model.SportCenterViewModel
import it.polito.mad.courtreservationapp.view_model.SportMasteryViewModel
import it.polito.mad.courtreservationapp.view_model.UserViewModel
import it.polito.mad.courtreservationapp.views.homeManager.HomeFragment
import it.polito.mad.courtreservationapp.views.profile.ShowProfileFragment
import it.polito.mad.courtreservationapp.views.ratings.LeaveRatingActivity
import it.polito.mad.courtreservationapp.views.reservationManager.BrowseReservationsFragment

class MainActivity : AppCompatActivity() {

    val userViewModel: UserViewModel by viewModels()
    val reservationBrowserViewModel: ReservationBrowserViewModel by viewModels()
    val sportCenterViewModel: SportCenterViewModel by viewModels()

    lateinit var user: User
    lateinit var userWithSportMasteriesAndName: UserWithSportMasteriesAndName
    lateinit var userReservations: List<Reservation>
    lateinit var userReservationsLocations: List<ReservationWithSportCenter>
    lateinit var userReservationsServices: List<ReservationWithServices>
    lateinit var binding: ActivityMainBinding

    lateinit var sportCenters : List<SportCenterWithCourtsAndServices>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        /* Setting the logged user */
        //hardcoded user
        /* Load user's info in both the User object and the shared preferences */
        userViewModel.setCurrentUser(1)
        userViewModel.user.observe(this) {
            user = it
            loadUserInfo(user.userId)
        }
        userViewModel.userWithSportMasteriesAndNameLiveData.observe(this){
            userViewModel.userWithSportMasteriesAndName = it
            userWithSportMasteriesAndName = it
        }

        val sharedPreferences = this.getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putLong("UserId", 1).apply()

        /* Getting current user's reservations */
        reservationBrowserViewModel.initUserReservations(1) // TODO: use actual user ID

        reservationBrowserViewModel.userReservations.observe(this) {
            userReservations = it
        }

        reservationBrowserViewModel.userReservationsLocations.observe(this) {
            userReservationsLocations = it
        }

        reservationBrowserViewModel.userReservationsServices.observe(this) {
            userReservationsServices = it
        }
        /* -------------------- */



        replaceFragment(HomeFragment())
        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    replaceFragment(HomeFragment())
                }
                R.id.explore -> {
                    replaceFragment(ShowUnimplementedFragment())
                }

                R.id.calendar -> {
                    replaceFragment(BrowseReservationsFragment())
                }
                R.id.chat -> {
                    //TODO remove this in production
                    val intent = Intent(this, LeaveRatingActivity::class.java)
                    intent.putExtra("courtId", 1L)
                    intent.putExtra("reservationId", 1L)
                    intent.putExtra("sportCenterId", 1L)
                    startActivity(intent)
                    replaceFragment(ShowUnimplementedFragment())
                }
                R.id.profile -> {
                    replaceFragment(ShowProfileFragment())
                }
                else -> {
                    replaceFragment(ShowUnimplementedFragment())
                }
            }
            true
        }
    }

    fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.commit()
    }


    private fun loadUserInfo(userId: Long) {
        val sharedPreferences = this.getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putLong("UserId", userId)
        editor.putString("username", user.username)
        editor.putString("firstname", user.firstName)
        editor.putString("lastname", user.lastName)
        editor.putString("email", user.email)
        editor.putString("address", user.address)
        editor.putInt("gender", user.gender)
        editor.putInt("height", user.height)
        editor.putInt("weight", user.weight)
        editor.putString("phone", user.phone)
        editor.apply()
    }
}
