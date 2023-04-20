package it.polito.mad.courtreservationapp.db.relationships

import androidx.room.Embedded
import androidx.room.Relation
import it.polito.mad.courtreservationapp.models.Court
import it.polito.mad.courtreservationapp.models.Reservation

data class CourtWithReservations(
    @Embedded
    val court: Court,
    @Relation(
        parentColumn = "courtId",
        entityColumn = "reservationCourtId"
    )
    val reservations: List<Reservation>
)
