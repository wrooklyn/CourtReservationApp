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
import it.polito.mad.courtreservationapp.view_model.ReservationBrowserViewModel
import it.polito.mad.courtreservationapp.views.homeManager.HomeFragment
import it.polito.mad.courtreservationapp.views.reservationManager.BrowseReservationsFragment
import it.polito.mad.courtreservationapp.views.reservationManager.CreateReservationActivity
import it.polito.mad.courtreservationapp.views.reservationManager.ReservationBrowserActivity

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: ReservationBrowserViewModel
    lateinit var userReservations: List<Reservation>
    lateinit var userReservationsLocations: List<ReservationWithSportCenter>
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[ReservationBrowserViewModel::class.java]
        viewModel.initUserReservations(1) // TODO: use actual user ID

        viewModel.userReservations.observe(this){
            userReservations = it
        }

        viewModel.userReservationsLocations.observe(this){
            userReservationsLocations = it
            Log.i("DBG", "Locations")
            Log.i("DBG", userReservationsLocations.toString())
        }
        replaceFragment(HomeFragment())
        binding.bottomNavigation.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.home -> {
                    replaceFragment(HomeFragment())
                }
                R.id.explore -> Log.i("DBG", "Explore button pressed")
//                R.id.calendar -> {
//                    val reservationBrowserIntent = Intent(this, ReservationBrowserActivity::class.java)
//                    startActivity(reservationBrowserIntent)
//                }
                R.id.calendar -> {
                    replaceFragment(BrowseReservationsFragment())
                }
                R.id.chat -> testLaunchGabry()
                R.id.profile -> {
                    replaceFragment(ShowProfileFragment())
                }
                else -> Log.i("DBG", "Invalid")
            }
            true
        }
    }

    private fun testLaunchGabry(){
        val createReservationIntent:Intent = Intent(this, CreateReservationActivity::class.java)
        startActivity(createReservationIntent)
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager=supportFragmentManager
        val fragmentTransaction=fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer,fragment)
        fragmentTransaction.commit()
    }


}
