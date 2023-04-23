package it.polito.mad.courtreservationapp.db.repository

import android.app.Application
import androidx.lifecycle.LiveData
import it.polito.mad.courtreservationapp.db.AppDatabase
import it.polito.mad.courtreservationapp.db.crossref.ReservationServiceCrossRef
import it.polito.mad.courtreservationapp.db.relationships.CourtWithReservations
import it.polito.mad.courtreservationapp.db.relationships.ReservationWithServices
import it.polito.mad.courtreservationapp.models.Reservation

class ReservationRepository(private val application: Application) {
    private val db: AppDatabase = AppDatabase.getDatabase(application)
    private val reservationDao = db.reservationDao()
    private val courtDao = db.courtDao()
    private val serviceDao = db.serviceDao()
    private val reservationAndServiceDao = db.reservationAndServiceDao()

    val allReservations = reservationDao.getAll()

    suspend fun insertReservationWithServices(reservationWithServices: ReservationWithServices){
        reservationDao.save(reservationWithServices.reservation)
        val reservationId = reservationWithServices.reservation.reservationId
        for(service in reservationWithServices.services){
            reservationAndServiceDao.save(ReservationServiceCrossRef(reservationId, service.serviceId))
        }
    }

    suspend fun deleteReservations(reservations: List<Reservation>){
        for(reservation in reservations){
            reservationDao.delete(reservation)
        }
    }
}