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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.android.swingmusic.auth.presentation.viewmodel.AuthViewModel
import com.android.swingmusic.common.presentation.navigator.CommonNavigator
import com.android.swingmusic.core.data.dto.ArtistDto
import com.android.swingmusic.core.data.dto.TrackDto
import com.android.swingmusic.core.data.mapper.Map.toTrack
import com.android.swingmusic.core.domain.util.QueueSource
import com.android.swingmusic.home.presentation.library.HomeViewModel
import com.android.swingmusic.player.presentation.event.QueueEvent
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
    homeViewModel: HomeViewModel = hiltViewModel(),
) {
    SideEffect {
        mediaControllerViewModel.refreshBaseUrl()
    }

    val homeState by homeViewModel.state.collectAsStateWithLifecycle()

    val hour = remember { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) }
    val greetingPair = greetingResForHour(hour)
    val greeting = stringResource(greetingPair.first) to stringResource(greetingPair.second)

    val tiles = listOf(
        BrowseTile(stringResource(R.string.nav_albums), R.drawable.ic_album, SwingPurple) { commonNavigator.gotoAlbums() },
        BrowseTile(stringResource(R.string.nav_artists), R.drawable.ic_artist, SwingPink) { commonNavigator.gotoArtists() },
        BrowseTile(stringResource(R.string.nav_playlists), R.drawable.play_list, SwingTeal) { commonNavigator.gotoPlaylists() },
        BrowseTile(stringResource(R.string.nav_favorites), R.drawable.fav_filled, SwingGreen) { commonNavigator.gotoFavorites() },
        BrowseTile(stringResource(R.string.nav_stats), R.drawable.ic_artist, SwingTeal) { commonNavigator.gotoStats() },
    )

    val tabs = listOf(
        NavTab(stringResource(R.string.nav_home), {}, selected = true),
        NavTab(stringResource(R.string.nav_favorites), { commonNavigator.gotoFavorites() }, selected = false),
        NavTab(stringResource(R.string.nav_playlists), { commonNavigator.gotoPlaylists() }, selected = false),
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
                item { BrowseGrid(tiles, title = stringResource(R.string.browse_library)) }
                if (homeState.recentlyPlayed.isNotEmpty()) {
                    item { SectionHeader(title = stringResource(R.string.recently_played)) }
                    item {
                        RecentlyPlayedRow(
                            tracks = homeState.recentlyPlayed,
                            baseUrl = homeState.baseUrl,
                            onClick = { index ->
                                mediaControllerViewModel.onQueueEvent(
                                    QueueEvent.RecreateQueue(
                                        source = QueueSource.UNKNOWN,
                                        queue = homeState.recentlyPlayed.map { it.toTrack() },
                                        clickedTrackIndex = index
                                    )
                                )
                            }
                        )
                    }
                }
                if (homeState.topArtists.isNotEmpty()) {
                    item { SectionHeader(title = stringResource(R.string.top_artists_week)) }
                    item {
                        TopArtistsRow(
                            artists = homeState.topArtists,
                            baseUrl = homeState.baseUrl,
                            onClick = { hash -> commonNavigator.gotoArtistInfo(hash) }
                        )
                    }
                }
            }
        }
    }
}

private fun greetingResForHour(hour: Int): Pair<Int, Int> {
    return when {
        hour <= 3 -> R.string.greeting_night to R.string.greeting_night_sub
        hour <= 5 -> R.string.greeting_early to R.string.greeting_early_sub
        hour <= 12 -> R.string.greeting_morning to R.string.greeting_morning_sub
        hour <= 17 -> R.string.greeting_afternoon to R.string.greeting_afternoon_sub
        else -> R.string.greeting_evening to R.string.greeting_evening_sub
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
                text = stringResource(R.string.search_placeholder),
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
                            text = stringResource(R.string.hi_name, displayName),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold,
                        )
                    },
                    onClick = {}
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.quick_scan), color = SwingWhite) },
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
                    text = { Text(stringResource(R.string.nav_settings), color = SwingWhite) },
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
                            text = stringResource(R.string.log_out),
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
            text = stringResource(R.string.nav_home),
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
private fun BrowseGrid(tiles: List<BrowseTile>, title: String = "Browse Library") {
    Column(modifier = Modifier.padding(horizontal = SwingDimens.Large)) {
        Text(
            text = title,
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

@Composable
private fun RecentlyPlayedRow(
    tracks: List<TrackDto>,
    baseUrl: String,
    onClick: (Int) -> Unit,
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = SwingDimens.Large),
        horizontalArrangement = Arrangement.spacedBy(SwingDimens.Medium)
    ) {
        items(items = tracks) { track ->
            val index = tracks.indexOf(track)
            TrackThumb(
                title = track.title ?: "Unknown",
                subtitle = track.artistsDto?.firstOrNull()?.name ?: "",
                imageUrl = "${baseUrl}img/thumbnail/small/${track.image ?: ""}",
                onClick = { onClick(index) }
            )
        }
    }
}

@Composable
private fun TopArtistsRow(
    artists: List<ArtistDto>,
    baseUrl: String,
    onClick: (String) -> Unit,
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = SwingDimens.Large),
        horizontalArrangement = Arrangement.spacedBy(SwingDimens.Medium)
    ) {
        items(items = artists) { artist ->
            ArtistCircle(
                name = artist.name ?: "Unknown",
                imageUrl = "${baseUrl}img/artist/small/${artist.image ?: ""}",
                onClick = { artist.artisthash?.let(onClick) }
            )
        }
    }
}

@Composable
private fun TrackThumb(
    title: String,
    subtitle: String,
    imageUrl: String,
    onClick: () -> Unit,
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .width(140.dp)
            .clip(RoundedCornerShape(SwingDimens.RadiusLg))
            .clickable(onClick = onClick)
            .padding(4.dp),
        horizontalAlignment = Alignment.Start
    ) {
        AsyncImage(
            modifier = Modifier
                .size(132.dp)
                .clip(RoundedCornerShape(SwingDimens.RadiusMd)),
            model = ImageRequest.Builder(context)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.audio_fallback),
            error = painterResource(R.drawable.audio_fallback),
            fallback = painterResource(R.drawable.audio_fallback),
            contentDescription = title,
            contentScale = ContentScale.Crop,
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = title,
            color = SwingWhite,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 16.sp,
        )
        if (subtitle.isNotBlank()) {
            Spacer(Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun ArtistCircle(
    name: String,
    imageUrl: String,
    onClick: () -> Unit,
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .width(108.dp)
            .clip(RoundedCornerShape(SwingDimens.RadiusLg))
            .clickable(onClick = onClick)
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape),
            model = ImageRequest.Builder(context)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.artist_fallback),
            error = painterResource(R.drawable.artist_fallback),
            fallback = painterResource(R.drawable.artist_fallback),
            contentDescription = name,
            contentScale = ContentScale.Crop,
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = name,
            color = SwingWhite,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
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
