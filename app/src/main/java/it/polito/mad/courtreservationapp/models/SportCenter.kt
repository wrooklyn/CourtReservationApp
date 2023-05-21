package it.polito.mad.courtreservationapp.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sportCenters")
data class SportCenter(

    val name: String = "",
    val address: String = "",
    val description: String = "",
    @PrimaryKey(autoGenerate = true)
    var centerId: Long = 0
)