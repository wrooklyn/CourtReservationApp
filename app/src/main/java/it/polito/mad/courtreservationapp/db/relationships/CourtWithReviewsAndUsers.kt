package it.polito.mad.courtreservationapp.db.relationships

import androidx.room.Embedded
import androidx.room.Relation
import it.polito.mad.courtreservationapp.models.Court
import it.polito.mad.courtreservationapp.models.Review

data class CourtWithReviewsAndUsers(
//    @Embedded
    val court: Court,
    /*@Relation(
        entity = Review::class,
        entityColumn = "reviewCourtId",
        parentColumn = "courtId"
    )*/
    val reviewsWithUser: List<ReviewWithUser>
)
