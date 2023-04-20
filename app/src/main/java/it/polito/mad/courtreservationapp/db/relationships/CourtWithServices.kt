package it.polito.mad.courtreservationapp.db.relationships

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import it.polito.mad.courtreservationapp.db.crossref.CourtServiceCrossRef
import it.polito.mad.courtreservationapp.models.Court
import it.polito.mad.courtreservationapp.models.Service

data class CourtWithServices(
    @Embedded
    val court: Court,
    @Relation(
        parentColumn = "courtId",
        entityColumn = "serviceId",
        associateBy = Junction(CourtServiceCrossRef::class)
    )
    val services: List<Service>
)
