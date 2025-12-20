package com.restaurantclient.ui.admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.restaurantclient.data.Result
import com.restaurantclient.data.dto.RoleDTO
import com.restaurantclient.data.dto.UserDTO
import com.restaurantclient.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserManagementViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _users = MutableLiveData<Result<List<UserDTO>>>()
    val users: LiveData<Result<List<UserDTO>>> = _users

    private val _deleteUserResult = MutableLiveData<Result<Unit>>()
    val deleteUserResult: LiveData<Result<Unit>> = _deleteUserResult

    private val _updateUserResult = MutableLiveData<Result<Unit>>()
    val updateUserResult: LiveData<Result<Unit>> = _updateUserResult
    
    private val _updateRolesResult = MutableLiveData<Result<UserDTO>>()
    val updateRolesResult: LiveData<Result<UserDTO>> = _updateRolesResult

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun loadUsers(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = userRepository.getAllUsers(forceRefresh)
                _users.value = result
            } catch (e: Exception) {
                _users.value = Result.Error(e)
            } finally {
                _loading.value = false
            }
        }
    }

    fun deleteUser(userId: Int) {
        viewModelScope.launch {
            try {
                val result = userRepository.deleteUser(userId)
                _deleteUserResult.value = result
            } catch (e: Exception) {
                _deleteUserResult.value = Result.Error(e)
            }
        }
    }

    fun updateUserRole(username: String, role: RoleDTO) {
        viewModelScope.launch {
            try {
                val result = userRepository.assignRole(username, role)
                _updateUserResult.value = result
            } catch (e: Exception) {
                _updateUserResult.value = Result.Error(e)
            }
        }
    }
    
    fun updateUserRoles(userId: Int, roleNames: List<String>) {
        viewModelScope.launch {
            try {
                val result = userRepository.updateUserRoles(userId, roleNames)
                _updateRolesResult.value = result
            } catch (e: Exception) {
                _updateRolesResult.value = Result.Error(e)
            }
        }
    }
    
    fun addRolesToUser(username: String, roleNames: List<String>) {
        viewModelScope.launch {
            try {
                val result = userRepository.addRolesToUser(username, roleNames)
                _updateRolesResult.value = result
            } catch (e: Exception) {
                _updateRolesResult.value = Result.Error(e)
            }
        }
    }
    
    fun removeRoleFromUser(username: String, roleName: String) {
        viewModelScope.launch {
            try {
                val result = userRepository.removeRoleFromUser(username, roleName)
                _updateRolesResult.value = result
            } catch (e: Exception) {
                _updateRolesResult.value = Result.Error(e)
            }
        }
    }

    fun refreshUsers() {
        loadUsers(true)
    }
}
