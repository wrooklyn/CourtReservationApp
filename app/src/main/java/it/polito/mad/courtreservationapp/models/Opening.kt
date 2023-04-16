package it.polito.mad.courtreservationapp.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "openings",
    foreignKeys = [
        ForeignKey(entity = SportCenter::class,
            parentColumns = ["centerId"],
            childColumns = ["centerId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]

)
data class Opening (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val centerId: Int,
    val openingDay: Int,
    val openingHour: Int,
    val closingHour: Int
)