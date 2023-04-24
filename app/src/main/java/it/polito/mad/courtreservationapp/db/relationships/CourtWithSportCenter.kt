package it.polito.mad.courtreservationapp.db.relationships

import androidx.room.Embedded
import androidx.room.Relation
import it.polito.mad.courtreservationapp.models.Court
import it.polito.mad.courtreservationapp.models.SportCenter

data class CourtWithSportCenter(
    @Embedded val court: Court,
    @Relation(
        parentColumn = "courtCenterId",
        entityColumn = "centerId"
    )
    val sportCenter: SportCenter
)