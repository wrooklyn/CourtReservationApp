package it.polito.mad.courtreservationapp.views.reservationManager

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.databinding.ActivityCreateReservationBinding
import it.polito.mad.courtreservationapp.view_model.CreateReservationViewModel

class CreateReservationActivity : AppCompatActivity() {

    private var pageNumber: Int = 0

    //ViewModel
    val viewModel: CreateReservationViewModel by viewModels()


    lateinit var binding: ActivityCreateReservationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateReservationBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //TODO: get data from main activity
        viewModel.courtId = intent.getStringExtra("courtId")  ?: ""
        viewModel.reservationId = intent.getStringExtra("reservationId") ?: ""
        viewModel.sportCenterId = intent.getStringExtra("sportCenterId") ?: ""


        if(viewModel.courtId.isNullOrEmpty()) throw Exception("Invalid parameters")

        //hardcoded user

        viewModel.initAll(this)

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