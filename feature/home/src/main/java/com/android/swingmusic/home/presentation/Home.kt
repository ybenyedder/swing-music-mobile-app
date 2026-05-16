package com.android.swingmusic.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.android.swingmusic.auth.presentation.viewmodel.AuthViewModel
import com.android.swingmusic.common.presentation.navigator.CommonNavigator
import com.android.swingmusic.player.presentation.viewmodel.MediaControllerViewModel
import com.android.swingmusic.uicomponent.R
import com.android.swingmusic.uicomponent.presentation.theme.SwingBars
import com.android.swingmusic.uicomponent.presentation.theme.SwingDimens
import com.android.swingmusic.uicomponent.presentation.theme.SwingGray5
import com.android.swingmusic.uicomponent.presentation.theme.SwingGreen
import com.android.swingmusic.uicomponent.presentation.theme.SwingHighlightBlue
import com.android.swingmusic.uicomponent.presentation.theme.SwingMusicTheme
import com.android.swingmusic.uicomponent.presentation.theme.SwingPink
import com.android.swingmusic.uicomponent.presentation.theme.SwingPurple
import com.android.swingmusic.uicomponent.presentation.theme.SwingTeal
import com.android.swingmusic.uicomponent.presentation.theme.SwingWhite
import com.ramcosta.composedestinations.annotation.Destination
import java.util.Calendar
import kotlin.math.absoluteValue

private data class BrowseTile(
    val title: String,
    val iconRes: Int,
    val accent: Color,
    val onClick: () -> Unit,
)

private data class NavTab(
    val label: String,
    val onClick: () -> Unit,
    val selected: Boolean,
)

@Destination
@Composable
fun Home(
    mediaControllerViewModel: MediaControllerViewModel,
    commonNavigator: CommonNavigator,
    authViewModel: AuthViewModel,
) {
    SideEffect {
        mediaControllerViewModel.refreshBaseUrl()
    }

    val greeting = remember { greetingForHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) }

    val tiles = listOf(
        BrowseTile("Albums", R.drawable.ic_album, SwingPurple) { commonNavigator.gotoAlbums() },
        BrowseTile("Artists", R.drawable.ic_artist, SwingPink) { commonNavigator.gotoArtists() },
        BrowseTile("Playlists", R.drawable.play_list, SwingTeal) { commonNavigator.gotoPlaylists() },
        BrowseTile("Favorites", R.drawable.fav_filled, SwingGreen) { commonNavigator.gotoFavorites() },
        BrowseTile("Fav. tracks", R.drawable.fav_filled, SwingPink) { commonNavigator.gotoFavorites() },
        BrowseTile("Fav. artists", R.drawable.ic_artist, SwingPurple) { commonNavigator.gotoFavorites() },
        BrowseTile("Fav. albums", R.drawable.ic_album, SwingHighlightBlue) { commonNavigator.gotoFavorites() },
        BrowseTile("Stats", R.drawable.ic_artist, SwingTeal) { commonNavigator.gotoStats() },
    )

    val tabs = listOf(
        NavTab("Home", {}, selected = true),
        NavTab("Favorites", { commonNavigator.gotoFavorites() }, selected = false),
        NavTab("Playlists", { commonNavigator.gotoPlaylists() }, selected = false),
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
                contentPadding = PaddingValues(
                    top = SwingDimens.Large,
                    bottom = 120.dp
                ),
                verticalArrangement = Arrangement.spacedBy(SwingDimens.Large)
            ) {
                item {
                    WebHeader(
                        authViewModel = authViewModel,
                        onSearch = { commonNavigator.gotoSearch() },
                        onSettings = { commonNavigator.gotoSettings() },
                    )
                }
                item { NavTabsRow(tabs) }
                item { HomeGreeting(greeting) }
                item { BrowseGrid(tiles) }
                item { SectionHeader(title = "Recently played", action = "VIEW HISTORY") }
                item { CirclesCarousel(count = 7, kind = CircleKind.MIXED) }
                item { SectionHeader(title = "Top artists this week") }
                item { CirclesCarousel(count = 7, kind = CircleKind.ARTIST) }
            }
        }
    }
}

private fun greetingForHour(hour: Int): Pair<String, String> {
    return when {
        hour <= 3 -> "Hey there night owl" to "Late session?"
        hour <= 5 -> "Hey there early bird" to "Early start."
        hour <= 12 -> "Good morning" to "Pick something fresh."
        hour <= 17 -> "Good afternoon" to "Press play."
        else -> "Goooood evening" to "Wind down."
    }
}

@Composable
private fun WebHeader(
    authViewModel: AuthViewModel,
    onSearch: () -> Unit,
    onSettings: () -> Unit,
) {
    val loggedInUser by authViewModel.loggedInUser.collectAsStateWithLifecycle()
    var menuOpen by remember { mutableStateOf(false) }
    val displayName = loggedInUser?.let {
        listOf(it.firstname, it.lastname)
            .filter(String::isNotBlank)
            .joinToString(" ")
            .ifBlank { it.username }
    } ?: "Guest"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SwingDimens.Large, vertical = SwingDimens.Small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SwingDimens.Small)
    ) {
        // Centered bell/lamp icon on the left side (matches web header)
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(SwingHighlightBlue),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.swing_music_logo_outlined),
                contentDescription = null,
                tint = SwingWhite,
                modifier = Modifier.size(20.dp)
            )
        }
        // Search pill (flex fill)
        Row(
            modifier = Modifier
                .weight(1f)
                .height(40.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(SwingGray5)
                .clickable { onSearch() }
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_search),
                contentDescription = null,
                tint = SwingWhite.copy(alpha = 0.5f),
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "Start typing to search",
                color = SwingWhite.copy(alpha = 0.5f),
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        // Profile avatar (gradient) with dropdown
        Box {
            val gradient = remember(displayName) { gradientForName(displayName) }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(brush = gradient)
                    .clickable { menuOpen = true },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = (displayName.firstOrNull()?.uppercase() ?: "?"),
                    color = SwingWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                )
            }
            DropdownMenu(
                expanded = menuOpen,
                onDismissRequest = { menuOpen = false },
                modifier = Modifier.background(SwingBars)
            ) {
                DropdownMenuItem(
                    enabled = false,
                    text = {
                        Text(
                            text = "Hi $displayName",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold,
                        )
                    },
                    onClick = {}
                )
                DropdownMenuItem(
                    text = { Text("Quick scan", color = SwingWhite) },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_search),
                            contentDescription = null,
                            tint = SwingWhite,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    onClick = { menuOpen = false }
                )
                DropdownMenuItem(
                    text = { Text("Settings", color = SwingWhite) },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_settings),
                            contentDescription = null,
                            tint = SwingWhite,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    onClick = {
                        menuOpen = false
                        onSettings()
                    }
                )
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "Log out",
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    onClick = {
                        menuOpen = false
                        authViewModel.logout()
                    }
                )
            }
        }
    }
}

@Composable
private fun NavTabsRow(tabs: List<NavTab>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SwingDimens.Medium),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        tabs.forEach { tab ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (tab.selected) SwingGray5 else Color.Transparent)
                    .clickable(onClick = tab.onClick)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = tab.label,
                    color = if (tab.selected) SwingWhite else SwingWhite.copy(alpha = 0.55f),
                    fontWeight = if (tab.selected) FontWeight.SemiBold else FontWeight.Medium,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun HomeGreeting(text: Pair<String, String>) {
    Column(modifier = Modifier.padding(horizontal = SwingDimens.Large)) {
        Text(
            text = "Home",
            color = SwingWhite,
            fontSize = 36.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 42.sp,
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = text.first,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun BrowseGrid(tiles: List<BrowseTile>) {
    Column(modifier = Modifier.padding(horizontal = SwingDimens.Large)) {
        Text(
            text = "Browse Library",
            color = SwingWhite,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(SwingDimens.Small))
        val rows = tiles.chunked(2)
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(SwingDimens.Small)
            ) {
                row.forEach { tile ->
                    BrowseTileCard(tile = tile, modifier = Modifier.weight(1f))
                }
                if (row.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(SwingDimens.Small))
        }
    }
}

@Composable
private fun BrowseTileCard(tile: BrowseTile, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .height(64.dp)
            .clip(RoundedCornerShape(SwingDimens.RadiusMd))
            .background(SwingGray5)
            .clickable(onClick = tile.onClick)
            .padding(horizontal = SwingDimens.Medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SwingDimens.Small),
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(SwingDimens.RadiusSm))
                .background(tile.accent.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(tile.iconRes),
                contentDescription = tile.title,
                tint = tile.accent,
                modifier = Modifier.size(18.dp),
            )
        }
        Text(
            text = tile.title,
            color = SwingWhite,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
        )
    }
}

@Composable
private fun SectionHeader(title: String, action: String? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SwingDimens.Large),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            color = SwingWhite,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )
        if (action != null) {
            Text(
                text = action,
                color = SwingHighlightBlue,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                letterSpacing = 1.sp
            )
        }
    }
}

private enum class CircleKind { MIXED, ARTIST }

@Composable
private fun CirclesCarousel(count: Int, kind: CircleKind) {
    val labels = remember(kind) {
        when (kind) {
            CircleKind.MIXED -> listOf("Mix 1", "Mix 2", "Track", "Album", "Artist", "Track", "Album")
            CircleKind.ARTIST -> listOf("Artist 1", "Artist 2", "Artist 3", "Artist 4", "Artist 5", "Artist 6", "Artist 7")
        }
    }
    LazyRow(
        contentPadding = PaddingValues(horizontal = SwingDimens.Large),
        horizontalArrangement = Arrangement.spacedBy(SwingDimens.Medium)
    ) {
        items(items = labels.take(count)) { label ->
            CircleStub(label = label)
        }
    }
}

@Composable
private fun CircleStub(label: String) {
    val gradient = remember(label) { gradientForName(label) }
    Column(
        modifier = Modifier.width(96.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .background(brush = gradient)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = label,
            color = SwingWhite.copy(alpha = 0.85f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

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
