package it.polito.mad.courtreservationapp.db.relationships

import androidx.room.Embedded
import androidx.room.Relation
import it.polito.mad.courtreservationapp.models.Court
import it.polito.mad.courtreservationapp.models.SportCenter

data class SportCenterWithCourtsAndReviews(
    @Embedded
    val sportCenter: SportCenter,
    @Relation(
        entity = Court::class,
        entityColumn = "courtCenterId",
        parentColumn = "centerId"
    )
    val courtsWithReviews: List<CourtWithReviews>
)
