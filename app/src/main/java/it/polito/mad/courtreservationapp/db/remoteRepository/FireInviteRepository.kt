package it.polito.mad.courtreservationapp.db.remoteRepository

import android.app.Application
import com.google.firebase.firestore.FirebaseFirestore
import it.polito.mad.courtreservationapp.db.RemoteDataSource
import it.polito.mad.courtreservationapp.models.Invite
import it.polito.mad.courtreservationapp.models.Status
import kotlinx.coroutines.tasks.await

class FireInviteRepository(val application: Application) {

    private val db: FirebaseFirestore = RemoteDataSource.instance

    suspend fun getPendingSentByUserId(userId: String): List<Invite> {
        val result: MutableList<Invite> = mutableListOf()
        val invitesSentCollection = db.collection("users").document(userId).collection("invites_sent").whereEqualTo("state", "pending").get().await()
        for(invite in invitesSentCollection.documents) {
            val reservationId = invite.data?.get("reference_id") as String
            val status = Status.PENDING
            val inviteItem = Invite(reservationId, status)
            result.add(inviteItem)
        }
        return result
    }

    suspend fun getPendingReceivedByUserId(userId: String): List<Invite> {
        val result: MutableList<Invite> = mutableListOf()
        val invitesSentCollection = db.collection("users").document(userId).collection("invites_received").whereEqualTo("state", "pending").get().await()
        for(invite in invitesSentCollection.documents) {
            val reservationId = invite.data?.get("reference_id") as String
            val status = Status.PENDING
            val inviteItem = Invite(reservationId, status)
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

    fun inviteUser(reservationId: String, invitedUserEmail: String, inviterEmail: String) {
        val inviteSentItem = Invite(reservationId, Status.PENDING)
        val inviteReceivedItem = Invite(reservationId, Status.PENDING)
        val invitesSentCollection = db.collection("users").document(inviterEmail).collection("invites_sent")
        val invitesReceivedCollection = db.collection("users").document(invitedUserEmail).collection("invites_received")
        invitesSentCollection.add(inviteSentItem)
            .addOnSuccessListener {
                println("Successfully sent invite from $inviterEmail to $invitedUserEmail")
            }
            .addOnFailureListener{
                println("Error sending invite from $inviterEmail to $invitedUserEmail")
            }
        invitesReceivedCollection.add(inviteReceivedItem)
            .addOnSuccessListener {
                println("Successfully received invite from $inviterEmail to $invitedUserEmail")
            }
            .addOnFailureListener{
                println("Error receiving invite from $inviterEmail to $invitedUserEmail")
            }
    }

    /* When accepting an invite need to:
    *  1. set the invite state as ACCEPTED
    *  2. add the user to the participants list for the reservation */
    fun acceptInvite(reservationId: String, invitedUserEmail: String) {
        val inviteQuery = db.collection("users").document(invitedUserEmail).collection("invites_received").whereEqualTo("reservationId", reservationId)
        inviteQuery.get()
            .addOnSuccessListener { querySnapshot ->
                if(!querySnapshot.isEmpty) {
                    val inviteSnapshot = querySnapshot.documents[0]
                    val inviteReference = inviteSnapshot.reference
                    inviteReference.update("status", Status.ACCEPTED.toString())
                        .addOnSuccessListener {
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

                } else {
                    println("Invite not found")
                }
            }
        return
    }

    fun declineInvite(reservationId: String, invitedUserEmail: String) {
        val inviteReference = db.collection("users").document(invitedUserEmail).collection("invites_received").document(reservationId)
        inviteReference.update("status", Status.REFUSED.toString())
            .addOnSuccessListener {
                println("Invite declined successfully")
            }
            .addOnFailureListener{
                println("Invite declined successfully")
            }
        return
    }


}