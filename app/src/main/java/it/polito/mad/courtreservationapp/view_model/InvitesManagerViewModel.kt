package it.polito.mad.courtreservationapp.view_model

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import it.polito.mad.courtreservationapp.db.remoteRepository.FireInviteRepository
import it.polito.mad.courtreservationapp.models.Invite
import it.polito.mad.courtreservationapp.models.User
import kotlinx.coroutines.launch

class InvitesManagerViewModel(application: Application): AndroidViewModel(application) {
    private val inviteRepository: FireInviteRepository = FireInviteRepository(application)

    private val _pendingSentInvites = MutableLiveData<List<Invite>>()
    val pendingSentInvites: LiveData<List<Invite>> = _pendingSentInvites

    private val _pendingReceivedInvites = MutableLiveData<List<Invite>>()
    val pendingReceivedInvites: LiveData<List<Invite>> = _pendingReceivedInvites

    private val _participants = MutableLiveData<List<String>>()
    val participants: LiveData<List<String>> = _participants

    fun getPendingSent(userId: String) {
        viewModelScope.launch {
            val invitesSent = inviteRepository.getPendingSentByUserId(userId)
            _pendingSentInvites.postValue(invitesSent)
        }
    }

    fun getPendingReceived(userId: String) {
        viewModelScope.launch {
            val invitesReceived = inviteRepository.getPendingReceivedByUserId(userId)
            _pendingReceivedInvites.postValue(invitesReceived)
        }
    }

    fun getParticipantsByReservationId(reservationId: String) {
        viewModelScope.launch{
            val participants = inviteRepository.getParticipantsByReservationId(reservationId)
            _participants.postValue(participants)
        }
    }

    fun inviteUser(reservationId: String, invitedUserEmail: String, inviterEmail: String) {
        inviteRepository.inviteUser(reservationId, invitedUserEmail, inviterEmail)
    }

    fun acceptInvite(reservationId: String, invitedUserEmail: String, inviterEmail: String) {
        inviteRepository.acceptInvite(reservationId, invitedUserEmail, inviterEmail)
    }

    fun declineInvite(reservationId: String, invitedUserEmail: String, inviterEmail: String) {
        inviteRepository.declineInvite(reservationId, invitedUserEmail, inviterEmail)
    }
}