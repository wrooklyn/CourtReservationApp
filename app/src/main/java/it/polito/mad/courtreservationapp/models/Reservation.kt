package it.polito.mad.courtreservationapp.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey

data class Reservation(
    val reservationDate: String = "",
    val timeSlotId: Long = 0L,

    val reservationUserId: String?,
    val reservationCourtId: String?,

    val request: String? = null,
    var reservationId: String,
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
