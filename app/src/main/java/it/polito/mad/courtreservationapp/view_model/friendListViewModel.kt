package it.polito.mad.courtreservationapp.view_model

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentSnapshot
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
        .document(SavedPreference.EMAIL)
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
        RemoteDataSource.instance.collection("users").document(id).collection("friend_list").document(SavedPreference.EMAIL).set(fields)
    }

    fun declineFriend(id : String){
        val friendsCollectionRef= RemoteDataSource.instance
            .collection("users")
            .document(SavedPreference.EMAIL)
            .collection("friend_list")
        friendsCollectionRef.document(id).delete()
    }

    fun addNewFriend(email:String){
        val usersCollectionRef= RemoteDataSource.instance
            .collection("users")

        var found = false
        var newFriend = true
        val friendRef = usersCollectionRef.document(SavedPreference.EMAIL).collection("friend_list").document(email)
        var friend: DocumentSnapshot
        var alreadyFriend = false
        var requestSent = false
        var userNotFound = false
        var successful = false

        runBlocking(Dispatchers.Default) {
            launch {
                val users = usersCollectionRef.get().await().documents
                friend = friendRef.get().await()
                for(user in users){
                    if(user.id == email) {
                        found = true
                    }
                }


                if (friend.exists() && !(friend.get("accepted") as Boolean)){ // if document of new friend is present && accepted = false
                    newFriend = false
                }else if(!found){
                    userNotFound = true

                }else if(friend.exists() && friend.get("accepted") as Boolean){
                    alreadyFriend = true
                }

                if(found && newFriend){
                    val newFriendRef = RemoteDataSource.instance
                        .collection("users")
                        .document(email)
                        .collection("friend_list")
                        .document(SavedPreference.EMAIL)
                    val data = hashMapOf(
                        "accepted" to false,
                    )
                    newFriendRef.set(data)
                    //Toast.makeText(getApplication(), "Friend request sent", Toast.LENGTH_LONG).show()
                    successful = true
                }
                if(found && successful && !friend.exists()){ // if accepted = false --> true
                    requestSent = true
                }
                Log.d("hello1", successful.toString())
                Log.d("hello2", friend.exists().toString())
                Log.d("hello3", requestSent.toString())

            }
        }

        if(userNotFound){
            Toast.makeText(getApplication(), "Unable to find a player with that name", Toast.LENGTH_LONG).show()
        }else if(!userNotFound && requestSent){
            Toast.makeText(getApplication(), "Friend request already sent", Toast.LENGTH_LONG).show()
        }else if(!userNotFound && !requestSent && alreadyFriend){
            Toast.makeText(getApplication(), "Already friends", Toast.LENGTH_LONG).show()
        }

    }

    override fun onCleared() {
        super.onCleared(); l.remove(); }

}
