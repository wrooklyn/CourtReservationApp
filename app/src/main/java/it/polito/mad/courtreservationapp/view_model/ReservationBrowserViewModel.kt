package it.polito.mad.courtreservationapp.view_model

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import it.polito.mad.courtreservationapp.db.relationships.*
import it.polito.mad.courtreservationapp.db.repository.CourtRepository
import it.polito.mad.courtreservationapp.db.repository.ReservationRepository
import it.polito.mad.courtreservationapp.db.repository.SportCenterRepository
import it.polito.mad.courtreservationapp.models.Reservation
import it.polito.mad.courtreservationapp.models.SportCenter
import it.polito.mad.courtreservationapp.models.User

class ReservationBrowserViewModel(application: Application): AndroidViewModel(application) {
    private val reservationRepo: ReservationRepository = ReservationRepository(application)

    lateinit var sportCenter: LiveData<SportCenter>

    lateinit var userReservations: LiveData<List<Reservation>>
    lateinit var userReservationsLocations: LiveData<List<ReservationWithSportCenter>>

    fun initUserReservations(userId: Long) {
        userReservations = reservationRepo.getReservationsByUser(userId)
        userReservationsLocations = reservationRepo.getReservationLocationsByUserId(userId)
    }
}