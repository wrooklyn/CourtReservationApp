package it.polito.mad.courtreservationapp.db.relationships

import androidx.room.Embedded
import androidx.room.Relation
import it.polito.mad.courtreservationapp.models.Reservation
import it.polito.mad.courtreservationapp.models.User

data class UserWithReservationsAndServices(
    @Embedded
    val user: User,
    @Relation(
        entity = Reservation::class,
        parentColumn = "userId",
        entityColumn = "reservationUserId"
    )
    val reservationWithServices: List<ReservationWithServices>
)
