package it.polito.mad.courtreservationapp.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey

@Entity(tableName = "services",
    foreignKeys = [
        ForeignKey(entity = Court::class,
            parentColumns = ["courtId"],
            childColumns = ["serviceCourtId"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        ),
        ForeignKey(entity = SportCenter::class,
            parentColumns = ["centerId"],
            childColumns = ["serviceCenterId"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    ]
)
data class Service(
    @PrimaryKey(autoGenerate = true)
    val serviceId: Int,
    val description: String,
    val cost: Double,

    val serviceCourtId: Int,
    val serviceCenterId: Int
)