package it.polito.mad.courtreservationapp.view_model

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import it.polito.mad.courtreservationapp.db.relationships.CourtWithReservations
import it.polito.mad.courtreservationapp.db.relationships.CourtWithServices
import it.polito.mad.courtreservationapp.db.relationships.ReservationWithServices
import it.polito.mad.courtreservationapp.db.repository.CourtRepository
import it.polito.mad.courtreservationapp.db.repository.ReservationRepository
import it.polito.mad.courtreservationapp.db.repository.SportCenterRepository
import it.polito.mad.courtreservationapp.db.repository.UserRepository
import it.polito.mad.courtreservationapp.models.SportCenter
import it.polito.mad.courtreservationapp.models.User
import kotlinx.coroutines.launch

class ReservationFragmentViewModel(application: Application): AndroidViewModel(application) {
    private val reservationRepo: ReservationRepository = ReservationRepository(application)
    private val courtRepo: CourtRepository = CourtRepository(application)
    private val sportCenterRepo: SportCenterRepository = SportCenterRepository(application)
    private val userRepo: UserRepository = UserRepository(application)

    lateinit var sportCenter: LiveData<SportCenter>
    lateinit var courtReservations: LiveData<CourtWithReservations>
    lateinit var courtServices: LiveData<CourtWithServices>
    lateinit var user: LiveData<User>

    fun initAll(courtId: Long, centerId: Long, userId: Long){
        initCourt(courtId, centerId)
        initUser(userId)
    }
    private fun initCourt(courtId: Long, centerId: Long){
        sportCenter = sportCenterRepo.getById(centerId)
        courtReservations = courtRepo.getByIdWithReservations(courtId)
        courtServices = courtRepo.getByIdWithServices(courtId)
    }
    private fun initUser(userId: Long){
        user = userRepo.getById(userId)
    }

    fun saveReservation(reservationsWithServices: List<ReservationWithServices>){
        viewModelScope.launch {
            for(reservation in reservationsWithServices){
                Log.i("VM", "Inserting: $reservation")
                reservationRepo.insertReservationWithServices(reservation)
            }
        }
    }

    fun getReservationByDateString(reservations: CourtWithReservations): Map<String, List<Long>> {
        val group = reservations.reservations.groupBy { it.reservationDate }
        return group.mapValues { entry -> entry.value.map { it.timeSlotId } }
    }

}