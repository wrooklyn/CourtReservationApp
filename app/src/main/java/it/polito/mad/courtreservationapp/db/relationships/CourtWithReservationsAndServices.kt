package it.polito.mad.courtreservationapp.db.relationships

import androidx.room.Embedded
import androidx.room.Relation
import it.polito.mad.courtreservationapp.models.Court
import it.polito.mad.courtreservationapp.models.Reservation

data class CourtWithReservationsAndServices(
    @Embedded
    val court: Court,
    @Relation(
        entity = Reservation::class,
        parentColumn = "courtId",
        entityColumn = "reservationCourtId"
    )
    val reservationWithServices: List<ReservationWithServices>
)
