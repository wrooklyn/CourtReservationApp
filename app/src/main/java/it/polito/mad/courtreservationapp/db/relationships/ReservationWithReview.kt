package it.polito.mad.courtreservationapp.db.relationships

import androidx.room.Embedded
import androidx.room.Relation
import it.polito.mad.courtreservationapp.models.Reservation
import it.polito.mad.courtreservationapp.models.Review

data class ReservationWithReview(
    @Embedded
    val reservation: Reservation,
    @Relation(
        parentColumn = "reservationId",
        entityColumn = "reviewReservationId",
        entity = Review::class
    )
    val review: Review?
)