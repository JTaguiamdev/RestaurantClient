package com.restaurantclient.ui.admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.restaurantclient.data.Result
import com.restaurantclient.data.dto.RoleDTO
import com.restaurantclient.data.dto.RoleDetailsDTO
import com.restaurantclient.data.repository.RoleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoleManagementViewModel @Inject constructor(
    private val roleRepository: RoleRepository
) : ViewModel() {

    private val _roles = MutableLiveData<Result<List<RoleDetailsDTO>>>()
    val roles: LiveData<Result<List<RoleDetailsDTO>>> = _roles

    private val _createRoleResult = MutableLiveData<Result<RoleDTO>>()
    val createRoleResult: LiveData<Result<RoleDTO>> = _createRoleResult

    private val _updateRoleResult = MutableLiveData<Result<RoleDTO>>()
    val updateRoleResult: LiveData<Result<RoleDTO>> = _updateRoleResult

    private val _deleteRoleResult = MutableLiveData<Result<Unit>>()
    val deleteRoleResult: LiveData<Result<Unit>> = _deleteRoleResult

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private var pollingJob: Job? = null

    fun loadRoles(forceRefresh: Boolean = false, showLoading: Boolean = true) {
        viewModelScope.launch {
            if (showLoading) _loading.value = true
            try {
                val result = roleRepository.getAllRoles(forceRefresh)
                _roles.value = result
            } catch (e: Exception) {
                _roles.value = Result.Error(e)
            } finally {
                if (showLoading) _loading.value = false
            }
        }
    }

    fun startPollingRoles() {
        if (pollingJob?.isActive == true) return
        
        pollingJob = viewModelScope.launch {
            while (true) {
                loadRoles(forceRefresh = true, showLoading = false)
                delay(5000)
            }
        }
    }

    fun stopPollingRoles() {
        pollingJob?.cancel()
        pollingJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopPollingRoles()
    }

    fun createRole(name: String, description: String) {
        viewModelScope.launch {
            try {
                val result = roleRepository.createRole(name, description)
                _createRoleResult.value = result
                loadRoles(true)
            } catch (e: Exception) {
                _createRoleResult.value = Result.Error(e)
            }
        }
    }

    fun updateRole(roleId: Int, name: String, description: String) {
        viewModelScope.launch {
            try {
                val result = roleRepository.updateRole(roleId, name, description)
                _updateRoleResult.value = result
                loadRoles(true)
            } catch (e: Exception) {
                _updateRoleResult.value = Result.Error(e)
            }
        }
    }

    fun deleteRole(roleId: Int) {
        viewModelScope.launch {
            try {
                val result = roleRepository.deleteRole(roleId)
                _deleteRoleResult.value = result
                loadRoles(true)
            } catch (e: Exception) {
                _deleteRoleResult.value = Result.Error(e)
            }
        }
    }

    fun addPermissionToRole(roleName: String, permission: String) {
        viewModelScope.launch {
            try {
                roleRepository.addPermissionToRole(roleName, permission)
                loadRoles(true)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun removePermissionFromRole(roleId: Int) {
        viewModelScope.launch {
            try {
                roleRepository.removePermissionFromRole(roleId)
                loadRoles(true)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun refreshRoles() {
        loadRoles(true)
    }
}
