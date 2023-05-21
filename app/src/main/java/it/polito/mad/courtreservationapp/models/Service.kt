package it.polito.mad.courtreservationapp.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey

@Entity(tableName = "services",
)
data class Service(
    val description: String = "",

    @PrimaryKey(autoGenerate = true)
    var serviceId: Long = 0
)