package it.polito.mad.courtreservationapp.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import it.polito.mad.courtreservationapp.db.relationships.CourtWithReservations
import it.polito.mad.courtreservationapp.db.relationships.CourtWithServices
import it.polito.mad.courtreservationapp.db.relationships.ReservationWithServices
import it.polito.mad.courtreservationapp.db.repository.*
import it.polito.mad.courtreservationapp.models.Court
import it.polito.mad.courtreservationapp.models.Reservation
import it.polito.mad.courtreservationapp.models.SportCenter
import it.polito.mad.courtreservationapp.models.User
import it.polito.mad.courtreservationapp.views.reservationManager.CreateReservationActivity
import kotlinx.coroutines.launch
import java.util.*

class CreateReservationViewModel(application: Application): AndroidViewModel(application) {
    private val tag: String = "ReservationFragmentViewModel"
    val reservationRepo: FireReservationRepository = FireReservationRepository(application)
    private val courtRepo: CourtRepository = CourtRepository(application)
    private val sportCenterRepo: FireSportCenterRepository = FireSportCenterRepository(application)
    private val userRepo: FireUserRepository = FireUserRepository(application)

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

    var courtId: Long = 0
    var sportCenterId: Long = 0
    var reservationId: Long = 0
    var userId: Long = 0



    fun initAll(ctx : CreateReservationActivity){
        initCourt(courtId, sportCenterId)
        initUser(userId)

        if(reservationId!=0L){
            reservationRepo.getByIdWithServices(reservationId).observe(ctx){
                reservationDate=it.reservation.reservationDate
                reservationTimeSlots.add(it.reservation.timeSlotId)
                reservationServices=it.services.map { service -> service.serviceId }.toMutableList()
                reservationRequests=it.reservation.request?:""
            }
        }
    }
    private fun initCourt(courtId: Long, centerId: Long){
        sportCenterLiveData = sportCenterRepo.getById(centerId)
        courtReservationsLiveData = courtRepo.getByIdWithReservations(courtId)
        courtServicesLiveData = courtRepo.getByIdWithServices(courtId)

    }
    private fun initUser(userId: Long){
        userLiveData = userRepo.getById(userId)
    }


    fun saveReservation(){
        viewModelScope.launch {
            val reservations: MutableList<ReservationWithServices> = mutableListOf()
            for(timeSlot in reservationTimeSlots){
                val res = Reservation(reservationDate?: Calendar.getInstance().toString(), timeSlot, user.userId, courtWithReservations.court.courtId, reservationRequests, reservationId)
//            reservations.add(ReservationWithServices(res, reservationServices as List<Service>))
                val services = reservationServices.map { id ->
                    courtWithServices.services.first { it.serviceId == id }
                }
                val resWithServices = ReservationWithServices(res, services)
                reservations.add(resWithServices)
            }

            for(reservation in reservations){
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

    fun getServicesInfo(): String{
        var servStr: String = reservationServices.fold("") { acc, i ->
            if (acc.isNotEmpty()) {
                "$acc, ${courtWithServices.services.first { service -> service.serviceId == i }.description}"
            } else {
                courtWithServices.services.first { service -> service.serviceId == i }.description
            }
        }
        if (servStr.isNotEmpty()) {
            servStr = "I'd like to request $servStr.\n"
        }
        if (reservationRequests.isNotEmpty()) {
            servStr = "${servStr}Other requests: $reservationRequests"
        }
        return servStr
    }


}