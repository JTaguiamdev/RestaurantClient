package com.restaurantclient.ui.auth

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.restaurantclient.R
import com.restaurantclient.data.Result
import com.restaurantclient.data.dto.LoginDTO
import com.restaurantclient.data.dto.NewUserDTO
import com.restaurantclient.databinding.ActivityLoginBinding
import com.restaurantclient.ui.common.setupGlassEffect
import com.restaurantclient.ui.intro.IntroductionActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBackgroundGif()
        setupGlassUI()
        setupObservers()

        binding.aboutButton.setOnClickListener {
            startActivity(android.content.Intent(this, IntroductionActivity::class.java))
        }

        binding.loginButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            
            if (username.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            binding.progressBar.visibility = View.VISIBLE
            authViewModel.login(LoginDTO(username, password))
        }

        binding.registerButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            
            if (username.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            binding.progressBar.visibility = View.VISIBLE
            authViewModel.register(NewUserDTO(username, password))
        }
    }
    
    private fun setupBackgroundGif() {
        // Load from preloaded cache - no rendering delay
        binding.backgroundGif.load(R.raw.steam_animation) {
            crossfade(false)
            memoryCacheKey("steam_animation_login")
            diskCacheKey("steam_animation_login")
        }
    }
    
    private fun setupGlassUI() {
        // Setup glass effect for login card with 25f blur radius
        binding.loginGlassCard.setupGlassEffect(25f)
        binding.loginGlassCard.setOutlineProvider(android.view.ViewOutlineProvider.BACKGROUND)
        binding.loginGlassCard.clipToOutline = true
    }

    private fun setupObservers() {
        authViewModel.loginResult.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Login Failed: ${result.exception.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
        
        authViewModel.registrationResult.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Registration Successful! You are now logged in.", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    val errorMessage = if (result.exception.message?.contains("403") == true) {
                        "Registration closed. First user already exists. Please login instead."
                    } else {
                        "Registration Failed: ${result.exception.message}"
                    }
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
