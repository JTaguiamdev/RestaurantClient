package com.orderly.ui.auth

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.orderly.data.Result
import com.orderly.data.TokenManager
import com.orderly.data.dto.LoginDTO
import com.orderly.data.dto.LoginResponse
import com.orderly.data.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle // Added import
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.never
import org.mockito.kotlin.doReturn

package com.orderly.ui.auth

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.orderly.data.Result
import com.orderly.data.TokenManager
import com.orderly.data.dto.LoginDTO
import com.orderly.data.dto.LoginResponse
import com.orderly.data.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle // Added import
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.never
import org.mockito.kotlin.doReturn

@ExperimentalCoroutinesApi
class AuthViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var authViewModel: AuthViewModel
    private val userRepository: UserRepository = mock()
    private val tokenManager: TokenManager = mock()
    private lateinit var testScope: TestScope // Declare TestScope

    @Before
    fun setUp() {
        testScope = TestScope() // Initialize TestScope
        authViewModel = AuthViewModel(userRepository, tokenManager, testScope) // Pass testScope
        doNothing().whenever(tokenManager).deleteToken()
    }

    @Test
    fun `isLoggedIn returns true when token and username exist and token is valid`() {
        // Given
        whenever(tokenManager.getToken()).thenReturn("some_token")
        whenever(tokenManager.isTokenValid()).thenReturn(true)
        whenever(tokenManager.getUsername()).thenReturn("testuser")

        // When
        val isLoggedIn = authViewModel.isLoggedIn()

        // Then
        assert(isLoggedIn)
        verify(tokenManager, never()).deleteToken()
    }

    @Test
    fun `isLoggedIn returns false when token is null`() {
        // Given
        whenever(tokenManager.getToken()).thenReturn(null)
        whenever(tokenManager.isTokenValid()).thenReturn(false)
        whenever(tokenManager.getUsername()).thenReturn(null)

        // When
        val isLoggedIn = authViewModel.isLoggedIn()

        // Then
        assert(!isLoggedIn)
        verify(tokenManager, never()).deleteToken()
    }

    @Test
    fun `logout deletes token`() {
        // When
        authViewModel.logout()

        // Then
        verify(tokenManager).deleteToken()
    }
}