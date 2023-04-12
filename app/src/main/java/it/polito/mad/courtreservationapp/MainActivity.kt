package it.polito.mad.courtreservationapp
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import it.polito.mad.courtreservationapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(Profile())
        val bottomNav=findViewById<BottomNavigationView>(R.id.bottom_navigation)
        binding.bottomNavigation.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.home -> Log.i("DBG", "Home button pressed")
                R.id.explore -> Log.i("DBG", "Explore button pressed")
                R.id.calendar -> Log.i("DBG", "Calendar button pressed")
                R.id.chat -> Log.i("DBG", "Calendar button pressed")
                R.id.profile -> {
                    replaceFragment(Profile())
                }
                else -> Log.i("DBG", "Invalid")
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager=supportFragmentManager
        val fragmentTransaction=fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer,fragment)
        fragmentTransaction.commit()
    }


}
