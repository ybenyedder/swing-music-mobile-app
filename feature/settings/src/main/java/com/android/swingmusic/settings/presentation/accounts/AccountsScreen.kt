package com.android.swingmusic.settings.presentation.accounts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.android.swingmusic.auth.presentation.viewmodel.AuthViewModel
import com.android.swingmusic.common.presentation.navigator.CommonNavigator
import com.android.swingmusic.database.domain.model.Account
import com.android.swingmusic.uicomponent.presentation.theme.SwingDimens
import com.android.swingmusic.uicomponent.presentation.theme.SwingGray5
import com.android.swingmusic.uicomponent.presentation.theme.SwingHighlightBlue
import com.android.swingmusic.uicomponent.presentation.theme.SwingMusicTheme
import com.android.swingmusic.uicomponent.presentation.theme.SwingTeal
import com.android.swingmusic.uicomponent.presentation.theme.SwingWhite
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun AccountsScreen(
    commonNavigator: CommonNavigator,
    authViewModel: AuthViewModel,
    viewModel: AccountsViewModel = hiltViewModel()
) {
    val accounts by viewModel.accounts.collectAsStateWithLifecycle()
    val activeAccount = accounts.firstOrNull { it.isActive }
    val isUserLoggedIn by authViewModel.isUserLoggedIn.collectAsStateWithLifecycle()
    val loggedInUser by authViewModel.loggedInUser.collectAsStateWithLifecycle()
    var pendingDelete by remember { mutableStateOf<Account?>(null) }
    var pendingLogout by remember { mutableStateOf(false) }

    SwingMusicTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = 80.dp,
                    bottom = 96.dp
                ),
                verticalArrangement = Arrangement.spacedBy(SwingDimens.Medium)
            ) {
                item {
                    Text(
                        modifier = Modifier.padding(horizontal = SwingDimens.Large),
                        text = "Accounts",
                        color = SwingWhite,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }

                if (accounts.isEmpty()) {
                    item {
                        Text(
                            modifier = Modifier.padding(horizontal = SwingDimens.Large),
                            text = "No accounts saved yet.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    items(items = accounts, key = { it.accountKey }) { account ->
                        AccountRow(
                            account = account,
                            onSwitch = {
                                if (!account.isActive) {
                                    viewModel.switchTo(account.accountKey) {
                                        authViewModel.switchAccount(account.accountKey)
                                        commonNavigator.gotoHome()
                                    }
                                }
                            },
                            onDelete = { pendingDelete = account }
                        )
                    }
                }

                item {
                    ActionRow(
                        title = "Add another account",
                        subtitle = "Sign in with another Swing Music server or user",
                        icon = { Icon(Icons.Filled.Add, contentDescription = null, tint = SwingHighlightBlue) },
                        accent = SwingHighlightBlue,
                        onClick = {
                            authViewModel.prepareAddAccount()
                            commonNavigator.gotoLoginWithUsername()
                        }
                    )
                }

                val isAdminAccess = activeAccount?.isAdmin == true ||
                    loggedInUser?.roles?.any { it.equals("admin", ignoreCase = true) } == true
                if (isAdminAccess) {
                    item {
                        ActionRow(
                            title = "Manage server users",
                            subtitle = "Create users and assign roles (admin only)",
                            icon = {
                                Icon(
                                    Icons.Filled.AdminPanelSettings,
                                    contentDescription = null,
                                    tint = SwingTeal
                                )
                            },
                            accent = SwingTeal,
                            onClick = { commonNavigator.gotoAdminUsers() }
                        )
                    }
                }

                val logoutLabel = when {
                    activeAccount != null -> "Log out of ${activeAccount.displayName}"
                    loggedInUser != null -> "Log out of ${loggedInUser!!.username.ifBlank { "current account" }}"
                    isUserLoggedIn == true -> "Log out"
                    else -> null
                }
                if (logoutLabel != null) {
                    item {
                        ActionRow(
                            title = logoutLabel,
                            subtitle = "Forget this account on this device",
                            icon = {
                                Icon(
                                    Icons.Filled.Logout,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            },
                            accent = MaterialTheme.colorScheme.error,
                            onClick = { pendingLogout = true }
                        )
                    }
                }
            }
        }
    }

    pendingDelete?.let { account ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text("Remove account?") },
            text = { Text("This will forget ${account.displayName} on this device. Server data is untouched.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.remove(account.accountKey)
                    pendingDelete = null
                    if (account.isActive) {
                        authViewModel.logout()
                        commonNavigator.gotoLoginWithUsername()
                    }
                }) { Text("Remove") }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) { Text("Cancel") }
            }
        )
    }

    if (pendingLogout) {
        AlertDialog(
            onDismissRequest = { pendingLogout = false },
            title = { Text("Log out?") },
            text = { Text("You will need to sign in again to use this account.") },
            confirmButton = {
                TextButton(onClick = {
                    pendingLogout = false
                    authViewModel.logout()
                    commonNavigator.gotoLoginWithUsername()
                }) { Text("Log out") }
            },
            dismissButton = {
                TextButton(onClick = { pendingLogout = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun AccountRow(
    account: Account,
    onSwitch: () -> Unit,
    onDelete: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SwingDimens.Large)
            .clip(RoundedCornerShape(SwingDimens.RadiusMd))
            .background(SwingGray5)
            .clickable(onClick = onSwitch)
            .padding(horizontal = SwingDimens.Medium, vertical = SwingDimens.Medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(SwingHighlightBlue.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = null,
                tint = SwingHighlightBlue,
                modifier = Modifier.size(22.dp),
            )
        }
        Spacer(modifier = Modifier.width(SwingDimens.Medium))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = account.displayName,
                    color = SwingWhite,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (account.isActive) {
                    Spacer(Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Active",
                        tint = SwingHighlightBlue,
                        modifier = Modifier.size(16.dp)
                    )
                }
                if (account.isAdmin) {
                    Spacer(Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(SwingDimens.RadiusSm))
                            .background(SwingTeal.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "ADMIN",
                            color = SwingTeal,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                        )
                    }
                }
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text = "@${account.username}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = account.serverUrl,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = "Remove account",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ActionRow(
    title: String,
    subtitle: String,
    icon: @Composable () -> Unit,
    accent: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SwingDimens.Large)
            .clip(RoundedCornerShape(SwingDimens.RadiusMd))
            .background(SwingGray5)
            .clickable(onClick = onClick)
            .padding(horizontal = SwingDimens.Medium, vertical = SwingDimens.Medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(SwingDimens.RadiusSm))
                .background(accent.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center,
        ) { icon() }
        Spacer(modifier = Modifier.width(SwingDimens.Small))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = SwingWhite,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
            )
            Text(
                text = subtitle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
            )
        }
    }
}
