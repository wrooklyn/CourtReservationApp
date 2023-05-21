package it.polito.mad.courtreservationapp.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.db.relationships.*
import it.polito.mad.courtreservationapp.db.repository.FireReservationRepository
import it.polito.mad.courtreservationapp.models.Reservation
import it.polito.mad.courtreservationapp.models.SportCenter
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ReservationBrowserViewModel(application: Application): AndroidViewModel(application) {
    private val reservationRepo: FireReservationRepository = FireReservationRepository(application)

    lateinit var sportCenter: LiveData<SportCenter>

    var userReservations: MutableLiveData<List<Reservation>> = MutableLiveData()
    var userReservationsLocations: MutableLiveData<List<ReservationWithSportCenter>> = MutableLiveData()
    var userReservationsServices: MutableLiveData<List<ReservationWithServices>> = MutableLiveData()
    var userReservationsReviews: MutableLiveData<List<ReservationWithReview>> = MutableLiveData()

    val servicesIcons: Map<Long, Int> = mapOf(
        Pair(0, R.drawable.safety_shower),
        Pair(1, R.drawable.equipment),
        Pair(2, R.drawable.coach),
        Pair(3, R.drawable.refreshment)
    )
    fun initUserReservations(userEmail: String) {
        runBlocking {
            launch {
                userReservations.postValue(reservationRepo.getReservationsByUser(userEmail))
                userReservationsLocations.postValue(reservationRepo.getReservationLocationsByUserId(userEmail))
                userReservationsServices.postValue(reservationRepo.getReservationServicesByUserId(userEmail))
                userReservationsReviews.postValue(reservationRepo.getReservationsReviewsByUserId(userEmail))
            }
        }
    }

    fun deleteReservation(reservationId: Long) {
        viewModelScope.launch {
            reservationRepo.deleteReservationById(reservationId)
        }
    }
}