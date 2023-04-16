package it.polito.mad.courtreservationapp.models

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(tableName = "services",
    primaryKeys = ["centerId", "courtId", "serviceId"],
    foreignKeys = [
        ForeignKey(entity = Court::class,
            parentColumns = ["centerId", "courtId"],
            childColumns = ["centerId", "courtId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class Service(
    val centerId: Int,
    val courtId: Int,
    val serviceId: Int,
    val description: String,
    val cost: Float
)