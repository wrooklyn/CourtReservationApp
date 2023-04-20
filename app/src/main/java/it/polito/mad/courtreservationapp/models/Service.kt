package it.polito.mad.courtreservationapp.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey

@Entity(tableName = "services",
)
data class Service(
    @PrimaryKey(autoGenerate = true)
    val serviceId: Int,
    val description: String,
    val cost: Double
)