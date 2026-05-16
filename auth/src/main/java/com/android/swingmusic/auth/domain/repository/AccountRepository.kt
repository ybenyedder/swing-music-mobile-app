package com.android.swingmusic.auth.domain.repository

import com.android.swingmusic.database.domain.model.Account
import com.android.swingmusic.database.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AccountRepository {

    suspend fun getAllAccounts(): List<Account>

    fun observeAllAccounts(): Flow<List<Account>>

    suspend fun getActiveAccount(): Account?

    /**
     * Snapshot the current in-memory + DataStore session into an Account row.
     * Marks the saved account active and deactivates all others.
     */
    suspend fun saveCurrentSessionAsAccount(user: User): Account

    /**
     * Switch the active session to the given account: rewrites tokens,
     * baseUrl, and the single-user table from the stored snapshot.
     */
    suspend fun switchTo(accountKey: String): Account?

    /**
     * Remove an account row. If it was the active one, clears the active
     * session and tries to switch to any remaining account.
     */
    suspend fun removeAccount(accountKey: String)

    /**
     * Log out of the active account: delete its row and wipe the active
     * session state. Does not touch other stored accounts.
     */
    suspend fun logout()

    /**
     * Clear the active session state (tokens, baseUrl, user table) without
     * removing the account row. Used when the user wants to add another
     * account while keeping the current one saved.
     */
    suspend fun deactivateCurrentSession()

    /**
     * If a pre-multi-account session exists (UserEntity + BaseUrlEntity +
     * tokens in DataStore) but the accounts table is empty, snapshot the
     * current session as an Account row so it shows up in the new UI.
     * No-op if accounts already exist or any piece of legacy data is missing.
     */
    suspend fun ensureLegacySessionMigrated()
}
