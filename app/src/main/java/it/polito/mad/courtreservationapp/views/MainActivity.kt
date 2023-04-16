package it.polito.mad.courtreservationapp.views
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.databinding.ActivityMainBinding
import it.polito.mad.courtreservationapp.views.reservationManager.CreateReservationActivity

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(ShowProfileFragment())
        binding.bottomNavigation.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.home -> Log.i("DBG", "Home button pressed")
                R.id.explore -> Log.i("DBG", "Explore button pressed")
                R.id.calendar -> Log.i("DBG", "Calendar button pressed")
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
