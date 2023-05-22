package it.polito.mad.courtreservationapp.view_model

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.ListenerRegistration
import it.polito.mad.courtreservationapp.db.RemoteDataSource
import it.polito.mad.courtreservationapp.models.Friend
import it.polito.mad.courtreservationapp.views.login.SavedPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class FriendListViewModel(application: Application) : AndroidViewModel(application) {
    private val _friendList = MutableLiveData<List<Friend>>()
    val friendList: LiveData<List<Friend>> = _friendList
    private val l: ListenerRegistration = RemoteDataSource.instance
        .collection("users")
        .document(SavedPreference.getEmail(application))
        .collection("friend_list")
        .addSnapshotListener { r, e ->
            runBlocking(Dispatchers.Default) {
                launch {
                    val res = if (e != null)
                    emptyList()
                    else r?.mapNotNull { d -> Friend.fromSnapshot(d) }
                    _friendList.postValue(res?: listOf())
                }
            }

        }

    fun acceptFriend(id : String){
        val friendsCollectionRef= RemoteDataSource.instance
            .collection("users")
            .document(SavedPreference.EMAIL)
            .collection("friend_list")
        val fields : HashMap<String, Any> = hashMapOf("accepted" to true)
        friendsCollectionRef.document(id).update(fields)
    }

    fun declineFriend(id : String){
        val friendsCollectionRef= RemoteDataSource.instance
            .collection("users")
            .document(SavedPreference.EMAIL)
            .collection("friend_list")
        friendsCollectionRef.document(id).delete()
    }

    fun addNewFriend(id:String){
        val usersCollectionRef= RemoteDataSource.instance
            .collection("users")

        var found = false
        var friendEmail=""
        runBlocking(Dispatchers.Default) {
            launch {
                val users = usersCollectionRef.get().await().documents

                for(user in users){
                    val username = user.data?.get("username") as String
                    if(username == id) {
                        found = true
                        friendEmail = user.id
                    }
                }
            }
        }
        if(found){
            val newFriendRef = RemoteDataSource.instance
                .collection("users")
                .document(friendEmail)
                .collection("friend_list")
                .document(SavedPreference.USERNAME)
            val data = hashMapOf(
                "accepted" to false,
            )
            newFriendRef.set(data)
            Toast.makeText(getApplication(), "Friend request sent", Toast.LENGTH_LONG).show()
        }else{
            Toast.makeText(getApplication(), "Unable to find a player with that name", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCleared() {
        super.onCleared(); l.remove(); }

}
