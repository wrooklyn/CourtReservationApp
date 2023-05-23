package it.polito.mad.courtreservationapp.models

enum class State(stateString: String){
    PENDING("pending"),
    ACCEPTED("accepted"),
    REFUSED("refused")
}

data class Invite(
    val ReservationId: String,
    val state: State
)