package com.example.breakkyuapp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.breakkyuapp.repository.AuthRepository
import com.example.breakkyuapp.repository.Resource
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val authRepository: AuthRepository): ViewModel() {

    private val _loginFlow = MutableLiveData<Resource<FirebaseUser>?>(null)
    val loginFlow: LiveData<Resource<FirebaseUser>?> = _loginFlow

    private val _signupFlow = MutableLiveData<Resource<FirebaseUser>?>(null)
    val signupFlow: LiveData<Resource<FirebaseUser>?> = _signupFlow


    val currentUser: FirebaseUser?
        get() = authRepository.currentUser

    init {
        if(authRepository.currentUser != null){
            _loginFlow.value = Resource.Success(authRepository.currentUser!!)
        }
    }

    fun login(email: String, password: String) = viewModelScope.launch {
        _loginFlow.value = Resource.Loading
        val res = authRepository.login(email, password)
        _loginFlow.value = res
    }

    fun signup(name: String, email: String, password: String) = viewModelScope.launch {
        _signupFlow.value = Resource.Loading
        val res = authRepository.signup(name, email, password)
        _signupFlow.value = res
    }

    fun logout() = viewModelScope.launch {
        authRepository.logout()
        _signupFlow.value = null
        _loginFlow.value = null
    }

    fun isLoggedIn() = viewModelScope.launch {
        val res = currentUser
        if(res != null) {
            _signupFlow.value = Resource.Success(res)
            _loginFlow.value = Resource.Success(res)
        } else {
            _signupFlow.value = null
            _loginFlow.value = null
        }
    }
}