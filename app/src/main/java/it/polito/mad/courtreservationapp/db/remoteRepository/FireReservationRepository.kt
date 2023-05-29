

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import it.polito.mad.courtreservationapp.db.RemoteDataSource
import it.polito.mad.courtreservationapp.db.relationships.CourtWithSportCenter
import it.polito.mad.courtreservationapp.db.relationships.ReservationWithServices
import it.polito.mad.courtreservationapp.db.relationships.ReservationWithSportCenter
import it.polito.mad.courtreservationapp.db.relationships.ReservationWithReview
import it.polito.mad.courtreservationapp.models.*
import it.polito.mad.courtreservationapp.view_model.ReservationBrowserViewModel
import kotlinx.coroutines.tasks.await

class FireReservationRepository(val application: Application, val vm: ReservationBrowserViewModel?) {
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
        //optional reservationId reservationWithServices.reservation.reservationId
        val reservationId = reservationWithServices.reservation.reservationId
        var isEdit = !reservationWithServices.reservation.reservationId.isNullOrEmpty()


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


        var content = hashMapOf(
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


        if(isEdit){
            database.collection("reservations").document(reservationId).set(content)
                .addOnSuccessListener { documentReference ->
                    //content["reservationId"] = reservationId
                    database.collection("sport-centers").document(sportCenterId).collection("courts")
                        .document(reservationWithServices.reservation.reservationCourtId).collection("reservations")
                        .whereEqualTo("reservationId", reservationId).get().addOnSuccessListener {
                            for (document in it.documents) {
                                document.reference.set(content)
                            }
                        }
                    database.collection("sport-centers").document(sportCenterId).collection("courts")
                        .document(reservationWithServices.reservation.reservationCourtId!!).update(flag)
                    database.collection("users")
                        .document(reservationWithServices.reservation.reservationUserId!!)
                        .collection("reservations")
                        .whereEqualTo("reservationId", reservationId).get().addOnSuccessListener {
                            for (document in it.documents) {
                                document.reference.set(content)
                            }
                        }
                }
        }else{
            database.collection("reservations").add(content)
                .addOnSuccessListener { documentReference ->
                    val id = documentReference.id
                    content["reservationId"] = id
                    database.collection("sport-centers").document(sportCenterId).collection("courts")
                        .document(reservationWithServices.reservation.reservationCourtId!!)
                        .collection("reservations").add(content)
                    database.collection("sport-centers").document(sportCenterId).collection("courts")
                        .document(reservationWithServices.reservation.reservationCourtId!!).update(flag)
                    database.collection("users")
                        .document(reservationWithServices.reservation.reservationUserId!!)
                        .collection("reservations").add(content)
                }
        }
    }

    suspend fun getCourtItemBySportCenterIdCourtId(sportCenterId: String, courtId: String): Court? {
        Log.i(
            "CREATING RESERVATION",
            "Getting court with ID: $courtId from sport center: $sportCenterId"
        )
        val courtRef =
            database.collection("sport-centers").document(sportCenterId).collection("courts")
                .document(courtId)
                .get()
                .await()
        val sportName = courtRef.data?.get("sport_name") as String
        val imageName = courtRef.data?.get("image_name") as String?
        return Court(sportCenterId, sportName, 0, courtId, imageName)
    }

    suspend fun getSportCenterItemBySportCenterId(sportCenterId: String): SportCenter? {
        Log.i("CREATING RESERVATION", "Getting sport center with ID: $sportCenterId")
        val sportCenterRef = database.collection("sport-centers").document(sportCenterId)
            .get()
            .await()
        val name = sportCenterRef.data?.get("name") as String
        val address = sportCenterRef.data?.get("address") as String
        val description = sportCenterRef.data?.get("description") as String
        val image = sportCenterRef.data?.get("image_name") as String?
        return SportCenter(name, address, description, sportCenterId, image)
    }

    suspend fun deleteReservations(reservations: List<Reservation>){
       /* for(reservation in reservations){
            reservationDao.delete(reservation)
        }*/ //TODO
    }

    fun deleteReservationById(reservationId: String, userEmail: String, sportCenterId: String, courtId: String) {
        deleteFromReservations(reservationId)
        deleteFromUsersReservations(reservationId, userEmail)
        deleteFromSportCenterCourtReservations(reservationId, sportCenterId, courtId)
    }

    private fun deleteFromReservations(reservationId: String) {
        val reservationRef = database.collection("reservations").document(reservationId)
        reservationRef.delete()
            .addOnSuccessListener {
                println("Successfully deleted reservation $reservationId from reservations")
            }
            .addOnFailureListener{
                println("Error deleting reservation $reservationId from reservations")
            }
    }

    private fun deleteFromUsersReservations(reservationId: String, userEmail: String) {
        database
            .collection("users")
            .document(userEmail)
            .collection("reservations")
            .whereEqualTo("reservationId", reservationId)
            .get()
            .addOnSuccessListener{
                for(document in it.documents) {
                    document.reference.delete()
                        .addOnSuccessListener {
                            println("Successfully deleted reservation $reservationId from users/$userEmail/reservations/$reservationId")
                        }
                        .addOnFailureListener {
                            println("Error deleting reservation $reservationId from users/$userEmail/reservations/$reservationId")
                        }
                }
            }
    }

    private fun deleteFromSportCenterCourtReservations(reservationId: String, sportCenterId: String, courtId: String) {
        database
            .collection("sport-centers")
            .document(sportCenterId)
            .collection("courts")
            .document(courtId)
            .collection("reservations")
            .whereEqualTo("reservationId", reservationId)
            .get()
            .addOnSuccessListener{
                for(document in it.documents) {
                    document.reference.delete()
                        .addOnSuccessListener {
                            println("Successfully deleted reservation $reservationId from sport-centers/$sportCenterId/courts/$courtId/reservations")
                        }
                        .addOnFailureListener {
                            println("Error deleting reservation $reservationId from sport-centers/$sportCenterId/courts/$courtId/reservations")
                        }
                }
            }
            .addOnFailureListener{
                println("Error getting reservation from sport-centers/$sportCenterId/courts/$courtId/reservations/")
            }
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
