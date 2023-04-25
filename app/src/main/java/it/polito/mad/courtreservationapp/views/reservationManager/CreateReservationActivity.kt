package it.polito.mad.courtreservationapp.views.reservationManager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.databinding.ActivityCreateReservationBinding
import it.polito.mad.courtreservationapp.db.relationships.CourtWithReservations
import it.polito.mad.courtreservationapp.db.relationships.CourtWithServices
import it.polito.mad.courtreservationapp.models.*
import it.polito.mad.courtreservationapp.view_model.ReservationFragmentViewModel

class CreateReservationActivity : AppCompatActivity() {

    private var pageNumber: Int = 0
    var courtId : Long = -1;
    var reservationId : Long = 0;
    //ViewModel
    val viewModel: ReservationFragmentViewModel by viewModels()


    lateinit var binding: ActivityCreateReservationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateReservationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.i("DBG", "CourtId: ${intent.getLongExtra("courtId", -1)}")
        Log.i("DBG", "ReservationId: ${intent.getLongExtra("reservationId", -1)}")

        //TODO: get data from main activity
        courtId = intent.getLongExtra("courtId", -1)
        reservationId = intent.getLongExtra("reservationId", 0)
        if(courtId.equals(-1)) throw Exception("Invalid parameters")

        val courtId = savedInstanceState?.getLong("courtId") ?: courtId
        val userId = savedInstanceState?.getLong("userId") ?: 1

        viewModel.initAll(courtId, 1, userId)

        setObservers()

        replaceFragment(ShowCalendarFragment())
    }

    private fun setObservers() {
        viewModel.sportCenterLiveData.observe(this) {
            viewModel.liveDataToData(it)
        }

        viewModel.userLiveData.observe(this) {
            viewModel.liveDataToData(it)
        }

        viewModel.courtReservationsLiveData.observe(this) {
            viewModel.liveDataToData(it)
        }

        viewModel.courtServicesLiveData.observe(this) {
            viewModel.liveDataToData(it)
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.commit()
    }

    fun commitReservation() {
        viewModel.saveReservation()
        finish()
    }

    fun setSchedule() {
        //updates myReservation with the first fragment data
    }

    fun setServices() {
        //updates myReservation with the second fragment data
    }

    fun ggNEXT() {
        //take current index and handle navigation
        if (pageNumber + 1 > 3) return;
        pageNumber++
        when (pageNumber) {
            1 -> replaceFragment(ShowSelectServicesFragment())
            2 -> replaceFragment(ShowSummaryFragment())
            3 -> commitReservation()
        }
    }

    fun ggBack() {
        //take current index and and handle navigation
        if (pageNumber - 1 < 0) return;
        pageNumber--
        when (pageNumber) {
            0 -> replaceFragment(ShowCalendarFragment())
            1 -> replaceFragment(ShowSelectServicesFragment())
            2 -> replaceFragment(ShowSummaryFragment())
            //3 -> commitReservation()
        }
    }
}