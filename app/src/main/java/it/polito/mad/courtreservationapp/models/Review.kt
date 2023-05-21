package it.polito.mad.courtreservationapp.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey

@Entity(tableName = "reviews",
    foreignKeys = [
        ForeignKey(
            entity = Court::class,
            parentColumns = ["courtId"],
            childColumns = ["reviewCourtId"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["reviewUserId"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        ),
        ForeignKey(
            entity = Reservation::class,
            parentColumns = ["reservationId"],
            childColumns = ["reviewReservationId"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    ]
)
data class Review(
    val reviewCourtId: Long = 0L,
    val reviewUserId: Long = 0L,
    val reviewReservationId: Long = 0L,
    val text: String? = null,
    val rating: Int = 0,
    val reviewDate: String = "",
    @PrimaryKey(autoGenerate = true)
    val reviewId : Long = 0
)
