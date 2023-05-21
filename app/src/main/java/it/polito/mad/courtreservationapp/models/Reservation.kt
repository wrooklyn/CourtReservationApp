package it.polito.mad.courtreservationapp.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey

@Entity(tableName = "reservations",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["reservationUserId"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        ),
        ForeignKey(
            entity = Court::class,
            parentColumns = ["courtId"],
            childColumns = ["reservationCourtId"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    ]
)
data class Reservation(
    val reservationDate: String = "",
    val timeSlotId: Long = 0L,

    val reservationUserId: Long = 0L,
    val reservationCourtId: Long = 0L,

    val request: String? = null,

    @PrimaryKey(autoGenerate = true)
    var reservationId: Long = 0,
)

//there will be a mapping of time slots in the form:
/*
0 -> 10-11 am
1 -> 11-12 am
2 -> 12-13 pm
3 -> 13-14 pm
4 -> 14-15 pm
5 -> 15-16 pm
6 -> 16-17 pm
7 -> 17-18 pm
 */
