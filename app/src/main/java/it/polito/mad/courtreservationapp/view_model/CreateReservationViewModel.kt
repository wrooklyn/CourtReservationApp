package it.polito.mad.courtreservationapp.view_model

import FireReservationRepository
import it.polito.mad.courtreservationapp.db.remoteRepository.FireSportCenterRepository
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import it.polito.mad.courtreservationapp.db.relationships.*
import it.polito.mad.courtreservationapp.db.repository.*
import it.polito.mad.courtreservationapp.models.*
import it.polito.mad.courtreservationapp.views.login.SavedPreference
import it.polito.mad.courtreservationapp.views.reservationManager.CreateReservationActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*

class CreateReservationViewModel(application: Application): AndroidViewModel(application) {
    private val tag: String = "ReservationFragmentViewModel"
    val reservationRepo: FireReservationRepository = FireReservationRepository(application, null)
    val courtRepo: FireCourtRepository = FireCourtRepository(application)
    private val sportCenterRepo: FireSportCenterRepository = FireSportCenterRepository(application)
    private val userRepo: FireUserRepository = FireUserRepository(application)

    var sportCenterLiveData: MutableLiveData<SportCenter> = MutableLiveData()
    var courtReservationsLiveData: MutableLiveData<CourtWithReservations> = MutableLiveData()
    var courtServicesLiveData: MutableLiveData<CourtWithServices> = MutableLiveData()
    var userLiveData: MutableLiveData<User> = MutableLiveData()

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
//    var reservationServices : MutableList<Long> = mutableListOf()
    var reservationRequests : String = ""
    var reservationServices: MutableList<Service> = mutableListOf()

    var courtId: String = ""
    var sportCenterId: String = ""
    var reservationId: String = ""
    var userId: String = SavedPreference.EMAIL

    var rating: Double = 0.0
    var reviews: String = "0 reviews"
//    var email: String = SavedPreference.EMAIL


    fun initAll(ctx : CreateReservationActivity){
        Log.i("CreateReservationVM", "using: $courtId, $sportCenterId, ${SavedPreference.EMAIL}")
        initCourt(courtId, sportCenterId)
        initUser(SavedPreference.EMAIL)

        if(reservationId.isNotEmpty()){
            reservationRepo.getByIdWithServices(reservationId).observe(ctx){
                reservationDate=it.reservation.reservationDate
                reservationTimeSlots.add(it.reservation.timeSlotId)
                reservationServices=it.services.map { service -> service }.toMutableList()
                reservationRequests=it.reservation.request?:""
            }
        }
    }
    private fun initCourt(courtId: String, centerId: String){
        runBlocking {
            launch {
                sportCenterLiveData.postValue(sportCenterRepo.getById(centerId))
                courtReservationsLiveData = courtRepo.observeCourtWithReservations(centerId, courtId)
                //courtReservationsLiveData.postValue(courtRepo.getByIdWithReservations(centerId, courtId))
                courtServicesLiveData.postValue(courtRepo.getByIdWithServices(centerId, courtId))
            }
        }

    }
    private fun initUser(email: String){
        runBlocking(Dispatchers.Default) {
            launch {
                Log.i("initUser", "$email")
                val res = userRepo.getUserWithMasteries(email)
                userLiveData.postValue(res.user)
                println("updated: hehe ${res}")
            }
        }

    }


    fun saveReservation(){
        viewModelScope.launch {
            val reservations: MutableList<ReservationWithServices> = mutableListOf()
            Log.i("CreateReservationVm", "time slots: $reservationTimeSlots")
            val today = Calendar.getInstance().toString()
            for(timeSlot in reservationTimeSlots){
                val res = Reservation(reservationDate?: today, timeSlot, SavedPreference.EMAIL, courtWithReservations.court.courtId, reservationRequests, reservationId)
//            reservations.add(ReservationWithServices(res, reservationServices as List<Service>))
                val services = reservationServices
                val resWithServices = ReservationWithServices(res, services)
                Log.i("CreateReservationVm", "Saving: $resWithServices")
                reservations.add(resWithServices)
            }

            for(reservation in reservations){
                reservationRepo.insertReservationWithServices(reservation, sportCenterId)
            }
            Log.i("CreateReservationVm", "Finished saveReservation")
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

//    fun getServicesInfo(): String{
//        var servStr: String = reservationServices.fold("") { acc, i ->
//            if (acc.isNotEmpty()) {
//                "$acc, ${courtWithServices.services.first { service -> service.serviceId == i }.description}"
//            } else {
//                courtWithServices.services.first { service -> service.serviceId == i }.description
//            }
//        }
//        if (servStr.isNotEmpty()) {
//            servStr = "I'd like to request $servStr.\n"
//        }
//        if (reservationRequests.isNotEmpty()) {
//            servStr = "${servStr}Other requests: $reservationRequests"
//        }
//        return servStr
//    }

    fun getServicesInfo(): String{
        var servStr: String = reservationServices.fold("") { acc, i ->
            if (acc.isNotEmpty()) {
                "$acc, ${i.description}(${i.cost}€)"
            } else {
                "${i.description} (${i.cost}€)"
            }
        }
        Log.i("CreateReservationViewModel", "str: $servStr")
        if (servStr.isNotEmpty()) {
            servStr = "I'd like to request $servStr.\n"
        }
        if (reservationRequests.isNotEmpty()) {
            servStr = "${servStr}Other requests: $reservationRequests"
        }
        return servStr
    }
    fun getTotalServiceCost(): String{
        val total: Double = reservationServices.fold(0.0) { acc, i ->
            Log.i("ServicePrices", "$acc + ${i.cost}")
            acc + i.cost
        }

        return String.format("%.2f", total)
    }
}