package it.polito.mad.courtreservationapp.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import it.polito.mad.courtreservationapp.db.relationships.UserWithSportMasteriesAndName
import it.polito.mad.courtreservationapp.db.repository.FireUserRepository
import it.polito.mad.courtreservationapp.models.User
import kotlinx.coroutines.launch

class UserViewModel(application: Application): AndroidViewModel(application) {
    private val userRepo: FireUserRepository = FireUserRepository(application)

    lateinit var user: LiveData<User>
    lateinit var userWithSportMasteriesAndNameLiveData: LiveData<UserWithSportMasteriesAndName>
    lateinit var userWithSportMasteriesAndName: UserWithSportMasteriesAndName

    fun insertUser(user: User) {
        viewModelScope.launch{
            userRepo.insertUser(user)
        }
    }
    fun setCurrentUser(email: String) {
        user = userRepo.getById(email)
        userWithSportMasteriesAndNameLiveData = userRepo.getUserWithMasteries(email)
    }

    fun updateUser(u: User) {
        viewModelScope.launch{
            userRepo.updateUser(u)
        }
    }
}