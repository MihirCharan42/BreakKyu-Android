package com.example.breakkyuapp.viewModels

import android.os.Handler
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.breakkyuapp.models.Break
import com.example.breakkyuapp.repository.BreakRepository
import com.example.breakkyuapp.repository.Resource
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BreakViewModel @Inject constructor(
    private val breakRepository: BreakRepository
) : ViewModel() {

    private val _breakLiveData = MutableLiveData<Resource<Task<QuerySnapshot>>>()
    var breakLiveData: LiveData<Resource<Task<QuerySnapshot>>> = _breakLiveData

    private val _addBreakLiveData = MutableLiveData<Resource<Task<DocumentReference>>>()
    var addBreakLiveData: LiveData<Resource<Task<DocumentReference>>> = _addBreakLiveData

    private val _breakState = MutableLiveData<Boolean>(false)
    val breakState: LiveData<Boolean>
        get() = _breakState

    fun getBreaks() = viewModelScope.launch{
        _breakLiveData.value = Resource.Loading
        val res = breakRepository.getBreak()
        _breakLiveData.value = res
    }

    fun addBreak(firebaseUser: FirebaseUser, fromTime: Timestamp, note: String) = viewModelScope.launch {
        _addBreakLiveData.value = Resource.Loading
        val res = breakRepository.addBreak(firebaseUser, fromTime, note)
        _addBreakLiveData.value = res
    }

    fun checkIfAlreadyOnBreak(firebaseUser: FirebaseUser) = viewModelScope.launch {
        val breaks = breakRepository.getBreak()
        if(breaks is Resource.Success){
            breaks.result.addOnCompleteListener{ querySnapshot ->
                var flag = false
                for (document in querySnapshot.result) {
                    if(document.data.get("userId").toString() == firebaseUser.uid) {
                        _breakState.value = true
                        flag = true
                    }
                }
                if(!flag){
                    _breakState.value = false
                }
            }
        }
    }

    fun deleteBreak(firebaseUser: FirebaseUser, toTime: Timestamp, fromTime: Timestamp, id: String) = viewModelScope.launch {
        _addBreakLiveData.value = Resource.Loading
        val res = breakRepository.deleteBreak(firebaseUser, toTime, fromTime, id)
        _addBreakLiveData.value = res
    }

    fun getBreakId(firebaseUser: FirebaseUser) = viewModelScope.launch {
        val breaks = breakRepository.getBreak()
        if(breaks is Resource.Success){
            breaks.result.addOnCompleteListener{ querySnapshot ->
                var flag = false
                for (document in querySnapshot.result) {
                    if(document.data.get("userId").toString() == firebaseUser.uid) {

                        flag = true
                    }
                }
                if(!flag){
                    _breakState.value = false
                }
            }
        }
    }
}