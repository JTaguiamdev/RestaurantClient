package com.orderly.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orderly.data.Result
import com.orderly.data.TokenManager
import com.orderly.data.dto.LoginDTO
import com.orderly.data.dto.LoginResponse
import com.orderly.data.dto.NewUserDTO
import com.orderly.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> = _loginResult

    private val _registrationResult = MutableLiveData<Result<LoginResponse>>()
    val registrationResult: LiveData<Result<LoginResponse>> = _registrationResult

    fun login(loginDto: LoginDTO) {
        viewModelScope.launch {
            val result = userRepository.login(loginDto)
            if (result is Result.Success) {
                tokenManager.saveToken(result.data.token)
            }
            _loginResult.postValue(result)
        }
    }

    fun register(newUserDto: NewUserDTO) {
        viewModelScope.launch {
            val result = userRepository.register(newUserDto)
            if (result is Result.Success) {
                tokenManager.saveToken(result.data.token)
            }
            _registrationResult.postValue(result)
        }
    }

    fun isLoggedIn(): Boolean {
        return tokenManager.getToken() != null
    }

    fun logout() {
        tokenManager.deleteToken()
    }
}