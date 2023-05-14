package it.polito.mad.courtreservationapp.db.relationships

import androidx.room.Embedded
import androidx.room.Relation
import it.polito.mad.courtreservationapp.models.Sport
import it.polito.mad.courtreservationapp.models.SportMastery

data class SportMasteryWithName(
    @Embedded
    val sportMastery: SportMastery,
    @Relation(
        entity = Sport::class,
        entityColumn = "id",
        parentColumn = "sportId"
    )
    val sport: Sport
)
