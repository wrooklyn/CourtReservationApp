package it.polito.mad.courtreservationapp.db.relationships

import androidx.room.Embedded
import androidx.room.Relation
import it.polito.mad.courtreservationapp.models.Court
import it.polito.mad.courtreservationapp.models.Reservation

data class ReservationWithSportCenter(
    @Embedded val reservation: Reservation,
    @Relation(
        parentColumn = "reservationCourtId",
        entityColumn = "courtId",
        entity = Court::class
    )
    val courtWithSportCenter: CourtWithSportCenter
)