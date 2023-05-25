package it.polito.mad.courtreservationapp.models

enum class Status(stateString: String){
    PENDING("pending"),
    ACCEPTED("accepted"),
    REFUSED("refused")
}

data class Invite(
    val ReservationId: String,
    val status: Status
)