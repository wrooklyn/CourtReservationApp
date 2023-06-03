package it.polito.mad.courtreservationapp.utils

import com.google.firebase.firestore.DocumentSnapshot
import it.polito.mad.courtreservationapp.models.Court

object DbUtils {
    fun getCourt(snapshot: DocumentSnapshot, sportCenterId: String): Court{
        val cSportName = snapshot.data?.get("sport_name") as String
        val image: String? = snapshot.data?.get("image_name") as String?
        val cost: Double = snapshot.data?.get("cost") as Double
        return Court(sportCenterId, cSportName, 0, snapshot.id, cost, image)
    }
}