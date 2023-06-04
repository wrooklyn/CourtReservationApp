package it.polito.mad.courtreservationapp.view_model

import FireReservationRepository
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.db.RemoteDataSource
import it.polito.mad.courtreservationapp.db.relationships.ReservationWithReview
import it.polito.mad.courtreservationapp.db.relationships.ReservationWithServices
import it.polito.mad.courtreservationapp.db.relationships.ReservationWithSportCenter
import it.polito.mad.courtreservationapp.db.remoteRepository.FireInviteRepository
import it.polito.mad.courtreservationapp.models.Reservation
import it.polito.mad.courtreservationapp.models.SportCenter
import it.polito.mad.courtreservationapp.views.login.SavedPreference
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class ReservationBrowserViewModel(application: Application) : AndroidViewModel(application) {
    private val reservationRepo: FireReservationRepository = FireReservationRepository(application)
    private val inviteRepository: FireInviteRepository = FireInviteRepository(application)
    lateinit var sportCenter: LiveData<SportCenter>

    private val _userReservations: MutableLiveData<List<Reservation>> = MutableLiveData()
    val userReservations: LiveData<List<Reservation>> = _userReservations

    private val _userReservationsLocations: MutableLiveData<List<ReservationWithSportCenter>> =
        MutableLiveData()
    val userReservationsLocations: LiveData<List<ReservationWithSportCenter>> =
        _userReservationsLocations

    private val _userReservationsServices: MutableLiveData<List<ReservationWithServices>> =
        MutableLiveData()
    val userReservationsServices: LiveData<List<ReservationWithServices>> =
        _userReservationsServices

    private val _userReservationsReviews: MutableLiveData<List<ReservationWithReview>> =
        MutableLiveData()
    val userReservationsReviews: LiveData<List<ReservationWithReview>> = _userReservationsReviews

    //    val servicesIcons: Map<Long, Int> = mapOf(
//        Pair(0, R.drawable.safety_shower),
//        Pair(1, R.drawable.equipment),
//        Pair(2, R.drawable.coach),
//        Pair(3, R.drawable.refreshment)
//    )
    operator fun <T> List<T>.plus(other: List<T>): List<T> {
        val result = ArrayList<T>(size + other.size)
        result.addAll(this)
        result.addAll(other)
        return result
    }

    fun initUserReservations() {
        val userEmail = SavedPreference.EMAIL
        viewModelScope.launch {
            try {
                val invitesReceived =
                    inviteRepository.getAcceptedReceivedByUserId(SavedPreference.EMAIL)
                println("Invites list: $invitesReceived")

                val invitedReserv = reservationRepo.getReservationsFromInvites(invitesReceived)
                val userReserv = reservationRepo.getReservationsByUser(userEmail)
                _userReservations.postValue(userReserv + invitedReserv)

                val invitedReservLocations = reservationRepo.getReservationLocationsFromInvites(invitesReceived)
                val userReservLocations = reservationRepo.getReservationLocationsByUserId(userEmail)
                _userReservationsLocations.postValue(userReservLocations + invitedReservLocations)

                val invitedReservServices = reservationRepo.getReservationServicesFromInvites(invitesReceived)
                val userReservServices = reservationRepo.getReservationServicesByUserId(userEmail)
                _userReservationsServices.postValue(userReservServices + invitedReservServices)


                val invitedReservReviews = reservationRepo.getReservationReviewsFromInvites(invitesReceived)
                val userReservReviews = reservationRepo.getReservationsReviewsByUserId(userEmail)
                _userReservationsReviews.postValue(userReservReviews + invitedReservReviews)

            } catch (e: Exception) {
                println("Error initializing reservations: $e")
            }
        }
    }

    fun deleteReservation(
        reservationId: String,
        userEmail: String,
        sportCenterId: String,
        courtId: String
    ) {
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

    fun addReservation(
        reservation: Reservation,
        reservationLocation: ReservationWithSportCenter,
        reservationServices: ReservationWithServices,
        reservationReview: ReservationWithReview
    ) {
        val updatedReservations = userReservations.value?.toMutableList() ?: mutableListOf()
        updatedReservations.add(reservation)
        _userReservations.postValue(updatedReservations)

        val updatedReservationsLocation =
            userReservationsLocations.value?.toMutableList() ?: mutableListOf()
        updatedReservationsLocation.add(reservationLocation)
        _userReservationsLocations.postValue(updatedReservationsLocation)

        val updatedReservationsServices =
            userReservationsServices.value?.toMutableList() ?: mutableListOf()
        updatedReservationsServices.add(reservationServices)
        _userReservationsServices.postValue(updatedReservationsServices)

        val updatedReservationsReview =
            userReservationsReviews.value?.toMutableList() ?: mutableListOf()
        updatedReservationsReview.add(reservationReview)
        _userReservationsReviews.postValue(updatedReservationsReview)
    }

    fun hasAlreadyReviewed(sportCenterId: String, courtId: String, reservationId: String): Boolean {
        var result = false
        runBlocking {
            launch {
                Log.i("ReservationBrowserVM", "sportcenter: ${sportCenterId}")
                Log.i("ReservationBrowserVM", "court: ${courtId}")
                Log.i("ReservationBrowserVM", "reservation: ${reservationId}")
//                Log.i("ReservationBrowserVM", "result: ${query.documents}")
                val db = RemoteDataSource.instance
                val query = db.collection("sport-centers")
                    .document(sportCenterId)
                    .collection("courts")
                    .document(courtId)
                    .collection("reservations")
                    .whereEqualTo("reservationId", reservationId)
                    .get().await()
                Log.i("ReservationBrowserVM", "result: ${query.documents}")
                result = query.documents.first().data?.contains("review_date") ?: false
            }
        }
        return result
    }
}