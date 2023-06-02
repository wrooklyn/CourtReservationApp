package it.polito.mad.courtreservationapp.views.reservationManager

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.databinding.ActivityCreateReservationBinding
import it.polito.mad.courtreservationapp.db.relationships.ReservationWithReview
import it.polito.mad.courtreservationapp.db.relationships.ReservationWithServices
import it.polito.mad.courtreservationapp.db.relationships.ReservationWithSportCenter
import it.polito.mad.courtreservationapp.models.Reservation
import it.polito.mad.courtreservationapp.view_model.CreateReservationViewModel
import it.polito.mad.courtreservationapp.view_model.ReservationBrowserViewModel
import it.polito.mad.courtreservationapp.views.homeManager.HomeFragment
import it.polito.mad.courtreservationapp.views.login.SavedPreference
import java.util.*

class CreateReservationActivity : AppCompatActivity() {

    private var pageNumber: Int = 0

    //ViewModel
    val viewModel: CreateReservationViewModel by viewModels()
    val RBViewModel: ReservationBrowserViewModel by viewModels()


    lateinit var binding: ActivityCreateReservationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("Create Res Activity", "Starting activity")

        binding = ActivityCreateReservationBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //TODO: get data from main activity
        viewModel.courtId = intent.getStringExtra("courtId")  ?: ""
        viewModel.reservationId = intent.getStringExtra("reservationId") ?: ""
        viewModel.sportCenterId = intent.getStringExtra("sportCenterId") ?: ""
        viewModel.rating = intent.getLongExtra("rating", 0L)
        viewModel.reviews = intent.getStringExtra("reviews") ?: "0 reviews"

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
//            this.findViewById<RecyclerView>(R.id.recyclerView).adapter!!.notifyDataSetChanged()
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
        // TODO: fix this ungodly mess
        Log.i("CreateReservationActivity","commitReservation start")
        val reservations: MutableList<ReservationWithServices> = mutableListOf()
        for(timeSlot in viewModel.reservationTimeSlots){
            val res = Reservation(viewModel.reservationDate?: Calendar.getInstance().toString(), timeSlot, SavedPreference.EMAIL, viewModel.courtWithReservations.court.courtId, viewModel.reservationRequests, viewModel.reservationId)
//            reservations.add(ReservationWithServices(res, reservationServices as List<Service>))
            val services = viewModel.reservationServices
            val resWithServices = ReservationWithServices(res, services)
            Log.i("CreateReservationActivity", "Saving again(?): $resWithServices")
            reservations.add(resWithServices)
        }

        for(reservation in reservations){
            val userReservation = reservation.reservation
            viewModel.courtRepo.getByIdWithSportCenter(viewModel.courtId, viewModel.sportCenterId) {result ->
                val reservationLocation = ReservationWithSportCenter(
                    reservation.reservation,
                    result!!
                )
                val reservationWithReview = ReservationWithReview(reservation.reservation, null)
                RBViewModel.addReservation(
                    userReservation,
                    reservationLocation,
                    reservation,
                    reservationWithReview
                )
            }
        }
        setResult(Activity.RESULT_OK)
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