package com.restaurantclient.ui.admin

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.eightbitlab.com.blurview.RenderScriptBlur
import com.restaurantclient.R
import com.restaurantclient.data.Result
import com.restaurantclient.data.dto.RoleDTO
import com.restaurantclient.databinding.ActivityCreateUserBinding
import com.restaurantclient.ui.common.setupGlassEffect
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateUserActivity : BaseAdminActivity() {

    private lateinit var binding: ActivityCreateUserBinding
    private val createUserViewModel: CreateUserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupGlassForm()
        setupClickListeners()
        setupObservers()
    }

    private fun setupToolbar() {
        setupAdminToolbar(
            binding.adminToolbar.toolbar,
            getString(R.string.create_user_title),
            showBackButton = true
        )
    }

    private fun setupGlassForm() {
        val whiteOverlay = ContextCompat.getColor(this, R.color.white_glass_overlay)
        binding.createUserFormBlur.setOverlayColor(whiteOverlay)
        binding.createUserFormBlur.setupGlassEffect(25f)
    }

    private fun setupClickListeners() {
        binding.createUserButton.setOnClickListener {
            createUser()
        }

        binding.cancelButton.setOnClickListener {
            finish()
        }
    }

    private fun setupObservers() {
        createUserViewModel.createUserResult.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "User created successfully!", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    val message = com.restaurantclient.util.ErrorUtils.getHumanFriendlyErrorMessage(result.exception)
                    Toast.makeText(this, "Failed to create user: $message", Toast.LENGTH_LONG).show()
                }
            }
        }

        createUserViewModel.loading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.createUserButton.isEnabled = !isLoading
        }
    }

    private fun createUser() {
        val username = binding.usernameEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString()
        val confirmPassword = binding.confirmPasswordEditText.text.toString()

        // Validation
        if (username.isEmpty()) {
            binding.usernameLayout.error = "Username is required"
            return
        } else {
            binding.usernameLayout.error = null
        }

        if (password.isEmpty()) {
            binding.passwordLayout.error = "Password is required"
            return
        } else {
            binding.passwordLayout.error = null
        }

        if (password != confirmPassword) {
            binding.confirmPasswordLayout.error = "Passwords do not match"
            return
        } else {
            binding.confirmPasswordLayout.error = null
        }

        if (password.length < 6) {
            binding.passwordLayout.error = "Password must be at least 6 characters"
            return
        } else {
            binding.passwordLayout.error = null
        }

        // Create user
        binding.progressBar.visibility = View.VISIBLE
        createUserViewModel.createUser(username, password, RoleDTO.Customer)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
