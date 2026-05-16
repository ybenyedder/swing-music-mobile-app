package com.android.swingmusic.settings.presentation.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.swingmusic.auth.domain.repository.AccountRepository
import com.android.swingmusic.database.domain.model.Account
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountsViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {

    val accounts: StateFlow<List<Account>> =
        accountRepository.observeAllAccounts()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    fun switchTo(accountKey: String, onSwitched: () -> Unit = {}) {
        viewModelScope.launch {
            accountRepository.switchTo(accountKey)
            onSwitched()
        }
    }

    fun remove(accountKey: String) {
        viewModelScope.launch {
            accountRepository.removeAccount(accountKey)
        }
    }
}
