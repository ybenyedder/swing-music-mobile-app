package com.android.swingmusic.home.presentation

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.android.swingmusic.auth.presentation.viewmodel.AuthViewModel
import com.android.swingmusic.common.presentation.navigator.CommonNavigator
import com.android.swingmusic.uicomponent.R
import com.android.swingmusic.uicomponent.presentation.theme.SwingDimens
import com.android.swingmusic.uicomponent.presentation.theme.SwingGray5
import com.android.swingmusic.uicomponent.presentation.theme.SwingHighlightBlue
import com.android.swingmusic.uicomponent.presentation.theme.SwingMusicTheme
import com.android.swingmusic.uicomponent.presentation.theme.SwingOrange
import com.android.swingmusic.uicomponent.presentation.theme.SwingPink
import com.android.swingmusic.uicomponent.presentation.theme.SwingPurple
import com.android.swingmusic.uicomponent.presentation.theme.SwingTeal
import com.android.swingmusic.uicomponent.presentation.theme.SwingWhite
import com.ramcosta.composedestinations.annotation.Destination

private data class SettingsItem(
    val title: String,
    val subtitle: String,
    val iconRes: Int,
    val accent: Color,
    val onClick: () -> Unit,
)

@Destination
@Composable
fun SettingsScreen(commonNavigator: CommonNavigator, authViewModel: AuthViewModel) {
    val loggedInUser by authViewModel.loggedInUser.collectAsStateWithLifecycle()
    val authUiState by authViewModel.authUiState.collectAsStateWithLifecycle()

    val displayName = loggedInUser?.let {
        listOf(it.firstname, it.lastname)
            .filter(String::isNotBlank)
            .joinToString(" ")
            .ifBlank { it.username }
    } ?: "Guest"
    val username = loggedInUser?.username?.let { "@$it" } ?: ""
    val serverUrl = authUiState.baseUrl?.trimEnd('/') ?: ""
    val isAdmin = loggedInUser?.roles?.any { it.equals("admin", ignoreCase = true) } == true

    SwingMusicTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 80.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(SwingDimens.Small)
            ) {
                item {
                    Text(
                        modifier = Modifier.padding(horizontal = SwingDimens.Large),
                        text = "Settings",
                        color = SwingWhite,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                item { Spacer(modifier = Modifier.height(SwingDimens.Small)) }
                item {
                    UserHeaderCard(
                        displayName = displayName,
                        username = username,
                        serverUrl = serverUrl,
                        isAdmin = isAdmin,
                        onClick = { commonNavigator.gotoAccounts() }
                    )
                }
                item { SectionLabel("General") }
                item {
                    SettingsRow(SettingsItem(
                        "Appearance", "Sidebar layout, list mode, inline favorite",
                        R.drawable.swing_music_logo_outlined, SwingPurple
                    ) { commonNavigator.gotoAppearance() })
                }
                item {
                    SettingsRow(SettingsItem(
                        "Profile", "Username, email, server", R.drawable.ic_artist, SwingHighlightBlue
                    ) { commonNavigator.gotoAccounts() })
                }
                item {
                    SettingsRow(SettingsItem(
                        "Pair device", "Scan QR to pair another server", R.drawable.ic_search, SwingTeal
                    ) { commonNavigator.gotoLoginWithQrCode() })
                }
                item {
                    SettingsRow(SettingsItem(
                        "Accounts", "Switch or add accounts", R.drawable.ic_artist, SwingHighlightBlue
                    ) { commonNavigator.gotoAccounts() })
                }

                item { SectionLabel("Library") }
                item {
                    SettingsRow(SettingsItem(
                        "Folders", "Root directories and show options",
                        R.drawable.folder_filled, SwingHighlightBlue
                    ) { commonNavigator.gotoFolders() })
                }
                item {
                    SettingsRow(SettingsItem(
                        "Tracks", "Sorting, metadata", R.drawable.play_arrow, SwingTeal
                    ) {})
                }
                item {
                    SettingsRow(SettingsItem(
                        "Albums", "Grid count, sorting", R.drawable.ic_album, SwingPurple
                    ) { commonNavigator.gotoAlbums() })
                }
                item {
                    SettingsRow(SettingsItem(
                        "Artists", "Grid count, sorting", R.drawable.ic_artist, SwingPink
                    ) { commonNavigator.gotoArtists() })
                }
                item {
                    SettingsRow(SettingsItem(
                        "Backup", "Export and restore (coming soon)",
                        R.drawable.swing_music_logo_outlined, SwingTeal
                    ) {})
                }

                item { SectionLabel("Audio") }
                item {
                    SettingsRow(SettingsItem(
                        "Playback", "Crossfade, gapless, silence detection",
                        R.drawable.play_arrow, SwingTeal
                    ) {})
                }

                item { SectionLabel("Plugins") }
                item {
                    SettingsRow(SettingsItem(
                        "Lyrics", "Synced lyrics, auto-fetch", R.drawable.lyrics_icon, SwingPink
                    ) {})
                }
                item {
                    SettingsRow(SettingsItem(
                        "Last.fm", "Scrobbling (coming soon)",
                        R.drawable.swing_music_logo_outlined, SwingOrange
                    ) {})
                }

                if (isAdmin) {
                    item { SectionLabel("Admin") }
                    item {
                        SettingsRow(SettingsItem(
                            "Manage server users", "Create users and assign roles",
                            R.drawable.ic_artist, SwingTeal
                        ) { commonNavigator.gotoAdminUsers() })
                    }
                }

                item { Spacer(modifier = Modifier.height(SwingDimens.Medium)) }
                item {
                    SettingsRow(SettingsItem(
                        "About", "Version, changelog, links",
                        R.drawable.swing_music_logo_outlined, SwingTeal
                    ) { commonNavigator.gotoAbout() })
                }
            }
        }
    }
}

@Composable
private fun UserHeaderCard(
    displayName: String,
    username: String,
    serverUrl: String,
    isAdmin: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SwingDimens.Large)
            .clip(RoundedCornerShape(SwingDimens.RadiusLg))
            .background(SwingGray5)
            .clickable(onClick = onClick)
            .padding(SwingDimens.Medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(SwingHighlightBlue.copy(alpha = 0.22f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = (displayName.firstOrNull()?.uppercase() ?: "?"),
                color = SwingHighlightBlue,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
            )
        }
        Spacer(modifier = Modifier.width(SwingDimens.Medium))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = displayName,
                    color = SwingWhite,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 17.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (isAdmin) {
                    Spacer(Modifier.width(8.dp))
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
            if (username.isNotBlank()) {
                Text(
                    text = username,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (serverUrl.isNotBlank()) {
                Text(
                    text = serverUrl,
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
private fun SectionLabel(text: String) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = SwingDimens.Large,
                end = SwingDimens.Large,
                top = SwingDimens.Medium,
                bottom = SwingDimens.Smaller
            ),
        text = text.uppercase(),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
    )
}

@Composable
private fun SettingsRow(item: SettingsItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SwingDimens.Large)
            .clip(RoundedCornerShape(SwingDimens.RadiusMd))
            .background(SwingGray5)
            .clickable(onClick = item.onClick)
            .padding(horizontal = SwingDimens.Medium, vertical = SwingDimens.Medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(SwingDimens.RadiusSm))
                .background(item.accent.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(item.iconRes),
                contentDescription = item.title,
                tint = item.accent,
                modifier = Modifier.size(20.dp),
            )
        }
        Spacer(modifier = Modifier.width(SwingDimens.Small))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                color = SwingWhite,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
            )
            Text(
                text = item.subtitle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
            )
        }
    }
}
