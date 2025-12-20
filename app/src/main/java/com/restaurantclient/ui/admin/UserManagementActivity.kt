package com.restaurantclient.ui.admin

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.eightbitlab.com.blurview.RenderScriptBlur
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.restaurantclient.MainActivity
import com.restaurantclient.R
import com.restaurantclient.data.Result
import com.restaurantclient.data.dto.RoleDTO
import com.restaurantclient.data.dto.UserDTO
import com.restaurantclient.databinding.ActivityUserManagementBinding
import com.restaurantclient.ui.common.setupGlassEffect
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserManagementActivity : BaseAdminActivity() {

    private lateinit var binding: ActivityUserManagementBinding
    private val userManagementViewModel: UserManagementViewModel by viewModels()
    private lateinit var userManagementAdapter: UserManagementAdapter

    private val createUserLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            // Refresh user list after creating new user
            userManagementViewModel.loadUsers(true)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupGlassFAB()
        setupRecyclerView()
        setupClickListeners()
        setupObservers()

        // Load initial data
        userManagementViewModel.loadUsers()
    }

    private fun setupToolbar() {
        setupAdminToolbar(
            binding.adminToolbar.toolbar,
            getString(R.string.user_management_title),
            showBackButton = true
        )
    }

    private fun setupGlassFAB() {
        binding.addUserFabBlur.setupGlassEffect(25f)
    }

    private fun setupRecyclerView() {
        userManagementAdapter = UserManagementAdapter(
            onEditUser = { user ->
                showAssignRoleDialog(user)
            },
            onDeleteUser = { user ->
                showDeleteConfirmationDialog(user.username) {
                    userManagementViewModel.deleteUser(user.userId ?: 0)
                }
            },
            onManagePermissions = { user ->
                showManagePermissionsDialog(user)
            }
        )

        binding.usersRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@UserManagementActivity)
            adapter = userManagementAdapter
        }
    }

    private fun setupClickListeners() {
        binding.addUserFab.setOnClickListener {
            createUserLauncher.launch(Intent(this, CreateUserActivity::class.java))
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            userManagementViewModel.loadUsers()
        }
    }

    private fun setupObservers() {
        userManagementViewModel.users.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.swipeRefreshLayout.isRefreshing = false
                    userManagementAdapter.submitList(result.data)
                    
                    if (result.data.isEmpty()) {
                        binding.emptyStateLayout.visibility = View.VISIBLE
                        binding.usersRecyclerView.visibility = View.GONE
                    } else {
                        binding.emptyStateLayout.visibility = View.GONE
                        binding.usersRecyclerView.visibility = View.VISIBLE
                    }
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.swipeRefreshLayout.isRefreshing = false
                    Toast.makeText(this, "Failed to load users: ${result.exception.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        userManagementViewModel.deleteUserResult.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    Toast.makeText(this, "User deleted successfully", Toast.LENGTH_SHORT).show()
                    userManagementViewModel.loadUsers(true) // Refresh list
                }
                is Result.Error -> {
                    Toast.makeText(this, "Failed to delete user: ${result.exception.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        userManagementViewModel.updateUserResult.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    Toast.makeText(this, getString(R.string.edit_user_success), Toast.LENGTH_SHORT).show()
                    userManagementViewModel.loadUsers(true)
                }
                is Result.Error -> {
                    Toast.makeText(this, getString(R.string.edit_user_error, result.exception.message), Toast.LENGTH_LONG).show()
                }
            }
        }
        
        userManagementViewModel.updateRolesResult.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    Toast.makeText(this, "User roles updated successfully", Toast.LENGTH_SHORT).show()
                    userManagementViewModel.loadUsers(true)
                }
                is Result.Error -> {
                    Toast.makeText(this, "Failed to update roles: ${result.exception.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
        
        userManagementViewModel.loading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun showAssignRoleDialog(user: UserDTO) {
        val roles = RoleDTO.values()
        val labels = roles.map { it.name.uppercase() }.toTypedArray()
        var selectedIndex = roles.indexOf(user.role ?: RoleDTO.Customer).takeIf { it >= 0 } ?: 0

        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.edit_user_title, user.username))
            .setSingleChoiceItems(labels, selectedIndex) { _, which ->
                selectedIndex = which
            }
            .setPositiveButton(R.string.action_save) { _, _ ->
                val selectedRole = roles[selectedIndex]
                userManagementViewModel.updateUserRole(user.username, selectedRole)
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun showManagePermissionsDialog(user: UserDTO) {
        val currentPermissions = user.roleDetails?.permissions ?: emptyList()
        val permissionsText = if (currentPermissions.isEmpty()) {
            "No permissions assigned"
        } else {
            currentPermissions.joinToString("\n• ", "Current permissions:\n• ")
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("User Permissions: ${user.username}")
            .setMessage(
                "Role: ${user.roleDetails?.name ?: "None"}\n\n$permissionsText\n\n" +
                "To change permissions, assign a different role to this user."
            )
            .setPositiveButton("Change Role") { _, _ ->
                showAssignRoleDialog(user)
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun showDeleteConfirmationDialog(username: String, onConfirm: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle("Delete User")
            .setMessage("Are you sure you want to delete user '$username'? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                onConfirm()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.user_management_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_refresh -> {
                userManagementViewModel.loadUsers(true)
                true
            }
            R.id.action_logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        authViewModel.logout()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
