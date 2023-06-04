package it.polito.mad.courtreservationapp.db.remoteRepository

import android.app.Application
import androidx.room.util.query
import com.google.firebase.firestore.FirebaseFirestore
import it.polito.mad.courtreservationapp.db.RemoteDataSource
import it.polito.mad.courtreservationapp.models.*
import it.polito.mad.courtreservationapp.utils.DbUtils
import kotlinx.coroutines.tasks.await

class FireInviteRepository(val application: Application) {

    private val db: FirebaseFirestore = RemoteDataSource.instance

    suspend fun getPendingSentByUserId(userId: String): List<Invite> {
        val result: MutableList<Invite> = mutableListOf()
        val invitesSentCollection =
            db.collection("users").document(userId).collection("invites_sent")
                .whereEqualTo("state", "pending").get().await()
        for (invite in invitesSentCollection.documents) {
//            val reservationId = invite.data?.get("reference_id") as String
//            val status = Status.PENDING
//            val inviter = invite.data?.get("inviter") as String
//            val inviteItem = Invite(reservationId, status, inviter)
            val inviteItem = DbUtils.getInvite(invite)
            result.add(inviteItem)
        }
        return result
    }

    suspend fun getAcceptedReceivedByUserId(userId: String): List<Invite> {
        val result: MutableList<Invite> = mutableListOf()
        val invitesSentCollection =
            db.collection("users").document(userId).collection("invites_received")
                .whereEqualTo("status", Status.ACCEPTED.name).get().await()
        for (invite in invitesSentCollection.documents) {
//            val reservationId = invite.data?.get("reservationId") as String
//            val status = Status.PENDING
//            val inviter = invite.data?.get("inviter") as String
//            val inviteItem = Invite(reservationId, status, inviter)
            val inviteItem = DbUtils.getInvite(invite)
            result.add(inviteItem)
        }
        return result
    }

    suspend fun getPendingReceivedByUserId(userId: String): List<Invite> {
        val result: MutableList<Invite> = mutableListOf()
        val invitesSentCollection =
            db.collection("users").document(userId).collection("invites_received")
                .whereEqualTo("status", Status.PENDING.name).get().await()
        for (invite in invitesSentCollection.documents) {
//            val reservationId = invite.data?.get("reservationId") as String
//            val status = Status.PENDING
//            val inviter = invite.data?.get("inviter") as String
            val inviteItem = DbUtils.getInvite(invite)

            val reservationData =
                db.collection("reservations").document(inviteItem.reservationId).get().await()
            val courtId = reservationData.data?.get("courtId") as String
            val date = reservationData.data?.get("date") as String
            val timeslot = reservationData.data?.get("timeslot") as Long
            val sportCenterId = reservationData.data?.get("sportCenterId") as String
            val sportCenterData =
                db.collection("sport-centers").document(sportCenterId).get().await()
            val address = sportCenterData.data?.get("address") as String
            val name = sportCenterData.data?.get("name") as String
            val courtData =
                db.collection("sport-centers").document(sportCenterId).collection("courts")
                    .document(courtId).get().await()
            val sport = courtData.data?.get("sport_name") as String

            val additionalInfo = AdditionalInfo(date, timeslot, sport, name, address)
//            val inviteItem = Invite(reservationId, status, inviter, additionalInfo)
            inviteItem.additionalInfo = additionalInfo
            result.add(inviteItem)
        }
        return result
    }

    suspend fun getParticipantsByReservationId(reservationId: String): List<String> {
        val result: MutableList<String> = mutableListOf()
        val reservationSnapshot =
            db.collection("reservations").document(reservationId).get().await()
        if (reservationSnapshot.exists()) {
            if (reservationSnapshot.contains("participants")) {
                val participants = reservationSnapshot.get("participants") as? ArrayList<*>
                if (participants != null) {
                    for (participant in participants) {
                        result.add(participant as String)
                    }
                }
            } else {
                println("Participants not exists")
            }
        }
        return result
    }

    suspend fun getFriendsByEmail(inviterEmail: String): List<String> {
        val result: MutableList<String> = mutableListOf()
        val friendsCollection =
            db.collection("users").document(inviterEmail).collection("friend_list")
                .whereEqualTo("accepted", "true").get().await()
        for (friend in friendsCollection.documents) {
            val email = friend.id
            result.add(email)
        }
        return result
    }

    fun inviteUser(reservationId: String, invitedUserEmail: String, inviterEmail: String) {
        val inviteSentItem = Invite(reservationId, Status.PENDING, inviterEmail)
        val inviteReceivedItem = Invite(reservationId, Status.PENDING, inviterEmail)
        val invitesSentCollection =
            db.collection("users").document(inviterEmail).collection("invites_sent")
        val invitesReceivedCollection =
            db.collection("users").document(invitedUserEmail).collection("invites_received")
        invitesSentCollection.add(inviteSentItem)
            .addOnSuccessListener {
                println("Successfully sent invite from $inviterEmail to $invitedUserEmail")
            }
            .addOnFailureListener {
                println("Error sending invite from $inviterEmail to $invitedUserEmail")
            }
        invitesReceivedCollection.add(inviteReceivedItem)
            .addOnSuccessListener {
                println("Successfully received invite from $inviterEmail to $invitedUserEmail")
            }
            .addOnFailureListener {
                println("Error receiving invite from $inviterEmail to $invitedUserEmail")
            }
    }

    /* When accepting an invite need to:
    *  1. set the invite state as ACCEPTED in the invites_sent collection of the inviter
    *  2. set the invite state as ACCEPTED in the invites_received collection of the invited
    *  3. add the invited user to the participants list for the reservation
    * */
    suspend fun acceptInvite(
        reservationId: String,
        invitedUserEmail: String,
        inviterEmail: String,
        onSuccess: () -> Unit
    ) {

        /* Set invite as ACCEPTED in the invites_sent collection of the inviter */
        val inviteSentQueryData = db.collection("users")
            .document(inviterEmail)
            .collection("invites_sent")
            .whereEqualTo("reservationId", reservationId).get().await()

        if (!inviteSentQueryData.isEmpty) {
            val inviteSnapshot = inviteSentQueryData.documents[0]
            val inviteReference = inviteSnapshot.reference
            inviteReference.update("status", Status.ACCEPTED.toString())
        }

        /* Set invite as ACCEPTED in the invites_received collection of the invited user */
        val inviteQueryData = db.collection("users")
            .document(invitedUserEmail)
            .collection("invites_received")
            .whereEqualTo("reservationId", reservationId).get().await()

        if (!inviteQueryData.isEmpty) {
            val inviteSnapshot = inviteQueryData.documents[0]
            val inviteReference = inviteSnapshot.reference
            inviteReference.update("status", Status.ACCEPTED.toString()).await()

            val reservationRef = db.collection("reservations").document(reservationId)
            val reservationData = reservationRef.get().await()
            /* Add the invited users to the participants collection of the reservation */

            val participants = reservationData.get("participants") as MutableList<String>?

            if (participants != null) {
                // "participants" field exists, add new entry
                participants.add(invitedUserEmail)
                reservationRef.update("participants", participants).await()
            } else {
                // "participants" field doesn't exist, create it with a single entry
                val newParticipants = arrayListOf(invitedUserEmail)
                reservationRef.update("participants", newParticipants).await()
            }
        } else {
            println("Invite not found")
        }

        onSuccess()

        return
    }

    suspend fun declineInvite(
        reservationId: String,
        invitedUserEmail: String,
        inviterEmail: String
    ) {
        /* Set invite as REFUSED in the invites_sent collection of the inviter */
        val inviteSentData =
            db.collection("users").document(inviterEmail).collection("invites_sent")
                .whereEqualTo("reservationId", reservationId).get().await()

        if (!inviteSentData.isEmpty) {
            val inviteSnapshot = inviteSentData.documents[0]
            val inviteReference = inviteSnapshot.reference
            inviteReference.update("status", Status.REFUSED.toString())
        }

        /* Set invite as REFUSED in the invites_received collection of the invited user */
        val inviteQueryData =
            db.collection("users").document(invitedUserEmail).collection("invites_received")
                .whereEqualTo("reservationId", reservationId).get().await()

        if (!inviteQueryData.isEmpty) {
            val inviteSnapshot = inviteQueryData.documents[0]
            val inviteReference = inviteSnapshot.reference
            inviteReference.update("status", Status.REFUSED.toString())
                .addOnSuccessListener {
                    println("Invite successfully declined")
                }
                .addOnFailureListener {
                    println("Error accepting invite")
                }
        } else {
            println("Invite not found")
        }

        return
    }
}