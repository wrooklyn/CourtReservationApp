package it.polito.mad.courtreservationapp.db.remoteRepository

import android.app.Application
import com.google.firebase.firestore.FirebaseFirestore
import it.polito.mad.courtreservationapp.db.RemoteDataSource
import it.polito.mad.courtreservationapp.models.Invite
import it.polito.mad.courtreservationapp.models.State
import kotlinx.coroutines.tasks.await

class FireInviteRepository(val application: Application) {

    private val db: FirebaseFirestore = RemoteDataSource.instance

    suspend fun getPendingSentByUserId(userId: String): List<Invite> {
        val result: MutableList<Invite> = mutableListOf()
        val invitesSentCollection = db.collection("users").document(userId).collection("invites_sent").whereEqualTo("state", "pending").get().await()
        for(invite in invitesSentCollection.documents) {
            val reservationId = invite.data?.get("reference_id") as String
            val state = State.PENDING
            val inviteItem = Invite(reservationId, state)
            result.add(inviteItem)
        }
        return result
    }

    suspend fun getPendingReceivedByUserId(userId: String): List<Invite> {
        val result: MutableList<Invite> = mutableListOf()
        val invitesSentCollection = db.collection("users").document(userId).collection("invites_received").whereEqualTo("state", "pending").get().await()
        for(invite in invitesSentCollection.documents) {
            val reservationId = invite.data?.get("reference_id") as String
            val state = State.PENDING
            val inviteItem = Invite(reservationId, state)
            result.add(inviteItem)
        }
        return result
    }

    suspend fun getParticipantsByReservationId(reservationId: String): List<String> {
        val result: MutableList<String> = mutableListOf()
        val participantsDocument = db.collection("reservations").document(reservationId).collection("participants").get().await()
        for(participant in participantsDocument.documents) {
            val participantEmail = participant.data?.get("email") as String
            result.add(participantEmail)
        }
        return result
    }

    /* When accepting an invite need to:
    *  1. set the invite state as ACCEPTED
    *  2. add the user to the participants list for the reservation */
    fun acceptInvite(reservationId: String, invitedUserEmail: String) {
        val inviteReference = db.collection("users").document(invitedUserEmail).collection("invites_received").document(reservationId)
        inviteReference.update("status", State.ACCEPTED.toString())
            .addOnSuccessListener {
                println("Invite accepted successfully")

                val reservationRef = db.collection("reservations").document(reservationId)
                reservationRef.get().addOnSuccessListener { reservationSnapshot ->
                    val participants = reservationSnapshot.get("participants") as MutableList<String>?

                    if (participants != null) {
                        // "participants" field exists, add new entry
                        participants.add(invitedUserEmail)
                        reservationRef.update("participants", participants)
                            .addOnSuccessListener {
                                println("Participant added to the reservation")
                            }
                            .addOnFailureListener {
                                println("Error adding participant to the reservation")
                            }
                    } else {
                        // "participants" field doesn't exist, create it with a single entry
                        val newParticipants = arrayListOf(invitedUserEmail)
                        reservationRef.update("participants", newParticipants)
                            .addOnSuccessListener {
                                println("Participant added to the reservation")
                            }
                            .addOnFailureListener {
                                println("Error adding participant to the reservation")
                            }
                    }
                }.addOnFailureListener {
                    println("Error retrieving reservation document")
                }
            }
            .addOnFailureListener {
                println("Error accepting invite")
            }
        return
    }

    fun declineInvite(reservationId: String, invitedUserEmail: String) {
        val inviteReference = db.collection("users").document(invitedUserEmail).collection("invites_received").document(reservationId)
        inviteReference.update("status", State.REFUSED.toString())
            .addOnSuccessListener {
                println("Invite declined successfully")
            }
            .addOnFailureListener{
                println("Invite declined successfully")
            }
        return
    }


}