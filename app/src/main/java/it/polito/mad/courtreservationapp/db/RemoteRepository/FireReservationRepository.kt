

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import it.polito.mad.courtreservationapp.db.RemoteDataSource
import it.polito.mad.courtreservationapp.db.relationships.CourtWithSportCenter
import it.polito.mad.courtreservationapp.db.relationships.ReservationWithServices
import it.polito.mad.courtreservationapp.db.relationships.ReservationWithSportCenter
import it.polito.mad.courtreservationapp.db.relationships.ReservationWithReview
import it.polito.mad.courtreservationapp.models.*
import kotlinx.coroutines.tasks.await

class FireReservationRepository(val application: Application) {
    private val database: FirebaseFirestore = RemoteDataSource.instance
    private val serviceMap: Map<Int, Service> = mapOf(
        Pair(0, Service("Safety shower", 0)),
        Pair(1, Service("Equipment", 1)),
        Pair(2, Service("Coach", 2)),
        Pair(3, Service("Refreshment", 3))
    )

    suspend fun insertReservationWithServices(
        reservationWithServices: ReservationWithServices,
        sportCenterId: String
    ){
        //save reservation
        /*
        val reservationId = reservationDao.save(reservationWithServices.reservation)
        reservationWithServices.reservation.reservationId = reservationId
        //save service requested
        for(service in reservationWithServices.services){
            reservationAndServiceDao.save(ReservationServiceCrossRef(reservationId, service.serviceId))
        }

         */ //TODO replace with firebase
        val content = hashMapOf(
            "date" to reservationWithServices.reservation.reservationDate,
            "request" to reservationWithServices.reservation.request,
            "timeslot" to reservationWithServices.reservation.timeSlotId,
            "user" to reservationWithServices.reservation.reservationUserId,
            "services" to reservationWithServices.services
        )
        //TODO: decide to save services as {Id, name} or {Id}
        database.collection("sport-centers").document(sportCenterId).collection("courts").document(reservationWithServices.reservation.reservationCourtId!!).collection("reservations").document().set(content).addOnSuccessListener{
            //TODO: add reference in the user
//            database.collection("users").document(reservationWithServices.reservation.reservationUserId!!).collection("reservations").document().set()
        }


    }

    suspend fun deleteReservations(reservations: List<Reservation>){
       /* for(reservation in reservations){
            reservationDao.delete(reservation)
        }*/ //TODO
    }

    suspend fun deleteReservationById(reservationId: Long) {
        //reservationDao.deleteById(reservationId) //TODO
    }

    fun getAll(): LiveData<List<Reservation>>{
        //return reservationDao.getAll()
        return MutableLiveData() //TODO
    }

    fun getById(reservationId: Long): LiveData<Reservation>{
        //return reservationDao.getById(reservationId)
        return MutableLiveData() //TODO
    }


    fun getByIdWithServices(reservationId: String): LiveData<ReservationWithServices>{
        //return reservationDao.getByIdWithServices(reservationId)
        return MutableLiveData() //TODO
    }


    suspend fun getReservationsByUser(userEmail: String): List<Reservation> {
        val result: MutableList<Reservation> = mutableListOf()

        val reservationsSnapshot = database.collection("users").document(userEmail).collection("reservations").get().await()
        if(reservationsSnapshot != null) {
            for(reservation in reservationsSnapshot.documents) {
                val reservReference: DocumentReference = reservation.data?.get("ref") as DocumentReference
                val reservItem = reservReference.get().await()
                val reservDate: String = reservItem.data?.get("date") as String
                val request: String? = reservItem.data?.get("request") as String?
                val timeslotId: Long = reservItem.data?.get("timeslot") as Long
                val reservationItem = Reservation(reservDate, timeslotId, userEmail, null, request, reservation.id)
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
                val reservationItem = Reservation(reservDate, timeslotId, userEmail, null, request, reservation.id)
                val sportCenterId = reservReference.path.split("/")[1]
                val sportCenterSnapshot = database.collection("sport-centers").document(sportCenterId).get().await()
                val sportCenterName: String = sportCenterSnapshot.data?.get("name") as String

                val sportCenterAddress: String = sportCenterSnapshot.data?.get("address") as String
                val sportCenterDescription: String = sportCenterSnapshot.data?.get("description") as String
                val sportCenterItem = SportCenter(sportCenterName, sportCenterAddress, sportCenterDescription, sportCenterId)
                val courtId = reservReference.path.split("/")[3]
                val courtSnapshot = database.collection("sport-centers").document(sportCenterId).collection("courts").document(courtId).get().await()
                val sportName = courtSnapshot.data?.get("sport_name") as String
                val courtItem = Court(sportCenterId, sportName, 0, courtId)
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
                val reservationItem = Reservation(reservDate, timeslotId, userEmail, null, request, reservation.id)
                val servicesIds = reservItem.data?.get("services") as ArrayList<*>
                val serviceList = mutableListOf<Service>()
                for(id in servicesIds){
                    val serv = serviceMap[id]
                    if(serv!=null)
                        serviceList.add(serv)
                }
                val reservWithServices = ReservationWithServices(reservationItem, serviceList)
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
                val reservationItem = Reservation(reservDate, timeslotId, null, null, request, reservation.id)
                val reviewText = reservItem.data?.get("review_content") as String?
                val rating = (reservItem.data?.get("rating") as Long?)?.toInt() //TODO review might not be there
                val date = reservItem.data?.get("review_date") as String?
                val reviewItem = Review(null, null, reservation.id, reviewText, rating?: 0, date?:"", reservation.id)
                val reservWithReview = ReservationWithReview(reservationItem, reviewItem)
                result.add(reservWithReview)
            }
        }
        return result
    }
}
