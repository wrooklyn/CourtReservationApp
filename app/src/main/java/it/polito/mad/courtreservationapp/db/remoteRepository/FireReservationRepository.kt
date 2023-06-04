

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
import it.polito.mad.courtreservationapp.utils.DbUtils
import it.polito.mad.courtreservationapp.utils.ServiceUtils
import it.polito.mad.courtreservationapp.view_model.ReservationBrowserViewModel
import it.polito.mad.courtreservationapp.views.login.SavedPreference
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


        if (isEdit) {
            database.collection("reservations").document(reservationId).set(content)
                .addOnSuccessListener { documentReference ->
                    //content["reservationId"] = reservationId
                    database.collection("sport-centers").document(sportCenterId)
                        .collection("courts")
                        .document(reservationWithServices.reservation.reservationCourtId)
                        .collection("reservations")
                        .whereEqualTo("reservationId", reservationId).get().addOnSuccessListener {
                            for (document in it.documents) {
                                document.reference.set(content)
                            }
                        }
                    database.collection("sport-centers").document(sportCenterId)
                        .collection("courts")
                        .document(reservationWithServices.reservation.reservationCourtId!!)
                        .update(flag)
                    database.collection("users")
                        .document(reservationWithServices.reservation.reservationUserId!!)
                        .collection("reservations")
                        .whereEqualTo("reservationId", reservationId).get().addOnSuccessListener {
                            for (document in it.documents) {
                                document.reference.set(content)
                            }
                        }
                }
        } else {
            database.collection("reservations").add(content)
                .addOnSuccessListener { documentReference ->
                    val id = documentReference.id
                    content["reservationId"] = id
                    database.collection("sport-centers").document(sportCenterId)
                        .collection("courts")
                        .document(reservationWithServices.reservation.reservationCourtId!!)
                        .collection("reservations").add(content)
                    database.collection("sport-centers").document(sportCenterId)
                        .collection("courts")
                        .document(reservationWithServices.reservation.reservationCourtId!!)
                        .update(flag)
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
//        val sportName = courtRef.data?.get("sport_name") as String
//        val imageName = courtRef.data?.get("image_name") as String?
//        val cost: Double = courtRef.data?.get("cost") as Double
//        return Court(sportCenterId, sportName, 0, courtId, cost, imageName)
        return DbUtils.getCourt(courtRef, sportCenterId)
    }

    suspend fun getSportCenterItemBySportCenterId(sportCenterId: String): SportCenter? {
        Log.i("CREATING RESERVATION", "Getting sport center with ID: $sportCenterId")
        val sportCenterRef = database.collection("sport-centers").document(sportCenterId)
            .get()
            .await()
//        val name = sportCenterRef.data?.get("name") as String
//        val address = sportCenterRef.data?.get("address") as String
//        val description = sportCenterRef.data?.get("description") as String
//        val image = sportCenterRef.data?.get("image_name") as String?
//        return SportCenter(name, address, description, sportCenterId, image)
        return DbUtils.getSportCenter(sportCenterRef)
    }


    fun deleteReservationById(
        reservationId: String,
        userEmail: String,
        sportCenterId: String,
        courtId: String
    ) {
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
            .addOnFailureListener {
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
            .addOnSuccessListener {
                for (document in it.documents) {
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

    private fun deleteFromSportCenterCourtReservations(
        reservationId: String,
        sportCenterId: String,
        courtId: String
    ) {
        database
            .collection("sport-centers")
            .document(sportCenterId)
            .collection("courts")
            .document(courtId)
            .collection("reservations")
            .whereEqualTo("reservationId", reservationId)
            .get()
            .addOnSuccessListener {
                for (document in it.documents) {
                    document.reference.delete()
                        .addOnSuccessListener {
                            println("Successfully deleted reservation $reservationId from sport-centers/$sportCenterId/courts/$courtId/reservations")
                        }
                        .addOnFailureListener {
                            println("Error deleting reservation $reservationId from sport-centers/$sportCenterId/courts/$courtId/reservations")
                        }
                }
            }
            .addOnFailureListener {
                println("Error getting reservation from sport-centers/$sportCenterId/courts/$courtId/reservations/")
            }
    }


    fun getByIdWithServices(reservationId: String): LiveData<ReservationWithServices> {
        //return reservationDao.getByIdWithServices(reservationId)
        val resLiveData = MutableLiveData<ReservationWithServices>()
        database.collection("reservations").document(reservationId).get().addOnSuccessListener {
            if(it.exists()){
//                val courtId = it.data?.get("courtId") as String?
//                val date = it.data?.get("date") as String
//                val timeslot = it.data?.get("timeslot") as Long
//                val requests = it.data?.get("request") as String
//                val reservation = Reservation(date, timeslot,SavedPreference.EMAIL, courtId, requests, reservationId)
                val reservation = DbUtils.getReservation(it)

//                val servicesIds = it.data?.get("services") as List<*>
//                val serviceList = ServiceUtils.getServices(servicesIds)
//                for (id in servicesIds) {
//                    val serv = serviceMap[id]
//                    if (serv != null)
//                        serviceList.add(serv)
//                }
                val serviceList = DbUtils.getServices(it)
                val item = ReservationWithServices(reservation,serviceList)
                resLiveData.postValue(item)
            }
        }
        return resLiveData
    }


    suspend fun getReservationsByUser(userEmail: String): List<Reservation> {
        val result: MutableList<Reservation> = mutableListOf()

        val reservationsQuery = database.collection("reservations").whereEqualTo("user", userEmail)
        val reservationData = reservationsQuery.get().await()
        for (reservation in reservationData) {
//            val reservDate: String = reservation.data?.get("date") as String
//            val request: String? = reservation.data?.get("request") as String?
//            val timeslotId: Long = reservation.data?.get("timeslot") as Long
//            val courtId: String = reservation.data?.get("courtDisplayName") as String
//            val reservationItem =
//                Reservation(reservDate, timeslotId, userEmail, courtId, request, reservation.id)
            val reservationItem = DbUtils.getReservation(reservation)
            result.add(reservationItem)
        }
        return result
    }

    suspend fun getReservationsFromInvites(invites: List<Invite>): List<Reservation> {
        val result: MutableList<Reservation> = mutableListOf()

        for (invite in invites) {
            val reservation =
                database.collection("reservations").document(invite.reservationId).get().await()
//            val reservDate: String = reservation.data?.get("date") as String
//            val request: String? = reservation.data?.get("request") as String?
//            val timeslotId: Long = reservation.data?.get("timeslot") as Long
//            val courtId: String = reservation.data?.get("courtDisplayName") as String
//            val reservationItem = Reservation(
//                reservDate,
//                timeslotId,
//                invite.inviter,
//                courtId,
//                request,
//                reservation.id,
//                true,
//            )
            val reservationItem = DbUtils.getReservation(reservation)
            reservationItem.isGuest=true
            result.add(reservationItem)
        }
        return result
    }


    suspend fun getReservationLocationsByUserId(userEmail: String): List<ReservationWithSportCenter> {
        val result: MutableList<ReservationWithSportCenter> = mutableListOf()
        val database: FirebaseFirestore = RemoteDataSource.instance
        val reservationsData =
            database.collection("reservations").whereEqualTo("user", userEmail).get().await()

        for (reservation in reservationsData.documents) {
//            val reservDate: String = reservation.data?.get("date") as String
//            val request: String? = reservation.data?.get("request") as String?
//            val timeslotId: Long = reservation.data?.get("timeslot") as Long
//            val courtId: String = reservation.data?.get("courtId") as String
//
//            val reservationItem =
//                Reservation(reservDate, timeslotId, userEmail, courtId, request, reservation.id)
            val reservationItem = DbUtils.getReservation(reservation)
            val sportCenterId: String = reservation.data?.get("sportCenterId") as String
            val sportCenterDoc =
                database.collection("sport-centers").document(sportCenterId).get().await()

            if (sportCenterDoc.exists()) {
//                val sportCenterName: String = sportCenterDoc.data?.get("name") as String
//                val sportCenterAddress: String = sportCenterDoc.data?.get("address") as String
//                val sportCenterDescription: String =
//                    sportCenterDoc.data?.get("description") as String
//                val sportCenterImageName: String =
//                    sportCenterDoc.data?.get("image_name") as String
//                val sportCenterItem = SportCenter(
//                    sportCenterName,
//                    sportCenterAddress,
//                    sportCenterDescription,
//                    sportCenterId,
//                    sportCenterImageName
//                )
                val sportCenterItem = DbUtils.getSportCenter(sportCenterDoc)
                val courtDoc = database.collection("sport-centers").document(sportCenterId)
                    .collection("courts").document(reservationItem.reservationCourtId!!).get().await()

                if (courtDoc.exists()) {
//                    val sportName: String = courtDoc.data?.get("sport_name") as String
//                    val imageName: String? = courtDoc.data?.get("image_name") as String?
//                    val cost: Double = courtDoc.data?.get("cost") as Double
//                    val courtItem = Court(sportCenterId, sportName, 0, courtId, cost, imageName)
                    val courtItem = DbUtils.getCourt(courtDoc, sportCenterItem.centerId)
                    val courtWithSc = CourtWithSportCenter(courtItem, sportCenterItem)
                    val reservationWithSc =
                        ReservationWithSportCenter(reservationItem, courtWithSc)
                    result.add(reservationWithSc)
                }
            }
        }
        return result
    }

    suspend fun getReservationLocationsFromInvites(invites: List<Invite>): List<ReservationWithSportCenter> {
        val result: MutableList<ReservationWithSportCenter> = mutableListOf()

        for (invite in invites) {
            val reservation =
                database.collection("reservations").document(invite.reservationId).get().await()
//            val reservDate: String = reservation.data?.get("date") as String
//            val request: String? = reservation.data?.get("request") as String?
//            val timeslotId: Long = reservation.data?.get("timeslot") as Long
//            val courtId: String = reservation.data?.get("courtId") as String
//
//            val reservationItem =
//                Reservation(
//                    reservDate,
//                    timeslotId,
//                    invite.inviter,
//                    courtId,
//                    request,
//                    reservation.id,
//                    true,
//                )
            val reservationItem = DbUtils.getReservation(reservation)
            reservationItem.isGuest=true
            val sportCenterId: String = reservation.data?.get("sportCenterId") as String
            val sportCenterDoc =
                database.collection("sport-centers").document(sportCenterId).get().await()

            if (sportCenterDoc.exists()) {
//                val sportCenterName: String = sportCenterDoc.data?.get("name") as String
//                val sportCenterAddress: String = sportCenterDoc.data?.get("address") as String
//                val sportCenterDescription: String =
//                    sportCenterDoc.data?.get("description") as String
//                val sportCenterImageName: String =
//                    sportCenterDoc.data?.get("image_name") as String
//                val sportCenterItem = SportCenter(
//                    sportCenterName,
//                    sportCenterAddress,
//                    sportCenterDescription,
//                    sportCenterId,
//                    sportCenterImageName
//                )
                val sportCenterItem = DbUtils.getSportCenter(sportCenterDoc)
                val courtDoc = database.collection("sport-centers").document(sportCenterId)
                    .collection("courts").document(reservationItem.reservationCourtId!!).get().await()

                if (courtDoc.exists()) {
//                    val sportName: String = courtDoc.data?.get("sport_name") as String
//                    val imageName: String? = courtDoc.data?.get("image_name") as String?
//                    val cost: Double = courtDoc.data?.get("cost") as Double
//                    val courtItem = Court(sportCenterId, sportName, 0, courtId,cost, imageName)
                    val courtItem = DbUtils.getCourt(courtDoc, sportCenterItem.centerId)
                    val courtWithSc = CourtWithSportCenter(courtItem, sportCenterItem)
                    val reservationWithSc =
                        ReservationWithSportCenter(reservationItem, courtWithSc)
                    result.add(reservationWithSc)
                }
            }
        }
        return result
    }

    suspend fun getReservationServicesByUserId(userEmail: String): List<ReservationWithServices> {
        val result: MutableList<ReservationWithServices> = mutableListOf()
        val database: FirebaseFirestore = RemoteDataSource.instance
        val reservationsData =
            database.collection("reservations").whereEqualTo("user", userEmail).get().await()

        if (!reservationsData.isEmpty) {
            for (reservation in reservationsData.documents) {
//                val reservDate: String = reservation.data?.get("date") as String
//                val request: String? = reservation.data?.get("request") as String?
//                val timeslotId: Long = reservation.data?.get("timeslot") as Long
//                val courtId: String = reservation.data?.get("courtId") as String
//                val reservationItem = Reservation(
//                    reservDate,
//                    timeslotId,
//                    userEmail,
//                    courtId,
//                    request,
//                    reservation.id
//                )
                val reservationItem = DbUtils.getReservation(reservation)
//                val services = reservation.data?.get("services") as ArrayList<*>
//                val serviceList = ServiceUtils.getServices(services)
                val serviceList = DbUtils.getServices(reservation)

                val reservWithServices =
                    ReservationWithServices(reservationItem, serviceList)
                result.add(reservWithServices)
            }
        }

        return result
    }

    suspend fun getReservationServicesFromInvites(invites: List<Invite>): List<ReservationWithServices> {
        val result: MutableList<ReservationWithServices> = mutableListOf()

        for (invite in invites) {
            val reservation =
                database.collection("reservations").document(invite.reservationId).get().await()
//            val reservDate: String = reservation.data?.get("date") as String
//            val request: String? = reservation.data?.get("request") as String?
//            val timeslotId: Long = reservation.data?.get("timeslot") as Long
//            val courtId: String = reservation.data?.get("courtId") as String
//            val reservationItem = Reservation(
//                reservDate,
//                timeslotId,
//                invite.inviter,
//                courtId,
//                request,
//                reservation.id,
//                true,
//            )
            val reservationItem = DbUtils.getReservation(reservation)
            reservationItem.isGuest=true
//            val services = reservation.data?.get("services") as ArrayList<*>
//            val serviceList = ServiceUtils.getServices(services)
            val serviceList = DbUtils.getServices(reservation)
            val reservWithServices =
                ReservationWithServices(reservationItem, serviceList)
            result.add(reservWithServices)
        }
        return result
    }

    suspend fun getReservationsReviewsByUserId(userEmail: String): List<ReservationWithReview> {
        val result: MutableList<ReservationWithReview> = mutableListOf()
        val database: FirebaseFirestore = RemoteDataSource.instance
        val reservationsData =
            database.collection("reservations").whereEqualTo("user", userEmail).get().await()

        if (!reservationsData.isEmpty) {
            for (reservation in reservationsData.documents) {
//                val reservDate: String = reservation.data?.get("date") as String
//                val request: String? = reservation.data?.get("request") as String?
//                val timeslotId: Long = reservation.data?.get("timeslot") as Long
//                val courtId: String = reservation.data?.get("courtId") as String
                var review: Review? = null
//                val reservationItem = Reservation(
//                    reservDate,
//                    timeslotId,
//                    userEmail,
//                    courtId,
//                    request,
//                    reservation.id
//                )
                val reservationItem = DbUtils.getReservation(reservation)
                if (reservation.contains("rating")) {
                    review = DbUtils.getReview(reservation)
//                    val reviewMap = reservation.data?.get("review") as Map<*, *>
//                    val rating = reviewMap["rating"] as Int
//                    val reviewDate = reviewMap["date"] as String
//                    val reviewText = reviewMap["text"] as String
//                    val reviewId = reviewMap["id"] as String
//                    review = Review(
//                        courtId,
//                        userEmail,
//                        reservation.id,
//                        reviewText,
//                        rating,
//                        reviewDate,
//                        reviewId
//                    )
                }
                val reservWithReview = ReservationWithReview(reservationItem, review)
                result.add(reservWithReview)
            }

        }
        return result
    }

    suspend fun getReservationReviewsFromInvites(invites: List<Invite>): List<ReservationWithReview> {
        val result: MutableList<ReservationWithReview> = mutableListOf()

        for (invite in invites) {
            val reservation =
                database.collection("reservations").document(invite.reservationId).get().await()
//            val reservDate: String = reservation.data?.get("date") as String
//            val request: String? = reservation.data?.get("request") as String?
//            val timeslotId: Long = reservation.data?.get("timeslot") as Long
//            val courtId: String = reservation.data?.get("courtId") as String
            var review: Review? = null
//            val reservationItem = Reservation(
//                reservDate,
//                timeslotId,
//                invite.inviter,
//                courtId,
//                request,
//                reservation.id,
//                true,
//            )
            val reservationItem = DbUtils.getReservation(reservation)
            reservationItem.isGuest=true
            if (reservation.contains("rating")) {
                review = DbUtils.getReview(reservation)
//                val reviewMap = reservation.data?.get("review") as Map<*, *>
//                val rating = reviewMap["rating"] as Int
//                val reviewDate = reviewMap["date"] as String
//                val reviewText = reviewMap["text"] as String
//                val reviewId = reviewMap["id"] as String
//                review = Review(
//                    courtId,
//                    invite.inviter,
//                    reservation.id,
//                    reviewText,
//                    rating,
//                    reviewDate,
//                    reviewId
//                )
            }
            val reservWithReview = ReservationWithReview(reservationItem, review)
            result.add(reservWithReview)
        }
        return result
    }
}
