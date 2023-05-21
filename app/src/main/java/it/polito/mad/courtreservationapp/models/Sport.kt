package it.polito.mad.courtreservationapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sports")
data class Sport(
    val name: String = "",
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0

)
