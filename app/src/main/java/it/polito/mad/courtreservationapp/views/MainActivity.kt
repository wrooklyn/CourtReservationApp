package it.polito.mad.courtreservationapp.views

//import com.google.firebase.firestore.ListenerRegistration
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.databinding.ActivityMainBinding
import it.polito.mad.courtreservationapp.db.relationships.*
import it.polito.mad.courtreservationapp.models.Invite
import it.polito.mad.courtreservationapp.models.Reservation
import it.polito.mad.courtreservationapp.models.Sport
import it.polito.mad.courtreservationapp.models.SportMastery
import it.polito.mad.courtreservationapp.view_model.*
import it.polito.mad.courtreservationapp.views.homeManager.HomeFragment
import it.polito.mad.courtreservationapp.views.login.SavedPreference
import it.polito.mad.courtreservationapp.views.profile.ShowProfileFragment
import it.polito.mad.courtreservationapp.views.reservationManager.BrowseReservationsFragment
import it.polito.mad.courtreservationapp.views.social.ShowSocialPageFragment

class MainActivity : AppCompatActivity() {

    val userViewModel: UserViewModel by viewModels() //done
    val reservationBrowserViewModel: ReservationBrowserViewModel by viewModels()
    val sportCenterViewModel: SportCenterViewModel by viewModels() //done
//    val sportMasteryViewModel: SportMasteryViewModel by viewModels() //done
//    val ratingViewModel: LeaveRatingViewModel by viewModels() //done
    val invitesViewModel: InvitesManagerViewModel by viewModels()

    val registerForAchievementActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        println("${it}")
        Log.d("registerActivity", "${it}")
        if (it.resultCode == Activity.RESULT_OK){
            userViewModel.refreshUser(this)
        }
    }

    val registerForReservationActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        println("$it")
        Log.d("registerActivityReservation", "$it")
        if (it.resultCode == Activity.RESULT_OK){
            reservationBrowserViewModel.initUserReservations(SavedPreference.EMAIL)
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

    lateinit var userInvitesSent: List<Invite>
    lateinit var userInvitesReceived: List<Invite>

    // declare the GoogleSignInClient
    lateinit var mGoogleSignInClient: GoogleSignInClient
    // val auth is initialized by lazy
    private val auth by lazy {
        FirebaseAuth.getInstance()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        SavedPreference.EMAIL = SavedPreference.getEmail(this)
//        SavedPreference.USERNAME = SavedPreference.getUsername(this)

        sportCenterViewModel.initData()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient= GoogleSignIn.getClient(this,gso)

        /* Setting the logged user */
        //hardcoded user
        /* Load user's info in both the User object and the shared preferences */
        Log.i("MainActivity, OnCreate", "${SavedPreference.EMAIL}")
        userViewModel.setCurrentUser(SavedPreference.EMAIL)
//        userViewModel.userLiveData.observe(this) {
//            userViewModel.user = it
//            loadUserInfo()
//        }
        userViewModel.userWithSportMasteriesAndNameLiveData.observe(this){
            println("observer got: $it")
            println("Observer")
            userViewModel.user = it.user
            userViewModel.userWithSportMasteriesAndName = it
            loadUserInfo()

        }

        val sharedPreferences = this.getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putLong("UserId", 1).apply()

        /* Getting current user's reservations */
        reservationBrowserViewModel.initUserReservations(SavedPreference.EMAIL)

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
        }
        /* -------------------- */

        /* Invites */
        invitesViewModel.pendingSentInvites.observe(this) {
            userInvitesSent = it
        }

        invitesViewModel.pendingReceivedInvites.observe(this) {
            userInvitesReceived = it
        }

        /* --------------------*/


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

    override fun onBackPressed() {
        val homeFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer) as? HomeFragment
        homeFragment?.let {
            moveTaskToBack(true)
            binding.bottomNavigation.selectedItemId = R.id.home
        } ?: run {
            if (supportFragmentManager.backStackEntryCount > 1) {  // check if there are more than one fragment in the back stack
                supportFragmentManager.popBackStack()
                val lastFragment = supportFragmentManager.fragments.lastOrNull()
                when (lastFragment) {
                    is HomeFragment -> {
                        binding.bottomNavigation.selectedItemId = R.id.home
                    }
                    is ShowUnimplementedFragment -> {
                        binding.bottomNavigation.selectedItemId = R.id.explore
                    }
                    is BrowseReservationsFragment -> {
                        binding.bottomNavigation.selectedItemId = R.id.calendar
                    }
                    is ShowSocialPageFragment -> {
                        binding.bottomNavigation.selectedItemId = R.id.chat
                    }
                    is ShowProfileFragment -> {
                        binding.bottomNavigation.selectedItemId = R.id.profile
                    }
                    else -> {
                        binding.bottomNavigation.selectedItemId = R.id.home
                    }
                }
            } else {
                replaceFragment(HomeFragment())  // load HomeFragment if back stack is empty
                binding.bottomNavigation.selectedItemId = R.id.home
                clearBackStack() // Clear the back stack
            }
        }
    }

    fun clearBackStack() {
        val manager = supportFragmentManager
        if (manager.backStackEntryCount > 0) {
            val first = manager.getBackStackEntryAt(0)
            manager.popBackStack(first.id, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }
    fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()

        if (fragment is HomeFragment) {
            transaction.replace(R.id.fragmentContainer, fragment)
        } else {
            transaction.replace(R.id.fragmentContainer, fragment)
            transaction.addToBackStack(null)
        }

        transaction.commit()
    }


    /*    fun replaceHomeFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.commit()
    }*/
    private fun loadUserInfo() {
        val sharedPreferences = this.getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
//        editor.putLong("UserId", userId)
        Log.i("loadUserInfo", "${userViewModel.user}")
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
