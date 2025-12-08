package com.orderly.ui.user

import androidx.lifecycle.ViewModel
import com.orderly.data.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val tokenManager: TokenManager
) : ViewModel() {

    fun logout() {
        tokenManager.deleteToken()
    }
}