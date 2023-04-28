package it.polito.mad.courtreservationapp.view_model

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.db.relationships.*
import it.polito.mad.courtreservationapp.db.repository.CourtRepository
import it.polito.mad.courtreservationapp.db.repository.ReservationRepository
import it.polito.mad.courtreservationapp.db.repository.SportCenterRepository
import it.polito.mad.courtreservationapp.models.Reservation
import it.polito.mad.courtreservationapp.models.Service
import it.polito.mad.courtreservationapp.models.SportCenter
import it.polito.mad.courtreservationapp.models.User
import kotlinx.coroutines.launch

class ReservationBrowserViewModel(application: Application): AndroidViewModel(application) {
    private val reservationRepo: ReservationRepository = ReservationRepository(application)

    lateinit var sportCenter: LiveData<SportCenter>

    lateinit var userReservations: LiveData<List<Reservation>>
    lateinit var userReservationsLocations: LiveData<List<ReservationWithSportCenter>>
    lateinit var userReservationsServices: LiveData<List<ReservationWithServices>>

    val servicesIcons: Map<Long, Int> = mapOf(
        Pair(0, R.drawable.safety_shower),
        Pair(1, R.drawable.equipment),
        Pair(2, R.drawable.coach),
        Pair(3, R.drawable.refreshment)
    )
    fun initUserReservations(userId: Long) {
        userReservations = reservationRepo.getReservationsByUser(userId)
        userReservationsLocations = reservationRepo.getReservationLocationsByUserId(userId)
        userReservationsServices = reservationRepo.getReservationServicesByUserId(userId)
    }

    fun deleteReservation(reservationId: Long) {
        viewModelScope.launch {
            reservationRepo.deleteReservationById(reservationId)
        }
    }
}