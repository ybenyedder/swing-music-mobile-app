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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.android.swingmusic.auth.presentation.viewmodel.AuthViewModel
import com.android.swingmusic.common.presentation.navigator.CommonNavigator
import com.android.swingmusic.uicomponent.R
import com.android.swingmusic.uicomponent.presentation.theme.SwingDimens
import com.android.swingmusic.uicomponent.presentation.theme.SwingGray
import com.android.swingmusic.uicomponent.presentation.theme.SwingGray5
import com.android.swingmusic.uicomponent.presentation.theme.SwingGreen
import com.android.swingmusic.uicomponent.presentation.theme.SwingHighlightBlue
import com.android.swingmusic.uicomponent.presentation.theme.SwingMusicTheme
import com.android.swingmusic.uicomponent.presentation.theme.SwingOrange
import com.android.swingmusic.uicomponent.presentation.theme.SwingPink
import com.android.swingmusic.uicomponent.presentation.theme.SwingPurple
import com.android.swingmusic.uicomponent.presentation.theme.SwingTeal
import com.android.swingmusic.uicomponent.presentation.theme.SwingWhite
import com.android.swingmusic.uicomponent.presentation.theme.SwingYellow
import com.ramcosta.composedestinations.annotation.Destination
import kotlin.math.absoluteValue

private data class SettingsItem(
    val title: String,
    val subtitle: String,
    val iconRes: Int,
    val accent: Color,
    val onClick: () -> Unit,
)

private data class QuickAction(
    val label: String,
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

    val quickActions = listOf(
        QuickAction(stringResource(R.string.nav_search), R.drawable.ic_search, SwingHighlightBlue) { commonNavigator.gotoSearch() },
        QuickAction(stringResource(R.string.nav_pair), R.drawable.swing_music_logo_outlined, SwingTeal) { commonNavigator.gotoLoginWithQrCode() },
        QuickAction(stringResource(R.string.nav_theme), R.drawable.swing_music_logo_outlined, SwingPurple) { commonNavigator.gotoAppearance() },
        QuickAction(stringResource(R.string.nav_about), R.drawable.swing_music_logo_outlined, SwingOrange) { commonNavigator.gotoAbout() },
    )

    val librarySection = listOf(
        SettingsItem(stringResource(R.string.nav_folders), stringResource(R.string.settings_folders_sub), R.drawable.folder_filled, SwingHighlightBlue) { commonNavigator.gotoFolders() },
        SettingsItem(stringResource(R.string.nav_albums), stringResource(R.string.settings_albums_sub), R.drawable.ic_album, SwingPurple) { commonNavigator.gotoAlbums() },
        SettingsItem(stringResource(R.string.nav_artists), stringResource(R.string.settings_artists_sub), R.drawable.ic_artist, SwingPink) { commonNavigator.gotoArtists() },
        SettingsItem(stringResource(R.string.nav_stats), stringResource(R.string.settings_stats_sub), R.drawable.ic_artist, SwingYellow) { commonNavigator.gotoStats() },
    )

    val audioSection = listOf(
        SettingsItem(stringResource(R.string.nav_lyrics), stringResource(R.string.settings_lyrics_sub), R.drawable.lyrics_icon, SwingPink) { commonNavigator.gotoLyrics() },
        SettingsItem(stringResource(R.string.player_now_playing), stringResource(R.string.settings_playback_sub), R.drawable.play_arrow, SwingTeal) {},
        SettingsItem("Last.fm", stringResource(R.string.settings_lastfm_sub), R.drawable.swing_music_logo_outlined, SwingOrange) {},
    )

    val accountSection = listOf(
        SettingsItem(stringResource(R.string.nav_accounts), stringResource(R.string.settings_accounts_sub), R.drawable.ic_artist, SwingHighlightBlue) { commonNavigator.gotoAccounts() },
        SettingsItem(stringResource(R.string.nav_appearance), stringResource(R.string.settings_appearance_sub), R.drawable.swing_music_logo_outlined, SwingPurple) { commonNavigator.gotoAppearance() },
        SettingsItem(stringResource(R.string.nav_about), stringResource(R.string.settings_about_sub), R.drawable.swing_music_logo_outlined, SwingGreen) { commonNavigator.gotoAbout() },
    )

    SwingMusicTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = SwingDimens.Large, bottom = SwingDimens.BottomBarSpace),
                verticalArrangement = Arrangement.spacedBy(SwingDimens.Medium)
            ) {
                item {
                    Text(
                        modifier = Modifier.padding(horizontal = SwingDimens.Large),
                        text = stringResource(R.string.settings_title),
                        color = SwingWhite,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
                item {
                    HeroCard(
                        displayName = displayName,
                        username = username,
                        serverUrl = serverUrl,
                        isAdmin = isAdmin,
                        onClick = { commonNavigator.gotoAccounts() }
                    )
                }
                item { QuickActionsRow(actions = quickActions) }

                item { SectionLabel(stringResource(R.string.settings_section_library)) }
                item { GroupedCard(items = librarySection) }

                item { SectionLabel(stringResource(R.string.settings_section_audio)) }
                item { GroupedCard(items = audioSection) }

                item { SectionLabel(stringResource(R.string.settings_section_account)) }
                item { GroupedCard(items = accountSection) }

                if (isAdmin) {
                    item { SectionLabel(stringResource(R.string.settings_section_admin)) }
                    item {
                        GroupedCard(
                            items = listOf(
                                SettingsItem(
                                    stringResource(R.string.settings_manage_users),
                                    stringResource(R.string.settings_manage_users_sub),
                                    R.drawable.ic_artist,
                                    SwingTeal
                                ) { commonNavigator.gotoAdminUsers() }
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroCard(
    displayName: String,
    username: String,
    serverUrl: String,
    isAdmin: Boolean,
    onClick: () -> Unit,
) {
    val gradient = remember(displayName) { gradientForName(displayName) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SwingDimens.Large)
            .clip(RoundedCornerShape(24.dp))
            .background(SwingGray5)
            .clickable(onClick = onClick)
            .padding(SwingDimens.Large),
        verticalArrangement = Arrangement.spacedBy(SwingDimens.Medium)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(brush = gradient),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = (displayName.firstOrNull()?.uppercase() ?: "?"),
                    color = SwingWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                )
            }
            Spacer(Modifier.width(SwingDimens.Medium))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = displayName,
                        color = SwingWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (isAdmin) {
                        Spacer(Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(SwingTeal.copy(alpha = 0.22f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.settings_admin_badge),
                                color = SwingTeal,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                letterSpacing = 0.8.sp,
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
            }
        }
        if (serverUrl.isNotBlank()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SwingGray)
                    .padding(horizontal = SwingDimens.Medium, vertical = SwingDimens.Small),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(SwingGreen)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = serverUrl,
                    color = SwingWhite.copy(alpha = 0.8f),
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun QuickActionsRow(actions: List<QuickAction>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SwingDimens.Large),
        horizontalArrangement = Arrangement.spacedBy(SwingDimens.Small)
    ) {
        actions.forEach { action ->
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(SwingGray5)
                    .clickable(onClick = action.onClick)
                    .padding(vertical = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(action.accent.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(action.iconRes),
                        contentDescription = action.label,
                        tint = action.accent,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = action.label,
                    color = SwingWhite,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
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
                top = SwingDimens.Small,
                bottom = SwingDimens.Smallest
            ),
        text = text.uppercase(),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
    )
}

@Composable
private fun GroupedCard(items: List<SettingsItem>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SwingDimens.Large)
            .clip(RoundedCornerShape(20.dp))
            .background(SwingGray5)
    ) {
        items.forEachIndexed { index, item ->
            SettingsRow(item)
            if (index < items.lastIndex) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 68.dp)
                        .height(1.dp)
                        .background(SwingWhite.copy(alpha = 0.08f))
                )
            }
        }
    }
}

@Composable
private fun SettingsRow(item: SettingsItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = item.onClick)
            .padding(horizontal = SwingDimens.Medium, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
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
        Spacer(Modifier.width(SwingDimens.Medium))
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
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
            contentDescription = null,
            tint = SwingWhite.copy(alpha = 0.45f),
            modifier = Modifier.size(20.dp),
        )
    }
}

private val settingsPalettes = listOf(
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
    val palette = settingsPalettes[name.hashCode().absoluteValue % settingsPalettes.size]
    return Brush.linearGradient(
        colors = palette,
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )
}
