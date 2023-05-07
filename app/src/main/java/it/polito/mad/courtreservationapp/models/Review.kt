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
    val reviewCourtId: Long,
    val reviewUserId: Long,
    val reviewReservationId: Long,
    val text: String?,
    val rating: Int,
    val reviewDate: String,
    @PrimaryKey(autoGenerate = true)
    val reviewId : Long = 0
)
