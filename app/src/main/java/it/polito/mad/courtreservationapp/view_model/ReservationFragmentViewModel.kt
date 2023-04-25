package it.polito.mad.courtreservationapp.view_model

import android.app.Application
import android.app.Service
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
import it.polito.mad.courtreservationapp.models.Court
import it.polito.mad.courtreservationapp.models.Reservation
import it.polito.mad.courtreservationapp.models.SportCenter
import it.polito.mad.courtreservationapp.models.User
import kotlinx.coroutines.launch
import java.util.*

class ReservationFragmentViewModel(application: Application): AndroidViewModel(application) {
    private val tag: String = "ReservationFragmentViewModel"
    private val reservationRepo: ReservationRepository = ReservationRepository(application)
    private val courtRepo: CourtRepository = CourtRepository(application)
    private val sportCenterRepo: SportCenterRepository = SportCenterRepository(application)
    private val userRepo: UserRepository = UserRepository(application)

    lateinit var sportCenterLiveData: LiveData<SportCenter>
    lateinit var courtReservationsLiveData: LiveData<CourtWithReservations>
    lateinit var courtServicesLiveData: LiveData<CourtWithServices>
    lateinit var userLiveData: LiveData<User>

    lateinit var sportCenter: SportCenter
    lateinit var court: Court
    lateinit var courtWithReservations: CourtWithReservations
    lateinit var courtWithServices: CourtWithServices
    lateinit var user: User

    private var _reservationsByDateMap: Map<String, List<Long>> = mutableMapOf()
    val reservationsByDateMap: Map<String, List<Long>>
        get() = _reservationsByDateMap

    //new reservation data
    var reservationDate : String? = null
    var reservationTimeSlots : MutableList<Long> = mutableListOf()
    var reservationServices : MutableList<Long> = mutableListOf()
    var reservationRequests : String = ""



    fun initAll(courtId: Long, centerId: Long, userId: Long){
        initCourt(courtId, centerId)
        initUser(userId)
    }
    private fun initCourt(courtId: Long, centerId: Long){
        Log.i(tag, "InitCourt")
        sportCenterLiveData = sportCenterRepo.getById(centerId)
        courtReservationsLiveData = courtRepo.getByIdWithReservations(courtId)
        courtServicesLiveData = courtRepo.getByIdWithServices(courtId)

        Log.i(tag, "SportCenter: $sportCenterLiveData")
        Log.i(tag, "Reservations: $courtReservationsLiveData")
        Log.i(tag, "Services: $courtServicesLiveData")
    }
    private fun initUser(userId: Long){
        Log.i(tag, "InitUser")
        userLiveData = userRepo.getById(userId)
    }


    fun saveReservation(){
        viewModelScope.launch {
            val reservations: MutableList<ReservationWithServices> = mutableListOf()
            for(timeSlot in reservationTimeSlots){
                val res = Reservation(reservationDate?: Calendar.getInstance().toString(), timeSlot, user.userId, courtWithReservations.court.courtId, reservationRequests)
//            reservations.add(ReservationWithServices(res, reservationServices as List<Service>))
                val services = reservationServices.map { id ->
                    courtWithServices.services.first { it.serviceId == id }
                }
                val resWithServices = ReservationWithServices(res, services)
                reservations.add(resWithServices)
            }

            for(reservation in reservations){
                Log.i(tag, "Inserting: $reservation")
                reservationRepo.insertReservationWithServices(reservation)
            }
        }
    }

    private fun initReservationByDateString(reservations: CourtWithReservations) {
        val group = reservations.reservations.groupBy { it.reservationDate }
        _reservationsByDateMap = group.mapValues { entry -> entry.value.map { it.timeSlotId } }
    }

    fun liveDataToData(data: CourtWithReservations){
        court = data.court
        courtWithReservations = data
        initReservationByDateString(data)
    }

    fun liveDataToData(data: User){
        user = data
    }

    fun liveDataToData(data: SportCenter){
        sportCenter = data
    }

    fun liveDataToData(data: CourtWithServices){
        courtWithServices = data
    }


}