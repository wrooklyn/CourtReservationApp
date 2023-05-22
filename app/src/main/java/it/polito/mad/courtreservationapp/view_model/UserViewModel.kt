package it.polito.mad.courtreservationapp.view_model

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import it.polito.mad.courtreservationapp.db.relationships.UserWithSportMasteriesAndName
import it.polito.mad.courtreservationapp.db.repository.FireUserRepository
import it.polito.mad.courtreservationapp.models.User
import it.polito.mad.courtreservationapp.views.MainActivity
import it.polito.mad.courtreservationapp.views.login.SavedPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class UserViewModel(application: Application): AndroidViewModel(application) {
    private val userRepo: FireUserRepository = FireUserRepository(application)
    var userWithSportMasteriesAndNameLiveData: MutableLiveData<UserWithSportMasteriesAndName> = MutableLiveData()
    var userLiveData: MutableLiveData<User> = MutableLiveData()

    lateinit var user: User
    lateinit var userWithSportMasteriesAndName: UserWithSportMasteriesAndName

    lateinit var context: MainActivity



    fun setCurrentUser(email: String) {
        println("setUser $email")
        runBlocking(Dispatchers.Default) {
            launch {
                val res = userRepo.getUserWithMasteries(email)
                println("$res")
                userWithSportMasteriesAndNameLiveData.postValue(res)
                userLiveData.postValue(res.user)
                println("updated: hehe ${res}")
            }
        }

    }
    fun refreshUser(context: Context) {
        println("refresh")
        runBlocking(Dispatchers.Default) {
            launch {
                val res = userRepo.getUserWithMasteries(SavedPreference.getEmail(context))
                userWithSportMasteriesAndNameLiveData.postValue(res)
            }
        }

    }

    fun updateUser(u: User) {
        viewModelScope.launch{
            Log.i("updateUser", "$u")
            userRepo.updateUser(u)
        }
    }


}