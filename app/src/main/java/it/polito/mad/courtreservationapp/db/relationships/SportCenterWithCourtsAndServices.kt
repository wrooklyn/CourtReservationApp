package it.polito.mad.courtreservationapp.db.relationships

import androidx.room.Embedded
import androidx.room.Relation
import it.polito.mad.courtreservationapp.models.Court
import it.polito.mad.courtreservationapp.models.SportCenter


data class SportCenterWithCourtsAndServices(
    @Embedded
    val sportCenter: SportCenter,
    @Relation(
        entity = Court::class,
        parentColumn = "centerId",
        entityColumn = "courtCenterId"
    )
    val courtsWithServices: List<CourtWithServices>
)
