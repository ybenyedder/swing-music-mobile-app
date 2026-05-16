package com.android.swingmusic.auth.presentation.state

import com.android.swingmusic.auth.presentation.util.AuthError
import com.android.swingmusic.database.domain.model.User

data class AuthUiState(
    val baseUrl: String? = null,
    val username: String? = "",
    val password: String? = "",
    val authState: AuthState = AuthState.LOGGED_OUT,
    val isLoading: Boolean = false,
    val authError: AuthError = AuthError.None,
    val serverUsers: List<User> = emptyList(),
    val usersOnLogin: Boolean = false,
    val enableGuest: Boolean = false,
    val loadingServerUsers: Boolean = false,
    val serverUsersError: String? = null
)
