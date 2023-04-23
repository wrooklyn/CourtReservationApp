package it.polito.mad.courtreservationapp.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import it.polito.mad.courtreservationapp.db.relationships.CourtWithReservations
import it.polito.mad.courtreservationapp.db.relationships.CourtWithServices
import it.polito.mad.courtreservationapp.db.relationships.ReservationWithServices
import it.polito.mad.courtreservationapp.db.repository.CourtRepository
import it.polito.mad.courtreservationapp.db.repository.ReservationRepository
import it.polito.mad.courtreservationapp.db.repository.SportCenterRepository
import it.polito.mad.courtreservationapp.models.SportCenter
import it.polito.mad.utils.getOrAwaitValue

class ReservationFragmentViewModel(application: Application): AndroidViewModel(application) {
    private val reservationRepo: ReservationRepository = ReservationRepository(application)
    private val courtRepo: CourtRepository = CourtRepository(application)
    private val sportCenterRepo: SportCenterRepository = SportCenterRepository(application)

    private lateinit var sportCenter : LiveData<SportCenter>
    private lateinit var courtWithReservations: LiveData<CourtWithReservations>
    private lateinit var courtWithServices: LiveData<CourtWithServices>

    //esempio di inserimento di prenotazione
    suspend fun insertReservations(reservations: List<ReservationWithServices>){
        for(reservation in reservations){
            reservationRepo.insertReservationWithServices(reservation)
        }
    }

    //
    suspend fun getCourtWithReservations(id: Long){
        courtWithReservations = courtRepo.getByIdWithReservations(id)
        courtWithServices = courtRepo.getByIdWithServices(id)
        sportCenter = sportCenterRepo.getById(courtWithReservations.getOrAwaitValue().court.courtCenterId)
    }



}