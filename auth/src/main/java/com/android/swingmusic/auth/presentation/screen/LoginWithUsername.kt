package com.android.swingmusic.auth.presentation.screen

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.android.swingmusic.auth.presentation.event.AuthUiEvent
import com.android.swingmusic.auth.presentation.state.AuthState
import com.android.swingmusic.auth.presentation.state.AuthUiState
import com.android.swingmusic.auth.presentation.util.AuthError
import com.android.swingmusic.auth.presentation.util.AuthUtils.normalizeUrl
import com.android.swingmusic.auth.presentation.util.AuthUtils.validInputUrl
import com.android.swingmusic.auth.presentation.viewmodel.AuthViewModel
import com.android.swingmusic.common.presentation.navigator.CommonNavigator
import com.android.swingmusic.database.domain.model.User
import com.android.swingmusic.uicomponent.R
import com.android.swingmusic.uicomponent.presentation.theme.SwingBody
import com.android.swingmusic.uicomponent.presentation.theme.SwingGray5
import com.android.swingmusic.uicomponent.presentation.theme.SwingHighlightBlue
import com.android.swingmusic.uicomponent.presentation.theme.SwingMusicTheme
import com.android.swingmusic.uicomponent.presentation.theme.SwingWhite
import com.ramcosta.composedestinations.annotation.Destination
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue

private enum class LoginStep { URL, USER_PICKER, PASSWORD }

@Destination
@Composable
fun LoginWithUsernameScreen(
    authViewModel: AuthViewModel,
    authNavigator: CommonNavigator
) {
    val authUiState by authViewModel.authUiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        authViewModel.authStateEvent.collect { state ->
            if (state == AuthState.AUTHENTICATED) {
                authNavigator.gotoFolders()
            }
        }
    }

    LaunchedEffect(authUiState.baseUrl) {
        val raw = authUiState.baseUrl
        if (!raw.isNullOrBlank()) {
            val normalised = normalizeUrl(raw)
            if (!normalised.isNullOrBlank() && validInputUrl(normalised)) {
                delay(600)
                authViewModel.fetchServerUsers()
            }
        }
    }

    val step = when {
        !authUiState.username.isNullOrBlank() -> LoginStep.PASSWORD
        authUiState.serverUsers.isNotEmpty() -> LoginStep.USER_PICKER
        else -> LoginStep.URL
    }

    LoginRoot(
        step = step,
        state = authUiState,
        onBaseUrlChange = { authViewModel.onAuthUiEvent(AuthUiEvent.OnBaseUrlChange(it.trim())) },
        onUsernameChange = { authViewModel.onAuthUiEvent(AuthUiEvent.OnUsernameChange(it.trim())) },
        onPasswordChange = { authViewModel.onAuthUiEvent(AuthUiEvent.OnPasswordChange(it.trim())) },
        onContinue = { authViewModel.fetchServerUsers() },
        onLogIn = { authViewModel.onAuthUiEvent(AuthUiEvent.LogInWithUsernameAndPassword) },
        onSwitchUser = { authViewModel.onAuthUiEvent(AuthUiEvent.OnUsernameChange("")) },
        onUseDifferentServer = {
            authViewModel.onAuthUiEvent(AuthUiEvent.OnUsernameChange(""))
            authViewModel.onAuthUiEvent(AuthUiEvent.OnPasswordChange(""))
            authViewModel.onAuthUiEvent(AuthUiEvent.OnBaseUrlChange(""))
        },
        onQrCode = { authNavigator.gotoLoginWithQrCode() }
    )

    BackHandler(enabled = !authUiState.isLoading) {
        when (step) {
            LoginStep.PASSWORD -> authViewModel.onAuthUiEvent(AuthUiEvent.OnUsernameChange(""))
            LoginStep.USER_PICKER -> {
                authViewModel.onAuthUiEvent(AuthUiEvent.ClearErrorState)
                authNavigator.gotoLoginWithQrCode()
            }
            LoginStep.URL -> {
                authViewModel.onAuthUiEvent(AuthUiEvent.ClearErrorState)
                authNavigator.gotoLoginWithQrCode()
            }
        }
    }
}

@Composable
private fun LoginRoot(
    step: LoginStep,
    state: AuthUiState,
    onBaseUrlChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onContinue: () -> Unit,
    onLogIn: () -> Unit,
    onSwitchUser: () -> Unit,
    onUseDifferentServer: () -> Unit,
    onQrCode: () -> Unit,
) {
    SwingMusicTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SwingBody)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 56.dp, bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Icon(
                        painter = painterResource(R.drawable.swing_music_logo_outlined),
                        contentDescription = null,
                        tint = SwingHighlightBlue,
                        modifier = Modifier.size(56.dp)
                    )
                }
                item { Spacer(Modifier.height(8.dp)) }
                item {
                    Text(
                        text = "Swing Music",
                        color = SwingWhite.copy(alpha = 0.6f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.sp
                    )
                }
                item { Spacer(Modifier.height(32.dp)) }
                item {
                    AnimatedContent(
                        targetState = step,
                        label = "loginStep",
                        transitionSpec = {
                            (fadeIn(animationSpec = tween(300)) +
                                slideInVertically(initialOffsetY = { it / 20 })) togetherWith
                                (fadeOut(animationSpec = tween(200)) +
                                    slideOutVertically(targetOffsetY = { -it / 20 }))
                        }
                    ) { current ->
                        when (current) {
                            LoginStep.URL -> UrlPane(
                                state = state,
                                onBaseUrlChange = onBaseUrlChange,
                                onContinue = onContinue,
                                onQrCode = onQrCode
                            )
                            LoginStep.USER_PICKER -> UserPickerPane(
                                state = state,
                                onPick = { onUsernameChange(it.username) },
                                onUseDifferentServer = onUseDifferentServer,
                                onGuest = { onUsernameChange("guest") },
                            )
                            LoginStep.PASSWORD -> PasswordPane(
                                state = state,
                                onPasswordChange = onPasswordChange,
                                onLogIn = onLogIn,
                                onSwitchUser = onSwitchUser
                            )
                        }
                    }
                }
                item { Spacer(Modifier.height(24.dp)) }
                item { StatusLine(state) }
            }
        }
    }
}

@Composable
private fun UrlPane(
    state: AuthUiState,
    onBaseUrlChange: (String) -> Unit,
    onContinue: () -> Unit,
    onQrCode: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(0.86f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Connect to server",
            color = SwingWhite,
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Enter the URL of your Swing Music server",
            color = SwingWhite.copy(alpha = 0.55f),
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(28.dp))
        OutlinedTextField(
            value = state.baseUrl ?: "",
            onValueChange = onBaseUrlChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = {
                Text(
                    text = "https://music.example.com",
                    color = SwingWhite.copy(alpha = 0.35f)
                )
            },
            shape = RoundedCornerShape(14.dp),
            colors = textFieldColors(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Uri,
                imeAction = ImeAction.Go
            ),
            keyboardActions = KeyboardActions(onGo = { onContinue() })
        )
        Spacer(Modifier.height(8.dp))
        AnimatedVisibility(visible = state.loadingServerUsers) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(12.dp),
                    strokeWidth = 1.5.dp,
                    color = SwingHighlightBlue
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Reaching server…",
                    color = SwingWhite.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
            }
        }
        AnimatedVisibility(visible = state.serverUsersError != null) {
            Text(
                text = state.serverUsersError.orEmpty(),
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
            )
        }
        Spacer(Modifier.height(28.dp))
        Button(
            onClick = onContinue,
            enabled = !state.loadingServerUsers,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SwingHighlightBlue,
                contentColor = SwingWhite
            ),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Continue", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
        }
        Spacer(Modifier.height(20.dp))
        TextButton(onClick = onQrCode) {
            Text(
                text = "Pair via QR code instead",
                color = SwingWhite.copy(alpha = 0.7f),
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun UserPickerPane(
    state: AuthUiState,
    onPick: (User) -> Unit,
    onUseDifferentServer: () -> Unit,
    onGuest: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(0.92f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome back",
            color = SwingWhite,
            fontSize = 26.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = state.baseUrl?.trimEnd('/').orEmpty(),
            color = SwingWhite.copy(alpha = 0.45f),
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(32.dp))
        val rows = state.serverUsers.chunked(3)
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Top,
            ) {
                row.forEach { user ->
                    UserAvatarTile(user = user, onClick = { onPick(user) })
                }
                if (row.size < 3) {
                    repeat(3 - row.size) {
                        Spacer(modifier = Modifier.size(96.dp))
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
        }
        Spacer(Modifier.height(16.dp))
        if (state.enableGuest) {
            TextButton(onClick = onGuest) {
                Text(
                    text = "Continue as guest",
                    color = SwingWhite.copy(alpha = 0.85f),
                    fontSize = 13.sp
                )
            }
        }
        TextButton(onClick = onUseDifferentServer) {
            Text(
                text = "Use a different server",
                color = SwingWhite.copy(alpha = 0.5f),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun PasswordPane(
    state: AuthUiState,
    onPasswordChange: (String) -> Unit,
    onLogIn: () -> Unit,
    onSwitchUser: () -> Unit,
) {
    val username = state.username.orEmpty()
    val gradient = remember(username) { gradientForName(username) }
    val focus = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    var pwVisible by remember { mutableStateOf(false) }

    LaunchedEffect(username) { if (username.isNotBlank()) focus.requestFocus() }

    Column(
        modifier = Modifier.fillMaxWidth(0.86f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(brush = gradient)
                .border(width = 1.dp, color = Color.White.copy(alpha = 0.12f), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = (username.firstOrNull()?.uppercase() ?: "?"),
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = username,
            color = SwingWhite,
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = state.baseUrl?.trimEnd('/').orEmpty(),
            color = SwingWhite.copy(alpha = 0.4f),
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(32.dp))
        OutlinedTextField(
            value = state.password ?: "",
            onValueChange = onPasswordChange,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focus),
            singleLine = true,
            placeholder = {
                Text("Password", color = SwingWhite.copy(alpha = 0.35f))
            },
            shape = RoundedCornerShape(14.dp),
            colors = textFieldColors(),
            visualTransformation = if (pwVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (!pwVisible)
                    R.drawable.ic_password_visibility
                else R.drawable.ic_password_visibility_off
                IconButton(onClick = { pwVisible = !pwVisible }) {
                    Icon(
                        painter = painterResource(id = image),
                        contentDescription = null,
                        tint = SwingWhite.copy(alpha = 0.55f)
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                autoCorrect = false,
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Go
            ),
            keyboardActions = KeyboardActions(onGo = {
                focusManager.clearFocus(force = true)
                onLogIn()
            })
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onLogIn,
            enabled = !state.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SwingHighlightBlue,
                contentColor = SwingWhite,
                disabledContainerColor = SwingHighlightBlue.copy(alpha = 0.6f),
                disabledContentColor = SwingWhite
            ),
            shape = RoundedCornerShape(14.dp)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = SwingWhite,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Log in", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            }
        }
        Spacer(Modifier.height(12.dp))
        TextButton(onClick = onSwitchUser) {
            Text(
                text = "Choose another user",
                color = SwingWhite.copy(alpha = 0.55f),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun StatusLine(state: AuthUiState) {
    val msg = when {
        state.isLoading -> "Authenticating…" to SwingWhite.copy(alpha = 0.7f)
        state.authState == AuthState.AUTHENTICATED -> "Authenticated" to SwingHighlightBlue
        state.authError is AuthError.LoginError ->
            (state.authError as AuthError.LoginError).msg to MaterialTheme.colorScheme.error
        state.authError is AuthError.InputError ->
            (state.authError as AuthError.InputError).msg to MaterialTheme.colorScheme.error
        else -> "" to Color.Transparent
    }
    if (msg.first.isNotEmpty()) {
        Text(
            text = msg.first,
            color = msg.second,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 24.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun UserAvatarTile(user: User, onClick: () -> Unit) {
    val gradient = remember(user.username) { gradientForName(user.username) }
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .background(brush = gradient)
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.12f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = (user.username.firstOrNull()?.uppercase() ?: "?"),
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = user.username,
            color = SwingWhite,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = SwingGray5,
    unfocusedContainerColor = SwingGray5,
    disabledContainerColor = SwingGray5,
    focusedBorderColor = SwingHighlightBlue,
    unfocusedBorderColor = Color.Transparent,
    cursorColor = SwingHighlightBlue,
    focusedTextColor = SwingWhite,
    unfocusedTextColor = SwingWhite,
)

private val avatarPalettes = listOf(
    listOf(Color(0xFFFB923C), Color(0xFFEC4899), Color(0xFFA855F7)),
    listOf(Color(0xFF3B82F6), Color(0xFFA855F7), Color(0xFFEC4899)),
    listOf(Color(0xFFA855F7), Color(0xFFEC4899), Color(0xFFF472B6)),
    listOf(Color(0xFF10B981), Color(0xFF3B82F6), Color(0xFFA855F7)),
    listOf(Color(0xFFEF4444), Color(0xFFFB923C), Color(0xFFEC4899)),
    listOf(Color(0xFF14B8A6), Color(0xFF22D3EE), Color(0xFF3B82F6)),
    listOf(Color(0xFFFACC15), Color(0xFFFB923C), Color(0xFFEF4444)),
    listOf(Color(0xFF8B5CF6), Color(0xFFEC4899), Color(0xFFFB923C)),
)

private fun gradientForName(name: String): Brush {
    val palette = avatarPalettes[name.hashCode().absoluteValue % avatarPalettes.size]
    return Brush.linearGradient(
        colors = palette,
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    wallpaper = Wallpapers.RED_DOMINATED_EXAMPLE
)
@Composable
fun LoginPreview() {
    SwingMusicTheme {
        LoginRoot(
            step = LoginStep.URL,
            state = AuthUiState(baseUrl = ""),
            onBaseUrlChange = {},
            onUsernameChange = {},
            onPasswordChange = {},
            onContinue = {},
            onLogIn = {},
            onSwitchUser = {},
            onUseDifferentServer = {},
            onQrCode = {}
        )
    }
}
