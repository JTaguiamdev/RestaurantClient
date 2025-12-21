package com.restaurantclient.ui.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.restaurantclient.R
import com.restaurantclient.data.dto.RoleDetailsDTO
import com.restaurantclient.databinding.ItemRoleBinding
import com.restaurantclient.ui.common.setupGlassEffect
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class RoleManagementAdapter(
    private val onEditRole: (RoleDetailsDTO) -> Unit,
    private val onDeleteRole: (RoleDetailsDTO) -> Unit,
    private val onAddPermission: (RoleDetailsDTO) -> Unit,
    private val onRemovePermission: (RoleDetailsDTO) -> Unit
) : ListAdapter<RoleDetailsDTO, RoleManagementAdapter.RoleViewHolder>(RoleDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoleViewHolder {
        val binding = ItemRoleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RoleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RoleViewHolder(private val binding: ItemRoleBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            setupBlur()
        }

        private fun setupBlur() {
            val context = binding.root.context
            val whiteOverlay = ContextCompat.getColor(context, R.color.white_glass_overlay)
            binding.roleCardBlur.setOverlayColor(whiteOverlay)
            binding.roleCardBlur.setupGlassEffect(20f)
        }

        fun bind(role: RoleDetailsDTO) {
            binding.roleNameText.text = role.name
            binding.roleDescriptionText.text = role.description ?: "No description"
            
            // Format permissions list
            val permissions = role.permissions ?: emptyList()
            if (permissions.isNotEmpty()) {
                binding.permissionsBadge.text = "${permissions.size} permissions: ${permissions.joinToString(", ")}"
                binding.permissionsBadge.visibility = View.VISIBLE
            } else {
                binding.permissionsBadge.text = "No permissions assigned"
                binding.permissionsBadge.visibility = View.VISIBLE
            }

            // Set click listeners
            binding.editButton.setOnClickListener {
                onEditRole(role)
            }

            binding.deleteButton.setOnClickListener {
                onDeleteRole(role)
            }

            binding.addPermissionButton.setOnClickListener {
                onAddPermission(role)
            }

            binding.removePermissionButton.setOnClickListener {
                if (permissions.isNotEmpty()) {
                    onRemovePermission(role)
                } else {
                    Toast.makeText(binding.root.context, "No permissions to remove", Toast.LENGTH_SHORT).show()
                }
            }
            
            // Allow clicking permissions badge as well
            binding.permissionsBadge.setOnClickListener {
                if (permissions.isNotEmpty()) {
                    onRemovePermission(role)
                }
            }
        }
    }

    class RoleDiffCallback : DiffUtil.ItemCallback<RoleDetailsDTO>() {
        override fun areItemsTheSame(oldItem: RoleDetailsDTO, newItem: RoleDetailsDTO): Boolean {
            return oldItem.roleId == newItem.roleId
        }

        override fun areContentsTheSame(oldItem: RoleDetailsDTO, newItem: RoleDetailsDTO): Boolean {
            return oldItem == newItem
        }
    }
}
