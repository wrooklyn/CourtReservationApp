package it.polito.mad.courtreservationapp.db.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import it.polito.mad.courtreservationapp.db.AppDatabase
import it.polito.mad.courtreservationapp.db.RemoteDataSource
import it.polito.mad.courtreservationapp.db.crossref.ReservationServiceCrossRef
import it.polito.mad.courtreservationapp.db.relationships.CourtWithSportCenter
import it.polito.mad.courtreservationapp.db.relationships.ReservationWithServices
import it.polito.mad.courtreservationapp.db.relationships.ReservationWithSportCenter
import it.polito.mad.courtreservationapp.db.relationships.ReservationWithReview
import it.polito.mad.courtreservationapp.models.*
import kotlinx.coroutines.tasks.await

class FireReservationRepository(val application: Application) {
    private val db: AppDatabase = AppDatabase.getDatabase(application)
    private val reservationDao = db.reservationDao()
    private val reservationAndServiceDao = db.reservationAndServiceDao()
    private val reviewDao = db.reviewDao()

    private val serviceMap: Map<Int, Service> = mapOf(
        Pair(0, Service("Safety shower", 0)),
        Pair(1, Service("Equipment", 1)),
        Pair(2, Service("Coach", 2)),
        Pair(3, Service("Refreshment", 3))
    )

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


    suspend fun getReservationsByUser(userEmail: String): List<Reservation> {
        val result: MutableList<Reservation> = mutableListOf()
        val database: FirebaseFirestore = RemoteDataSource.instance
        val reservationsSnapshot = database.collection("users").document(userEmail).collection("reservations").get().await()
        if(reservationsSnapshot != null) {
            for(reservation in reservationsSnapshot.documents) {
                val reservReference: DocumentReference = reservation.data?.get("ref") as DocumentReference
                val reservItem = reservReference.get().await()
                val reservDate: String = reservItem.data?.get("date") as String
                val request: String? = reservItem.data?.get("request") as String?
                val timeslotId: Long = reservItem.data?.get("timeslot") as Long
                val reservationItem = Reservation(reservDate, timeslotId, 0L, 0L, request, 0L)
                result.add(reservationItem)
            }
        }
        return result
    }

    suspend fun getReservationLocationsByUserId(userEmail: String): List<ReservationWithSportCenter> {
        val result: MutableList<ReservationWithSportCenter> = mutableListOf()
        val database: FirebaseFirestore = RemoteDataSource.instance
        val reservationsSnapshot = database.collection("users").document(userEmail).collection("reservations").get().await()
        if(reservationsSnapshot != null) {
            for(reservation in reservationsSnapshot.documents) {
                val reservReference: DocumentReference = reservation.data?.get("ref") as DocumentReference
                val reservItem = reservReference.get().await()
                val reservDate: String = reservItem.data?.get("date") as String
                val request: String? = reservItem.data?.get("request") as String?
                val timeslotId: Long = reservItem.data?.get("timeslot") as Long
                val reservationItem = Reservation(reservDate, timeslotId, 0L, 0L, request, 0L)
                val sportCenterId = reservReference.path.split("/")[1]
                val sportCenterSnapshot = database.collection("sport-center").document(sportCenterId).get().await()
                val sportCenterName: String = sportCenterSnapshot.data?.get("name") as String
                val sportCenterAddress: String = sportCenterSnapshot.data?.get("address") as String
                val sportCenterDescription: String = sportCenterSnapshot.data?.get("description") as String
                val sportCenterItem = SportCenter(sportCenterName, sportCenterAddress, sportCenterDescription, 0L)
                val courtId = reservReference.path.split("/")[3]
                val courtSnapshot = database.collection("sport-center").document(sportCenterId).collection("court").document(courtId).get().await()
                val sportName = courtSnapshot.data?.get("sport_name") as String
                val courtItem = Court(0L, sportName, 0, 0L)
                val courtWithSC = CourtWithSportCenter(courtItem, sportCenterItem)
                val reservWithSC = ReservationWithSportCenter(reservationItem, courtWithSC)
                result.add(reservWithSC)
            }
        }
        return result
    }

    suspend fun getReservationServicesByUserId(userEmail: String): List<ReservationWithServices> {
        val result: MutableList<ReservationWithServices> = mutableListOf()
        val database: FirebaseFirestore = RemoteDataSource.instance
        val reservationsSnapshot = database.collection("users").document(userEmail).collection("reservations").get().await()
        if(reservationsSnapshot != null) {
            for(reservation in reservationsSnapshot.documents) {
                val reservReference: DocumentReference = reservation.data?.get("ref") as DocumentReference
                val reservItem = reservReference.get().await()
                val reservDate: String = reservItem.data?.get("date") as String
                val request: String? = reservItem.data?.get("request") as String?
                val timeslotId: Long = reservItem.data?.get("timeslot") as Long
                val reservationItem = Reservation(reservDate, timeslotId, 0L, 0L, request, 0L)
                val servicesIds = reservItem.data?.get("services") as Array<*>
                val reservWithServices = ReservationWithServices(reservationItem, servicesIds.map{ serviceMap[it]!!})
                result.add(reservWithServices)
            }
        }
        return result
    }

    suspend fun getReservationsReviewsByUserId(userEmail: String): List<ReservationWithReview> {
        val result: MutableList<ReservationWithReview> = mutableListOf()
        val database: FirebaseFirestore = RemoteDataSource.instance
        val reservationsSnapshot = database.collection("users").document(userEmail).collection("reservations").get().await()
        if(reservationsSnapshot != null) {
            for(reservation in reservationsSnapshot.documents) {
                val reservReference: DocumentReference = reservation.data?.get("ref") as DocumentReference
                val reservItem = reservReference.get().await()
                val reservDate: String = reservItem.data?.get("date") as String
                val request: String? = reservItem.data?.get("request") as String?
                val timeslotId: Long = reservItem.data?.get("timeslot") as Long
                val reservationItem = Reservation(reservDate, timeslotId, 0L, 0L, request, 0L)
                val reviewText: String = reservItem.data?.get("review_content") as String
                val rating: Int = reservItem.data?.get("rating") as Int
                val date: String = reservItem.data?.get("review_date") as String
                val reviewItem = Review(0L, 0L, 0L, reviewText, rating, date, 0L)
                val reservWithReview = ReservationWithReview(reservationItem, reviewItem)
                result.add(reservWithReview)
            }
        }
        return result
    }
}
