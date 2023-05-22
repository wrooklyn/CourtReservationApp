package it.polito.mad.courtreservationapp.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey


data class SportMastery(
    val sportId: Long = 0L,
    val userId: String?,
    val level: Int = 0,
    val achievement: String?,
    val id : Long = 0
)
