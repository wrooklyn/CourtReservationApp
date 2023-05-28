

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

        */
        val courtRef = database
            .collection("sport-centers")
            .document(sportCenterId)
            .collection("courts")
            .document(reservationWithServices.reservation.reservationCourtId!!)
            .get().await()

        val courtDisplayName = courtRef.data?.get("display_name") as String? ?: "Court ID"

         //TODO replace with firebase
        val content = hashMapOf(
            "date" to reservationWithServices.reservation.reservationDate,
            "request" to reservationWithServices.reservation.request,
            "timeslot" to reservationWithServices.reservation.timeSlotId,
            "user" to reservationWithServices.reservation.reservationUserId,
            "services" to reservationWithServices.services,
            "courtId" to reservationWithServices.reservation.reservationCourtId,
            "courtDisplayName" to courtDisplayName,
            "sportCenterId" to sportCenterId
        )
        val flag = hashMapOf(
            "lastUpdated" to System.currentTimeMillis(),
        ) as Map<String, Any>
        //TODO: decide to save services as {Id, name} or {Id}
        database.collection("sport-centers").document(sportCenterId).collection("courts").document(reservationWithServices.reservation.reservationCourtId!!).collection("reservations").document().set(content).addOnSuccessListener{
            //This allows realtime updates to everyone reserving
            database.collection("sport-centers").document(sportCenterId).collection("courts").document(reservationWithServices.reservation.reservationCourtId!!).update(flag)
            database.collection("reservations").add(content)
                .addOnSuccessListener{
                    val id = it.id
                    database.collection("reservations").document(id).update(flag)
                }
            database.collection("users").document(reservationWithServices.reservation.reservationUserId!!).collection("reservations").add(content)
                .addOnSuccessListener{
                    val id = it.id
                    database
                        .collection("users")
                        .document(reservationWithServices.reservation.reservationUserId!!)
                        .collection("reservations")
                        .document(id)
                        .update(flag)
                }

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


    fun getReservationsByUser(userEmail: String): List<Reservation> {
        val result: MutableList<Reservation> = mutableListOf()

        val reservationsQuery = database.collection("reservations").whereEqualTo("user", userEmail)
        reservationsQuery.get()
            .addOnSuccessListener { querySnapshot ->
                if(!querySnapshot.isEmpty) {
                    for(reservation in querySnapshot.documents) {
                        val reservDate: String = reservation.data?.get("date") as String
                        val request: String? = reservation.data?.get("request") as String?
                        val timeslotId: Long = reservation.data?.get("timeslot") as Long
                        val courtId: String = reservation.data?.get("courtDisplayName") as String
                        val reservationItem = Reservation(reservDate, timeslotId, userEmail, courtId, request, reservation.id)
                        result.add(reservationItem)
                    }
                }
            }
        return result
    }

    fun getReservationLocationsByUserId(userEmail: String): List<ReservationWithSportCenter> {
        val result: MutableList<ReservationWithSportCenter> = mutableListOf()
        val database: FirebaseFirestore = RemoteDataSource.instance
        val reservationsQuery = database.collection("reservations").whereEqualTo("user", userEmail)
        reservationsQuery.get()
            .addOnSuccessListener { querySnapshot ->
                if(!querySnapshot.isEmpty){
                    for(reservation in querySnapshot.documents) {
                        val reservDate: String = reservation.data?.get("date") as String
                        val request: String? = reservation.data?.get("request") as String?
                        val timeslotId: Long = reservation.data?.get("timeslot") as Long
                        val courtId: String = reservation.data?.get("courtId") as String
                        val courtDisplayName: String = reservation.data?.get("courtDisplayName") as String
                        val reservationItem = Reservation(reservDate, timeslotId, userEmail, courtId, request, reservation.id)
                        val sportCenterId: String = reservation.data?.get("sportCenterId") as String
                        val sportCenterRef = database.collection("sport-centers").document(sportCenterId)
                        sportCenterRef.get().addOnSuccessListener { sportCenterDoc ->
                            if(sportCenterDoc.exists()) {
                                val sportCenterName: String = sportCenterDoc.data?.get("name") as String
                                val sportCenterAddress: String = sportCenterDoc.data?.get("address") as String
                                val sportCenterDescription: String = sportCenterDoc.data?.get("description") as String
                                val sportCenterImageName: String = sportCenterDoc.data?.get("image_name") as String
                                val sportCenterItem = SportCenter(sportCenterName, sportCenterAddress, sportCenterDescription, sportCenterId, sportCenterImageName)
                                val courtRef = database.collection("sport-centers").document(sportCenterId).collection("courts").document(courtId)
                                courtRef.get().addOnSuccessListener { courtDoc ->
                                    if(courtDoc.exists()) {
                                        val sportName: String = courtDoc.data?.get("sport_name") as String
                                        val imageName: String? = courtDoc.data?.get("image_name") as String?
                                        val courtItem = Court(sportCenterId, sportName, 0, courtDisplayName, imageName)
                                        val courtWithSc = CourtWithSportCenter(courtItem, sportCenterItem)
                                        val reservationWithSc = ReservationWithSportCenter(reservationItem, courtWithSc)
                                        result.add(reservationWithSc)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        return result
    }

    fun getReservationServicesByUserId(userEmail: String): List<ReservationWithServices> {
        val result: MutableList<ReservationWithServices> = mutableListOf()
        val database: FirebaseFirestore = RemoteDataSource.instance
        val reservationsQuery = database.collection("reservations").whereEqualTo("user", userEmail)
        reservationsQuery.get()
            .addOnSuccessListener { querySnapshot ->
                if(!querySnapshot.isEmpty){
                    for(reservation in querySnapshot.documents) {
                        val reservDate: String = reservation.data?.get("date") as String
                        val request: String? = reservation.data?.get("request") as String?
                        val timeslotId: Long = reservation.data?.get("timeslot") as Long
                        val courtId: String = reservation.data?.get("courtId") as String
                        val reservationItem = Reservation(
                            reservDate,
                            timeslotId,
                            userEmail,
                            courtId,
                            request,
                            reservation.id
                        )
                        val servicesIds = reservation.data?.get("services") as ArrayList<*>
                        val serviceList = mutableListOf<Service>()
                        for (id in servicesIds) {
                            val serv = serviceMap[id]
                            if (serv != null)
                                serviceList.add(serv)
                        }
                        val reservWithServices =
                            ReservationWithServices(reservationItem, serviceList)
                        result.add(reservWithServices)
                    }
            }
        }
        return result
    }

    fun getReservationsReviewsByUserId(userEmail: String): List<ReservationWithReview> {
        val result: MutableList<ReservationWithReview> = mutableListOf()
        val database: FirebaseFirestore = RemoteDataSource.instance
        val reservationsQuery = database.collection("reservations").whereEqualTo("user", userEmail)
        reservationsQuery.get()
            .addOnSuccessListener { querySnapshot ->
                if(!querySnapshot.isEmpty) {
                    for(reservation in querySnapshot.documents) {
                        val reservDate: String = reservation.data?.get("date") as String
                        val request: String? = reservation.data?.get("request") as String?
                        val timeslotId: Long = reservation.data?.get("timeslot") as Long
                        val courtId: String = reservation.data?.get("courtId") as String
                        var review: Review? = null
                        val reservationItem = Reservation(
                            reservDate,
                            timeslotId,
                            userEmail,
                            courtId,
                            request,
                            reservation.id
                        )
                        if(reservation.contains("review")) {
                            val reviewMap = reservation.data?.get("review") as Map<*, *>
                            val rating = reviewMap["rating"] as Int
                            val reviewDate = reviewMap["date"] as String
                            val reviewText = reviewMap["text"] as String
                            val reviewId = reviewMap["id"] as String
                            review = Review(courtId, userEmail, reservation.id, reviewText, rating, reviewDate, reviewId)
                        }
                        val reservWithReview = ReservationWithReview(reservationItem, review)
                        result.add(reservWithReview)
                    }
                }
            }
        return result
    }
}
