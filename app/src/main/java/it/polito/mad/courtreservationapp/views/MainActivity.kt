package it.polito.mad.courtreservationapp.views

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.ListenerRegistration
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.databinding.ActivityMainBinding
import it.polito.mad.courtreservationapp.db.RemoteRepository.RemoteStorage
import it.polito.mad.courtreservationapp.db.relationships.ReservationWithReview
import it.polito.mad.courtreservationapp.db.relationships.ReservationWithServices
import it.polito.mad.courtreservationapp.db.relationships.ReservationWithSportCenter
import it.polito.mad.courtreservationapp.db.relationships.SportCenterWithCourtsAndServices
import it.polito.mad.courtreservationapp.db.relationships.UserWithSportMasteriesAndName
import it.polito.mad.courtreservationapp.db.repository.FireCourtRepository
import it.polito.mad.courtreservationapp.db.repository.FireSportCenterRepository
import it.polito.mad.courtreservationapp.models.Reservation
import it.polito.mad.courtreservationapp.models.SportCenter
import it.polito.mad.courtreservationapp.models.User
import it.polito.mad.courtreservationapp.view_model.*
import it.polito.mad.courtreservationapp.views.homeManager.HomeFragment
import it.polito.mad.courtreservationapp.views.profile.ShowProfileFragment
import it.polito.mad.courtreservationapp.views.ratings.LeaveRatingActivity
import it.polito.mad.courtreservationapp.views.reservationManager.BrowseReservationsFragment
import it.polito.mad.courtreservationapp.views.social.FriendList
import it.polito.mad.courtreservationapp.views.social.ShowSocialPageFragment

class MainActivity : AppCompatActivity() {

    val userViewModel: UserViewModel by viewModels() //done
    val reservationBrowserViewModel: ReservationBrowserViewModel by viewModels()
    val sportCenterViewModel: SportCenterViewModel by viewModels() //done
    val sportMasteryViewModel: SportMasteryViewModel by viewModels() //done
    val ratingViewModel: LeaveRatingViewModel by viewModels() //done

    val registerForActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        println("${it}")
        if (it.resultCode == Activity.RESULT_OK){
            userViewModel.refreshUser()
        }
    }

//    lateinit var user: User
//    lateinit var userWithSportMasteriesAndName: UserWithSportMasteriesAndName
    lateinit var userReservations: List<Reservation>
    lateinit var userReservationsLocations: List<ReservationWithSportCenter>
    lateinit var userReservationsServices: List<ReservationWithServices>
    lateinit var userReservationsReviews: List<ReservationWithReview>
    lateinit var binding: ActivityMainBinding

    lateinit var sportCenters : List<SportCenterWithCourtsAndServices>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sportCenterViewModel.initData()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        /* Setting the logged user */
        //hardcoded user
        /* Load user's info in both the User object and the shared preferences */
        userViewModel.setCurrentUser("chndavide@gmail.com") //TODO: get email from login
//        userViewModel.userLiveData.observe(this) {
//            userViewModel.user = it
//            loadUserInfo()
//        }
        userViewModel.userWithSportMasteriesAndNameLiveData.observe(this){
            println("observer got: $it")
            userViewModel.user = it.user
            userViewModel.userWithSportMasteriesAndName = it
            loadUserInfo()

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

        reservationBrowserViewModel.userReservationsReviews.observe(this) {
            userReservationsReviews = it
            Log.i("ASAASAS", "RESULT: $it")
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
                    replaceFragment(ShowSocialPageFragment())
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


    private fun loadUserInfo() {
        val sharedPreferences = this.getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
//        editor.putLong("UserId", userId)
        editor.putString("username", userViewModel.user.username)
        editor.putString("firstname", userViewModel.user.firstName)
        editor.putString("lastname", userViewModel.user.lastName)
        editor.putString("email", userViewModel.user.email)
        editor.putString("address", userViewModel.user.address)
        editor.putInt("gender", userViewModel.user.gender)
        editor.putInt("height", userViewModel.user.height)
        editor.putInt("weight", userViewModel.user.weight)
        editor.putString("phone", userViewModel.user.phone)
        editor.apply()
    }
}
