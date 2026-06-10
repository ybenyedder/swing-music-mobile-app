package com.android.swingmusic.database.domain.model

data class Account(
    val accountKey: String,
    val userId: Int,
    val serverUrl: String,
    val username: String,
    val firstname: String,
    val lastname: String,
    val email: String,
    val image: String,
    val roles: List<String>,
    val accessToken: String,
    val refreshToken: String,
    val maxAge: Long,
    val isActive: Boolean
) {
    val isAdmin: Boolean
        get() = roles.any { it.equals("admin", ignoreCase = true) }

    val displayName: String
        get() = listOf(firstname, lastname)
            .filter { it.isNotBlank() }
            .joinToString(" ")
            .ifBlank { username }

    companion object {
        fun keyOf(serverUrl: String, userId: Int): String = "$userId@$serverUrl"
    }
}
