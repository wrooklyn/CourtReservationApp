package it.polito.mad.courtreservationapp.db.crossref

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import it.polito.mad.courtreservationapp.models.Court
import it.polito.mad.courtreservationapp.models.Service

@Entity(primaryKeys = ["courtId", "serviceId"],
    foreignKeys = [
        ForeignKey(
            entity = Court::class,
            parentColumns = ["courtId"],
            childColumns = ["courtId"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        ),
        ForeignKey(
            entity = Service::class,
            parentColumns = ["serviceId"],
            childColumns = ["serviceId"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    ]
)
data class CourtServiceCrossRef(
    val courtId: Int,
    val serviceId: Int
)
