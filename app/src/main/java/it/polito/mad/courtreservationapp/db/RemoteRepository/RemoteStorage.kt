package it.polito.mad.courtreservationapp.db.RemoteRepository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import it.polito.mad.courtreservationapp.db.RemoteDataSource
import it.polito.mad.courtreservationapp.db.relationships.CourtWithServices
import it.polito.mad.courtreservationapp.db.relationships.SportCenterWithCourtsAndServices
import it.polito.mad.courtreservationapp.models.Court
import it.polito.mad.courtreservationapp.models.Service
import it.polito.mad.courtreservationapp.models.SportCenter

object RemoteStorage {
    val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    fun getAllSportCenters(liveData : MutableLiveData<List<SportCenter>>): ListenerRegistration {
        // using the listener
        return db.collection("sport-centers").addSnapshotListener { r, e ->
            liveData.value = if (e != null || r == null)
                emptyList()
            else r.mapNotNull { d ->
                val scName: String = d.data?.get("name") as String
                val scAddress = d.data?.get("address") as String
                val scDescription = d.data?.get("description") as String
                val scId = d.data?.get("id") as Long
                SportCenter(scName, scAddress, scDescription, scId)
            }
        }
    }

}