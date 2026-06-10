package com.android.swingmusic.settings.presentation.accounts

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.text.KeyboardOptions
import com.android.swingmusic.common.presentation.navigator.CommonNavigator
import com.android.swingmusic.uicomponent.presentation.theme.SwingDimens
import com.android.swingmusic.uicomponent.presentation.theme.SwingGray5
import com.android.swingmusic.uicomponent.presentation.theme.SwingHighlightBlue
import com.android.swingmusic.uicomponent.presentation.theme.SwingMusicTheme
import com.android.swingmusic.uicomponent.presentation.theme.SwingTeal
import com.android.swingmusic.uicomponent.presentation.theme.SwingWhite
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun AdminUsersScreen(
    commonNavigator: CommonNavigator,
    viewModel: AdminUsersViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showCreate by remember { mutableStateOf(false) }

    SwingMusicTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    modifier = Modifier.padding(
                        start = SwingDimens.Large,
                        end = SwingDimens.Large,
                        top = 80.dp,
                        bottom = SwingDimens.Medium
                    ),
                    text = "Server Users",
                    color = SwingWhite,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        top = SwingDimens.Small,
                        bottom = 120.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(SwingDimens.Medium)
                ) {

                if (!state.canManage) {
                    item {
                        Text(
                            modifier = Modifier.padding(horizontal = SwingDimens.Large),
                            text = "Only admins can manage server users.",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 13.sp
                        )
                    }
                }

                state.error?.let { err ->
                    item {
                        Text(
                            modifier = Modifier.padding(horizontal = SwingDimens.Large),
                            text = err,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 13.sp
                        )
                    }
                }

                if (state.isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                } else {
                    items(items = state.users, key = { it.id }) { user ->
                        UserRow(user)
                    }
                }
                }
            }

            if (state.canManage) {
                ExtendedFloatingActionButton(
                    onClick = { showCreate = true },
                    containerColor = SwingHighlightBlue,
                    contentColor = SwingWhite,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(
                            end = SwingDimens.Large,
                            bottom = SwingDimens.Larger + 120.dp
                        )
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("New user")
                }
            }
        }
    }

    if (showCreate) {
        CreateUserDialog(
            creating = state.creating,
            error = state.createError,
            onDismiss = {
                showCreate = false
                viewModel.clearCreateMessages()
            },
            onSubmit = { username, password, email, isAdmin ->
                viewModel.createUser(username, password, email, isAdmin)
            }
        )

        LaunchedEffect(state.createSuccessMessage) {
            if (state.createSuccessMessage != null) {
                showCreate = false
                viewModel.clearCreateMessages()
            }
        }
    }
}

@Composable
private fun UserRow(user: com.android.swingmusic.database.domain.model.User) {
    val isAdmin = user.roles.any { it.equals("admin", ignoreCase = true) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SwingDimens.Large)
            .clip(RoundedCornerShape(SwingDimens.RadiusMd))
            .background(SwingGray5)
            .padding(horizontal = SwingDimens.Medium, vertical = SwingDimens.Medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(SwingHighlightBlue.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = null,
                tint = SwingHighlightBlue,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(Modifier.width(SwingDimens.Small))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = listOf(user.firstname, user.lastname)
                        .filter { it.isNotBlank() }
                        .joinToString(" ")
                        .ifBlank { user.username },
                    color = SwingWhite,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (isAdmin) {
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
            Text(
                text = "@${user.username}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (user.email.isNotBlank()) {
                Text(
                    text = user.email,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun CreateUserDialog(
    creating: Boolean,
    error: String?,
    onDismiss: () -> Unit,
    onSubmit: (username: String, password: String, email: String, isAdmin: Boolean) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var isAdmin by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { if (!creating) onDismiss() },
        title = { Text("Create user") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(SwingDimens.Small)) {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    singleLine = true,
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email (optional)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isAdmin, onCheckedChange = { isAdmin = it })
                    Text("Grant admin role")
                }
                error?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = !creating,
                onClick = { onSubmit(username, password, email, isAdmin) }
            ) {
                Text(if (creating) "Creating…" else "Create")
            }
        },
        dismissButton = {
            TextButton(enabled = !creating, onClick = onDismiss) { Text("Cancel") }
        }
    )
}
