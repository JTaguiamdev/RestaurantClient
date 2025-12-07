package com.orderly.ui.user

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.orderly.MainActivity
import com.orderly.data.Result
import com.orderly.databinding.ActivityUserProfileBinding
import com.orderly.ui.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileBinding
    private val userViewModel: UserViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        // FIXME: Hardcoded userId.
        userViewModel.fetchUserProfile(1)

        binding.logoutButton.setOnClickListener {
            authViewModel.logout()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun setupObservers() {
        userViewModel.userProfile.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    val user = result.data
                    binding.usernameText.text = user.username
                    binding.roleText.text = user.role.toString()
                    binding.createdAtText.text = user.createdAt?.substringBefore("T")
                }
                is Result.Error -> {
                    Toast.makeText(this, "Failed to fetch user profile: ${result.exception.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}