package it.polito.mad.courtreservationapp.views

//import com.google.firebase.firestore.ListenerRegistration
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
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
    val sportMasteryViewModel: SportMasteryViewModel by viewModels() //done
    val ratingViewModel: LeaveRatingViewModel by viewModels() //done
    val invitesViewModel: InvitesManagerViewModel by viewModels()

    val registerForAchievementActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        println("${it}")
        Log.d("registerActivity", "${it}")
        if (it.resultCode == Activity.RESULT_OK){
            val data: Intent? = it.data
            val email= data?.getStringExtra("email")
            val sport=data?.getStringExtra("sport")
            val level=data?.getStringExtra("level")?.toInt()
            val achievement= data?.getStringExtra("achievement")
            Log.d("provaprova", email.toString()+"ciao")
            Log.d("provaprova", sport.toString()+"ciao")
            Log.d("provaprova", level.toString()+"ciao")
            Log.d("provaprova", achievement.toString()+"ciao")

            val mastery = SportMasteryWithName(SportMastery(0, "",level!!, achievement, 0), Sport(sport!!, 0))

            //userViewModel.refreshUser(this)
            userViewModel.updateMastery(mastery)
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
