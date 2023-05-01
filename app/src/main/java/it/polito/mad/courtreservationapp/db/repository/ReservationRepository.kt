package it.polito.mad.courtreservationapp.db.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import it.polito.mad.courtreservationapp.db.AppDatabase
import it.polito.mad.courtreservationapp.db.crossref.ReservationServiceCrossRef
import it.polito.mad.courtreservationapp.db.relationships.ReservationWithServices
import it.polito.mad.courtreservationapp.db.relationships.ReservationWithSportCenter
import it.polito.mad.courtreservationapp.db.relationships.UserWithReservations
import it.polito.mad.courtreservationapp.models.Reservation

class ReservationRepository(val application: Application) {
    private val db: AppDatabase = AppDatabase.getDatabase(application)
    private val reservationDao = db.reservationDao()
    private val reservationAndServiceDao = db.reservationAndServiceDao()

    suspend fun insertReservationWithServices(reservationWithServices: ReservationWithServices){
        //save reservation
        val reservationId = reservationDao.save(reservationWithServices.reservation)
        reservationWithServices.reservation.reservationId = reservationId
        //save service requested
        for(service in reservationWithServices.services){
            reservationAndServiceDao.save(ReservationServiceCrossRef(reservationId, service.serviceId))
        }
    }

    suspend fun deleteReservations(reservations: List<Reservation>){
        for(reservation in reservations){
            reservationDao.delete(reservation)
        }
    }

    suspend fun deleteReservationById(reservationId: Long) {
        reservationDao.deleteById(reservationId)
    }

    fun getAll(): LiveData<List<Reservation>>{
        return reservationDao.getAll()
    }

    fun getById(reservationId: Long): LiveData<Reservation>{
        return reservationDao.getById(reservationId)
    }

    fun getAllWithServices(): LiveData<List<ReservationWithServices>>{
        return reservationDao.getAllWithServices()
    }
    fun getByIdWithServices(reservationId: Long): LiveData<ReservationWithServices>{
        return reservationDao.getByIdWithServices(reservationId)
    }


    fun getReservationsByUser(userId: Long): LiveData<List<Reservation>> {
        return reservationDao.getByUser(userId)
    }

    fun getReservationLocationsByUserId(userId: Long): LiveData<List<ReservationWithSportCenter>> {
        return reservationDao.getLocationsByUserId(userId)
    }

    fun getReservationServicesByUserId(userId: Long): LiveData<List<ReservationWithServices>> {
        return reservationDao.getServicesByUserId(userId)
    }
}
