package it.polito.mad.courtreservationapp.view_model

import FireReservationRepository
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.db.relationships.ReservationWithReview
import it.polito.mad.courtreservationapp.db.relationships.ReservationWithServices
import it.polito.mad.courtreservationapp.db.relationships.ReservationWithSportCenter
import it.polito.mad.courtreservationapp.models.Reservation
import it.polito.mad.courtreservationapp.models.SportCenter
import kotlinx.coroutines.launch

class ReservationBrowserViewModel(application: Application): AndroidViewModel(application) {
    private val reservationRepo: FireReservationRepository = FireReservationRepository(application, null)

    lateinit var sportCenter: LiveData<SportCenter>

    private val _userReservations: MutableLiveData<List<Reservation>> = MutableLiveData()
    val userReservations: LiveData<List<Reservation>> = _userReservations

    private val _userReservationsLocations: MutableLiveData<List<ReservationWithSportCenter>> = MutableLiveData()
    val userReservationsLocations: LiveData<List<ReservationWithSportCenter>> = _userReservationsLocations

    private val _userReservationsServices: MutableLiveData<List<ReservationWithServices>> = MutableLiveData()
    val userReservationsServices: LiveData<List<ReservationWithServices>> = _userReservationsServices

    private val _userReservationsReviews: MutableLiveData<List<ReservationWithReview>> = MutableLiveData()
    val userReservationsReviews: LiveData<List<ReservationWithReview>> = _userReservationsReviews

    val servicesIcons: Map<Long, Int> = mapOf(
        Pair(0, R.drawable.safety_shower),
        Pair(1, R.drawable.equipment),
        Pair(2, R.drawable.coach),
        Pair(3, R.drawable.refreshment)
    )
    fun initUserReservations(userEmail: String) {
        viewModelScope.launch{
            try {
                val userReserv = reservationRepo.getReservationsByUser(userEmail)
                _userReservations.postValue(userReserv)

                val userReservLocations = reservationRepo.getReservationLocationsByUserId(userEmail)
                _userReservationsLocations.postValue(userReservLocations)

                val userReservServices = reservationRepo.getReservationServicesByUserId(userEmail)
                _userReservationsServices.postValue(userReservServices)

                val userReservReviews = reservationRepo.getReservationsReviewsByUserId(userEmail)
                _userReservationsReviews.postValue(userReservReviews)
            } catch(e: Exception) {
                println("Error initializing reservations: $e")
            }
        }
    }

    fun deleteReservation(reservationId: String, userEmail: String, sportCenterId: String, courtId: String) {
        viewModelScope.launch {
            reservationRepo.deleteReservationById(reservationId, userEmail, sportCenterId, courtId)
            val updatedReservations = userReservations.value?.toMutableList()
            updatedReservations?.removeAll { it.reservationId == reservationId }
            _userReservations.postValue(updatedReservations!!)

            val updatedReservationsLocations = userReservationsLocations.value?.toMutableList()
            updatedReservationsLocations?.removeAll { it.reservation.reservationId == reservationId }
            _userReservationsLocations.postValue(updatedReservationsLocations!!)

            val updatedReservationsServices = userReservationsServices.value?.toMutableList()
            updatedReservationsServices?.removeAll { it.reservation.reservationId == reservationId }
            _userReservationsServices.postValue(updatedReservationsServices!!)

            val updatedReservationsReviews = userReservationsReviews.value?.toMutableList()
            updatedReservationsReviews?.removeAll { it.reservation.reservationId == reservationId }
            _userReservationsReviews.postValue(updatedReservationsReviews!!)
        }
    }

    fun addReservation(reservation: Reservation, reservationLocation: ReservationWithSportCenter, reservationServices: ReservationWithServices, reservationReview: ReservationWithReview) {
        val updatedReservations = userReservations.value?.toMutableList() ?: mutableListOf()
        updatedReservations.add(reservation)
        _userReservations.postValue(updatedReservations)

        val updatedReservationsLocation = userReservationsLocations.value?.toMutableList() ?: mutableListOf()
        updatedReservationsLocation.add(reservationLocation)
        _userReservationsLocations.postValue(updatedReservationsLocation)

        val updatedReservationsServices = userReservationsServices.value?.toMutableList() ?: mutableListOf()
        updatedReservationsServices.add(reservationServices)
        _userReservationsServices.postValue(updatedReservationsServices)

        val updatedReservationsReview = userReservationsReviews.value?.toMutableList() ?: mutableListOf()
        updatedReservationsReview.add(reservationReview)
        _userReservationsReviews.postValue(updatedReservationsReview)
    }
}