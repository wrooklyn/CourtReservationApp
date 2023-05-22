package it.polito.mad.courtreservationapp.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey


data class Review(
    val reviewCourtId: String?,
    val reviewUserId: String?,
    val reviewReservationId: String?,
    val text: String? = null,
    val rating: Int = 0,
    val reviewDate: String = "",
    val reviewId : String
)
