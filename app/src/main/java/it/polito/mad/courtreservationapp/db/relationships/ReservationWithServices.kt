package it.polito.mad.courtreservationapp.db.relationships

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import it.polito.mad.courtreservationapp.db.crossref.ReservationServiceCrossRef
import it.polito.mad.courtreservationapp.models.Reservation
import it.polito.mad.courtreservationapp.models.Service

data class ReservationWithServices(
    @Embedded
    val reservation: Reservation,
    @Relation(
        parentColumn = "reservationId",
        entityColumn = "serviceId",
        associateBy = Junction(ReservationServiceCrossRef::class)
    )
    val services: List<Service>
)
