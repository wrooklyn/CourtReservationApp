package it.polito.mad.courtreservationapp.db.relationships

import androidx.room.Embedded
import androidx.room.Relation
import it.polito.mad.courtreservationapp.models.SportMastery
import it.polito.mad.courtreservationapp.models.User

data class UserWithSportMasteries(
    @Embedded
    val user: User,
    @Relation(
        entity = SportMastery::class,
        entityColumn = "userId",
        parentColumn = "userId"
    )
    val masteries: List<SportMastery>
)
