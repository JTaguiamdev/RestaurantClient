package com.orderly.ui.auth

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.orderly.data.Result
import com.orderly.data.dto.LoginDTO
import com.orderly.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()

        binding.loginButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            authViewModel.login(LoginDTO(username, password))
        }

        binding.registerButton.setOnClickListener {
            // For simplicity, this will just show a toast.
            // A real app would likely navigate to a separate registration screen.
            Toast.makeText(this, "Registration is not implemented yet.", Toast.LENGTH_SHORT).show()
        }
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
    }
}