package com.orderly.ui.auth

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.orderly.data.Result
import com.orderly.data.TokenManager
import com.orderly.data.dto.LoginDTO
import com.orderly.data.dto.LoginResponse
import com.orderly.data.repository.UserRepository
import com.orderly.util.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class AuthViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var authViewModel: AuthViewModel
    private val userRepository: UserRepository = mock()
    private val tokenManager: TokenManager = mock()

    @Before
    fun setUp() {
        authViewModel = AuthViewModel(userRepository, tokenManager)
    }

    @Test
    fun `login success saves token and posts success result`() = runTest {
        // Given
        val loginDto = LoginDTO("user", "pass")
        val loginResponse = LoginResponse("token")
        whenever(userRepository.login(loginDto)).thenReturn(Result.Success(loginResponse))

        // When
        authViewModel.login(loginDto)

        // Then
        verify(tokenManager).saveToken("token")
        assert(authViewModel.loginResult.value is Result.Success)
    }

    @Test
    fun `login failure posts error result`() = runTest {
        // Given
        val loginDto = LoginDTO("user", "pass")
        val exception = Exception("Login failed")
        whenever(userRepository.login(loginDto)).thenReturn(Result.Error(exception))

        // When
        authViewModel.login(loginDto)

        // Then
        assert(authViewModel.loginResult.value is Result.Error)
    }

    @Test
    fun `isLoggedIn returns true when token exists`() {
        // Given
        whenever(tokenManager.getToken()).thenReturn("some_token")

        // When
        val isLoggedIn = authViewModel.isLoggedIn()

        // Then
        assert(isLoggedIn)
    }

    @Test
    fun `isLoggedIn returns false when token is null`() {
        // Given
        whenever(tokenManager.getToken()).thenReturn(null)

        // When
        val isLoggedIn = authViewModel.isLoggedIn()

        // Then
        assert(!isLoggedIn)
    }

    @Test
    fun `logout deletes token`() {
        // When
        authViewModel.logout()

        // Then
        verify(tokenManager).deleteToken()
    }
}