package it.polito.mad.courtreservationapp.models

import com.google.firebase.firestore.QueryDocumentSnapshot
import it.polito.mad.courtreservationapp.db.RemoteDataSource
import kotlinx.coroutines.tasks.await

data class Friend(
    val username : String,
    val profilePicRef : String = "",
    val accepted : Boolean
){
    companion object {
        suspend fun fromSnapshot(docSnapshot:QueryDocumentSnapshot) : Friend{
            val username = docSnapshot.id
            // val profilePicRef  = docSnapshot.data?.get("username") as String
            val accepted = docSnapshot.data?.get("accepted") as Boolean

            return Friend(username, "",accepted)
        }
    }
}
