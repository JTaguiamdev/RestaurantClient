package com.restaurantclient.ui.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.eightbitlab.com.blurview.RenderScriptBlur
import com.restaurantclient.R
import com.restaurantclient.data.dto.RoleDTO
import com.restaurantclient.data.dto.UserDTO
import com.restaurantclient.databinding.ItemUserBinding
import com.restaurantclient.ui.common.setupGlassEffect

class UserManagementAdapter(
    private val onEditUser: (UserDTO) -> Unit,
    private val onDeleteUser: (UserDTO) -> Unit,
    private val onManagePermissions: (UserDTO) -> Unit
) : ListAdapter<UserDTO, UserManagementAdapter.UserViewHolder>(UserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class UserViewHolder(private val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            setupBlur()
        }

        private fun setupBlur() {
            val context = binding.root.context
            val whiteOverlay = ContextCompat.getColor(context, R.color.white_glass_overlay)
            binding.userCardBlur.setOverlayColor(whiteOverlay)
            binding.userCardBlur.setupGlassEffect(20f)
        }

        fun bind(user: UserDTO) {
            binding.usernameText.text = user.username
            binding.createdAtText.text = user.createdAt ?: "Unknown"
            
            // Display role with permissions
            val role = user.roleDetails
            if (role != null) {
                val permissions = role.permissions ?: emptyList()
                if (permissions.isNotEmpty()) {
                    binding.roleBadge.text = "${role.name} (${permissions.size} perms: ${permissions.joinToString(", ")})"
                } else {
                    binding.roleBadge.text = role.name
                }
                
                // Set color based on role
                when {
                    user.isAdmin() -> {
                        binding.roleBadge.setBackgroundColor(binding.root.context.getColor(R.color.admin_primary))
                    }
                    else -> {
                        binding.roleBadge.setBackgroundColor(binding.root.context.getColor(R.color.admin_secondary))
                    }
                }
            } else {
                binding.roleBadge.text = "NO ROLE"
                binding.roleBadge.setBackgroundColor(binding.root.context.getColor(R.color.admin_text_secondary))
            }

            // Set click listeners
            binding.editButton.setOnClickListener {
                onEditUser(user)
            }

            binding.deleteButton.setOnClickListener {
                onDeleteUser(user)
            }

            binding.managePermissionsButton.setOnClickListener {
                onManagePermissions(user)
            }

            // Don't allow deletion of admin users (safety measure)
            binding.deleteButton.isEnabled = !user.isAdmin()
        }
    }

    class UserDiffCallback : DiffUtil.ItemCallback<UserDTO>() {
        override fun areItemsTheSame(oldItem: UserDTO, newItem: UserDTO): Boolean {
            return oldItem.userId == newItem.userId
        }

        override fun areContentsTheSame(oldItem: UserDTO, newItem: UserDTO): Boolean {
            return oldItem == newItem
        }
    }
}
