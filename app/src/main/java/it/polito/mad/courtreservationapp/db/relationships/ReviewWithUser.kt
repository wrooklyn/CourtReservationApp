package it.polito.mad.courtreservationapp.db.relationships

import androidx.room.Embedded
import androidx.room.Relation
import it.polito.mad.courtreservationapp.models.Review
import it.polito.mad.courtreservationapp.models.User

data class ReviewWithUser(
    @Embedded
    val review: Review,
    @Relation(
        entity = User::class,
        entityColumn = "userId",
        parentColumn = "reviewUserId"
    )
    val user: User
)
