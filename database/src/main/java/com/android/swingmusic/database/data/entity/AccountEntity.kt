package com.android.swingmusic.database.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey
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
)
