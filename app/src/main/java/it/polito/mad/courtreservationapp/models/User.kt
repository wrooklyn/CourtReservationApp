package it.polito.mad.courtreservationapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val userId: Int,

    val username: String,

    val firstName: String,

    val lastName: String,

    val email: String,

    val address: String,

    val gender: Int,

    val height: Int,

    val weight: Double,

    val phone: String
)