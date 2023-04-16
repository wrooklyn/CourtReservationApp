package it.polito.mad.courtreservationapp.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sportCenters")
data class SportCenter(
    @PrimaryKey(autoGenerate = true)
    val centerId: Int,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "address")
    val address: String

    )