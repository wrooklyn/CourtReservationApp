package it.polito.mad.courtreservationapp.views.reservationManager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.databinding.ActivityReservationBrowserBinding
import it.polito.mad.courtreservationapp.db.relationships.*
import it.polito.mad.courtreservationapp.models.*
import it.polito.mad.courtreservationapp.view_model.ReservationBrowserViewModel

class ReservationBrowserActivity : AppCompatActivity() {

    lateinit var viewModel: ReservationBrowserViewModel
    lateinit var userReservations: List<Reservation>

    lateinit var binding: ActivityReservationBrowserBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReservationBrowserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(BrowseReservationsFragment())
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager=supportFragmentManager
        val fragmentTransaction=fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.reservBrowserFragmentContainer,fragment)
        fragmentTransaction.commit()
    }
}