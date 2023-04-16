package it.polito.mad.courtreservationapp.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val userId: String,

    @ColumnInfo(name = "username")
    val username: String,

    @ColumnInfo(name = "firstname")
    val firstName: String,

    @ColumnInfo(name = "lastname")
    val lastName: String,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "address")
    val address: String,

    @ColumnInfo(name = "gender")
    val gender: Int,

    @ColumnInfo(name = "height")
    val height: Int,

    @ColumnInfo(name = "weight")
    val weight: Float,

    @ColumnInfo(name = "phone")
    val phone: String
)