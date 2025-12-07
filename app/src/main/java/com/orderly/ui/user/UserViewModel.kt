package com.orderly.ui.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orderly.data.Result
import com.orderly.data.dto.UserDTO
import com.orderly.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userProfile = MutableLiveData<Result<UserDTO>>()
    val userProfile: LiveData<Result<UserDTO>> = _userProfile

    fun fetchUserProfile(userId: Int) {
        viewModelScope.launch {
            val result = userRepository.getUserById(userId)
            _userProfile.postValue(result)
        }
    }
}