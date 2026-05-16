package com.android.swingmusic.settings.presentation.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.swingmusic.auth.domain.repository.AccountRepository
import com.android.swingmusic.auth.domain.repository.AuthRepository
import com.android.swingmusic.core.data.util.Resource
import com.android.swingmusic.database.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminUsersUiState(
    val isLoading: Boolean = false,
    val users: List<User> = emptyList(),
    val error: String? = null,
    val creating: Boolean = false,
    val createError: String? = null,
    val createSuccessMessage: String? = null,
    val canManage: Boolean = false
)

@HiltViewModel
class AdminUsersViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminUsersUiState())
    val state: StateFlow<AdminUsersUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val active = accountRepository.getActiveAccount()
            _state.update { it.copy(canManage = active?.isAdmin == true) }
            val baseUrl = active?.serverUrl ?: authRepository.getBaseUrl()?.trimEnd('/')
            if (baseUrl.isNullOrBlank()) {
                _state.update { it.copy(error = "No server configured", isLoading = false) }
                return@launch
            }
            authRepository.getAllUsers(baseUrl).collectLatest { res ->
                when (res) {
                    is Resource.Loading -> _state.update { it.copy(isLoading = true, error = null) }
                    is Resource.Error -> _state.update {
                        it.copy(isLoading = false, error = res.message ?: "Failed to load users")
                    }
                    is Resource.Success -> _state.update {
                        it.copy(
                            isLoading = false,
                            error = null,
                            users = res.data?.users.orEmpty()
                        )
                    }
                }
            }
        }
    }

    fun clearCreateMessages() {
        _state.update { it.copy(createError = null, createSuccessMessage = null) }
    }

    fun createUser(username: String, password: String, email: String, isAdmin: Boolean) {
        if (username.isBlank() || password.isBlank()) {
            _state.update { it.copy(createError = "Username and password required") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(creating = true, createError = null, createSuccessMessage = null) }
            val roles = if (isAdmin) listOf("admin") else emptyList()
            authRepository.createUser(
                username = username.trim(),
                password = password,
                email = email.trim(),
                roles = roles
            ).collectLatest { res ->
                when (res) {
                    is Resource.Loading -> {}
                    is Resource.Error -> _state.update {
                        it.copy(creating = false, createError = res.message ?: "Failed to create user")
                    }
                    is Resource.Success -> {
                        _state.update {
                            it.copy(
                                creating = false,
                                createSuccessMessage = "Created ${res.data?.username ?: username}"
                            )
                        }
                        refresh()
                    }
                }
            }
        }
    }
}
