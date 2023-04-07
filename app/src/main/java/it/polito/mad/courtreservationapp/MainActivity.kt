package it.polito.mad.courtreservationapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import it.polito.mad.courtreservationapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.profile_button ->{
                    val myIntent = Intent(this, ShowProfileActivity::class.java)
                    startActivity(myIntent)
                }
                R.id.home_button -> Log.i("DBG", "Pressed home button")
                R.id.explore_button -> Log.i("DBG", "Pressed explore button")
                R.id.calendar_button -> Log.i("DBG", "Pressed calendar button")
                else -> Log.i("DBG", "Invalid")
            }
            true
        }
    }
}