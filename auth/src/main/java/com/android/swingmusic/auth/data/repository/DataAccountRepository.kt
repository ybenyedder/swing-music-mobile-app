package com.android.swingmusic.auth.data.repository

import com.android.swingmusic.auth.data.baseurlholder.BaseUrlHolder
import com.android.swingmusic.auth.data.datastore.AuthTokensDataStore
import com.android.swingmusic.auth.data.tokenholder.AuthTokenHolder
import com.android.swingmusic.auth.domain.repository.AccountRepository
import com.android.swingmusic.database.data.dao.AccountDao
import com.android.swingmusic.database.data.dao.BaseUrlDao
import com.android.swingmusic.database.data.dao.UserDao
import com.android.swingmusic.database.data.mapper.toEntity
import com.android.swingmusic.database.data.mapper.toModel
import com.android.swingmusic.database.domain.model.Account
import com.android.swingmusic.database.domain.model.BaseUrl
import com.android.swingmusic.database.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataAccountRepository @Inject constructor(
    private val accountDao: AccountDao,
    private val userDao: UserDao,
    private val baseUrlDao: BaseUrlDao,
    private val tokensDataStore: AuthTokensDataStore
) : AccountRepository {

    override suspend fun getAllAccounts(): List<Account> {
        return accountDao.getAllAccounts().map { it.toModel() }
    }

    override fun observeAllAccounts(): Flow<List<Account>> {
        return accountDao.observeAllAccounts().map { list -> list.map { it.toModel() } }
    }

    override suspend fun getActiveAccount(): Account? {
        return accountDao.getActiveAccount()?.toModel()
    }

    override suspend fun saveCurrentSessionAsAccount(user: User): Account {
        val rawBaseUrl = BaseUrlHolder.baseUrl
            ?: baseUrlDao.getBaseUrl()?.url
            ?: error("No baseUrl available to snapshot")
        val accessToken = AuthTokenHolder.accessToken
            ?: tokensDataStore.accessToken.firstOrNull()
            ?: error("No accessToken available to snapshot")
        val refreshToken = AuthTokenHolder.refreshToken
            ?: tokensDataStore.refreshToken.firstOrNull()
            ?: error("No refreshToken available to snapshot")
        val maxAge = tokensDataStore.maxTokenAge.firstOrNull() ?: 0L

        val normalisedServerUrl = rawBaseUrl.trimEnd('/')
        val account = Account(
            accountKey = Account.keyOf(normalisedServerUrl, user.id),
            userId = user.id,
            serverUrl = normalisedServerUrl,
            username = user.username,
            firstname = user.firstname,
            lastname = user.lastname,
            email = user.email,
            image = user.image,
            roles = user.roles,
            accessToken = accessToken,
            refreshToken = refreshToken,
            maxAge = maxAge,
            isActive = true
        )
        accountDao.deactivateAll()
        accountDao.upsert(account.toEntity())
        return account
    }

    override suspend fun switchTo(accountKey: String): Account? {
        val target = accountDao.getAccount(accountKey)?.toModel() ?: return null
        accountDao.deactivateAll()
        accountDao.activate(accountKey)
        applyAccountToSession(target)
        return target
    }

    override suspend fun removeAccount(accountKey: String) {
        val active = accountDao.getActiveAccount()
        accountDao.delete(accountKey)
        if (active?.accountKey == accountKey) {
            clearActiveSession()
            accountDao.getAllAccounts().firstOrNull()?.let { remaining ->
                switchTo(remaining.accountKey)
            }
        }
    }

    override suspend fun logout() {
        val active = accountDao.getActiveAccount()
        if (active != null) {
            accountDao.delete(active.accountKey)
        }
        clearActiveSession()
    }

    override suspend fun deactivateCurrentSession() {
        accountDao.deactivateAll()
        clearActiveSession()
    }

    override suspend fun ensureLegacySessionMigrated() {
        if (accountDao.count() > 0) return

        val legacyUser = userDao.getLoggedInUser()?.toModel() ?: return
        val legacyBaseUrl = baseUrlDao.getBaseUrl()?.url ?: return
        val legacyAccessToken = tokensDataStore.accessToken.firstOrNull().orEmpty()
        val legacyRefreshToken = tokensDataStore.refreshToken.firstOrNull().orEmpty()
        if (legacyAccessToken.isBlank() || legacyRefreshToken.isBlank()) return

        val legacyMaxAge = tokensDataStore.maxTokenAge.firstOrNull() ?: 0L
        val normalisedServerUrl = legacyBaseUrl.trimEnd('/')
        val account = Account(
            accountKey = Account.keyOf(normalisedServerUrl, legacyUser.id),
            userId = legacyUser.id,
            serverUrl = normalisedServerUrl,
            username = legacyUser.username,
            firstname = legacyUser.firstname,
            lastname = legacyUser.lastname,
            email = legacyUser.email,
            image = legacyUser.image,
            roles = legacyUser.roles,
            accessToken = legacyAccessToken,
            refreshToken = legacyRefreshToken,
            maxAge = legacyMaxAge,
            isActive = true
        )
        accountDao.upsert(account.toEntity())
        AuthTokenHolder.accessToken = legacyAccessToken
        AuthTokenHolder.refreshToken = legacyRefreshToken
        BaseUrlHolder.baseUrl = "$normalisedServerUrl/"
    }

    private suspend fun applyAccountToSession(account: Account) {
        val baseUrlWithSlash = "${account.serverUrl}/"
        BaseUrlHolder.baseUrl = baseUrlWithSlash
        AuthTokenHolder.accessToken = account.accessToken
        AuthTokenHolder.refreshToken = account.refreshToken

        tokensDataStore.updateAuthTokens(
            accessToken = account.accessToken,
            refreshToken = account.refreshToken,
            maxAge = account.maxAge
        )
        baseUrlDao.insertBaseUrl(BaseUrl(url = baseUrlWithSlash).toEntity())
        userDao.insertLoggedInUser(
            User(
                id = account.userId,
                firstname = account.firstname,
                lastname = account.lastname,
                email = account.email,
                username = account.username,
                image = account.image,
                roles = account.roles
            ).toEntity()
        )
    }

    private suspend fun clearActiveSession() {
        AuthTokenHolder.accessToken = null
        AuthTokenHolder.refreshToken = null
        BaseUrlHolder.baseUrl = null
        tokensDataStore.updateAuthTokens(accessToken = "", refreshToken = "", maxAge = 0L)
        baseUrlDao.clearBaseUrl()
        userDao.clearLoggedInUser()
    }
}
