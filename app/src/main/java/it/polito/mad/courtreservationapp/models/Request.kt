package it.polito.mad.courtreservationapp.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "requests",
    primaryKeys = ["requestId", "userId", "centerId", "courtId", "serviceId"],
    foreignKeys = [
        ForeignKey(entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(entity = Service::class,
            parentColumns = ["centerId", "courtId", "serviceId"],
            childColumns = ["centerId", "courtId", "serviceId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class Request(
    val requestId: Int,
    val userId: Int,
    val centerId: Int,
    val courtId: Int,
    val serviceId: Int,
    val note: String
)