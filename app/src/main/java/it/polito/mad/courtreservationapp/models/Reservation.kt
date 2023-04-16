package it.polito.mad.courtreservationapp.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "reservations",
    foreignKeys = [
        ForeignKey(entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(entity = Court::class,
            parentColumns = ["centerId", "courtId"],
            childColumns = ["centerId", "courtId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(entity = Request::class,
            parentColumns = ["requestId"],
            childColumns = ["requestId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class Reservation(
    @PrimaryKey(autoGenerate = true)
    val reservationId: Int,

    val userId: Int,

    val centerId: Int,

    val courtId: Int,

    val reservationDate: Date,

    val timeSlotId: Int,

    val requestId: Int

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
