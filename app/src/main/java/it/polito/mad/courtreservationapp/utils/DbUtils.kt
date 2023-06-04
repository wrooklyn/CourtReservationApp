package it.polito.mad.courtreservationapp.utils

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import it.polito.mad.courtreservationapp.models.*

object DbUtils {

    fun getSportCenter(snapshot: DocumentSnapshot): SportCenter {
        val centerName = snapshot.data?.get("name") as String
        val address = snapshot.data?.get("address") as String
        val description = snapshot.data?.get("description") as String
        val centerImage = snapshot.data?.get("image_name") as String?
        val coordinatesString = snapshot.data?.get("coordinates") as String?

        var coordinates:Coordinates? = null
        if(!coordinatesString.isNullOrEmpty()) {
            val coordinatesString = coordinatesString.split(",")
            val latitude = coordinatesString[0].toDouble()
            val longitude = coordinatesString[1].toDouble()
            coordinates = Coordinates(latitude, longitude)
        }



        return SportCenter(centerName, address, description, snapshot.id, centerImage, coordinates)
    }

    fun getCourt(snapshot: DocumentSnapshot, sportCenterId: String): Court {
        val cSportName = snapshot.data?.get("sport_name") as String
        val image: String? = snapshot.data?.get("image_name") as String?
        val cost: Double = snapshot.data?.get("cost") as Double
        return Court(sportCenterId, cSportName, 0, snapshot.id, cost, image)
    }

    fun getReservation(reservationSnapshot: DocumentSnapshot): Reservation {
        val reservDate: String = reservationSnapshot.data?.get("date") as String
        val request: String? = reservationSnapshot.data?.get("request") as String?
        val timeslotId: Long = reservationSnapshot.data?.get("timeslot") as Long
        val courtId: String = reservationSnapshot.data?.get("courtId") as String
        val user: String = reservationSnapshot.data?.get("user") as String

        val participants: MutableList<String> = (reservationSnapshot.data?.get("participants") as MutableList<String>?)?: mutableListOf()
        participants.add(0, user)
        return Reservation(reservDate, timeslotId, user, courtId, request, reservationSnapshot.id, participants=participants)
    }

    fun getServices(documentSnapshot: DocumentSnapshot): List<Service> {
        val servicesIds = documentSnapshot.data?.get("services") as List<*>?
        if (servicesIds.isNullOrEmpty()){
            return emptyList()
        }
        return ServiceUtils.getServices(servicesIds)
    }

    fun getReview(reservationSnapshot: DocumentSnapshot): Review {
        val reservation = getReservation(reservationSnapshot)
        val rating = reservationSnapshot.data?.get("rating") as Long
        val reviewDate = reservationSnapshot.data?.get("review_date") as String
        val reviewText = reservationSnapshot.data?.get("review_content") as String?

        return Review(reservation.reservationCourtId,
                            reservation.reservationUserId,
                            reservation.reservationId,
                            reviewText, rating.toInt(), reviewDate, reservation.reservationId)
    }

    fun getUser(userSnapshot: DocumentSnapshot): User {
        val userName = userSnapshot.get("username") as String
        val address = (userSnapshot.get("address") as String?) ?: ""
        val firstName = (userSnapshot.get("firstName") as String?) ?: ""
        val lastName = (userSnapshot.get("lastName") as String?) ?: ""
        val gender = (userSnapshot.get("gender") as Long?) ?: 0L
        val height = (userSnapshot.get("height") as Long?) ?: 0L
        val weight = (userSnapshot.get("weight") as Long?) ?: 0L
        val phone = (userSnapshot.get("phone") as String?) ?: ""
        return User(userName, firstName, lastName, userSnapshot.id, address, gender.toInt(), height.toInt(), weight.toInt(), phone, userSnapshot.id)
    }

    fun getMastery(masterySnapshot: DocumentSnapshot, email: String): SportMastery {

        val achievements = masterySnapshot.data?.get("achievements")  as ArrayList<String>
        val level = masterySnapshot.data?.get("level") as Long
        val achievementsString =
            if(achievements.isNullOrEmpty()) ""
            else {
                achievements.filter { s -> !s.isNullOrEmpty() }.toString().replace("[", "").replace("]", "")
            }

        return SportMastery(0L, email, level.toInt(), achievementsString)

    }

    fun getInvite(inviteSnapshot: DocumentSnapshot): Invite{
        val reservationId = inviteSnapshot.data?.get("reservationId") as String
        val status = Status.PENDING
        val inviter = inviteSnapshot.data?.get("inviter") as String
        return Invite(reservationId, status, inviter)
    }


}