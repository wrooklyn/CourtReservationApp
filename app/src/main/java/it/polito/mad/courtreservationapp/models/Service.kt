package it.polito.mad.courtreservationapp.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey


data class Service(
    val description: String = "",
    var serviceId: Long = 0
)
//TODO: add notification service icon