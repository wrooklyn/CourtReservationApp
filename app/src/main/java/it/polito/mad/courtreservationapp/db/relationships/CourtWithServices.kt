package it.polito.mad.courtreservationapp.db.relationships

import androidx.room.Embedded
import androidx.room.Relation
import it.polito.mad.courtreservationapp.models.Court
import it.polito.mad.courtreservationapp.models.Service

data class CourtWithServices(
    @Embedded
    val court: Court,
    @Relation(
        entity = Service::class,
        parentColumn = "courtId",
        entityColumn = "serviceCourtId"
    )
    val services: List<Service>
)
