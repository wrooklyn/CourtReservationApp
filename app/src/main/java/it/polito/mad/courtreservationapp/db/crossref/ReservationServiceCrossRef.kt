package it.polito.mad.courtreservationapp.db.crossref

import androidx.room.Entity
import androidx.room.ForeignKey
import it.polito.mad.courtreservationapp.models.Reservation
import it.polito.mad.courtreservationapp.models.Service

@Entity(primaryKeys = ["reservationId", "serviceId"],
    foreignKeys = [
        ForeignKey(
            entity = Reservation::class,
            parentColumns = ["reservationId"],
            childColumns = ["reservationId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Service::class,
            parentColumns = ["serviceId"],
            childColumns = ["serviceId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class ReservationServiceCrossRef(
    val reservationId: Long,
    val serviceId: Long
)
