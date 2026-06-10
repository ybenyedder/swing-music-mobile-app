package com.android.swingmusic.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.android.swingmusic.auth.presentation.viewmodel.AuthViewModel
import com.android.swingmusic.common.presentation.navigator.CommonNavigator
import com.android.swingmusic.core.data.dto.ArtistDto
import com.android.swingmusic.core.data.dto.TrackDto
import com.android.swingmusic.core.data.mapper.Map.toTrack
import com.android.swingmusic.core.domain.util.QueueSource
import com.android.swingmusic.home.presentation.library.HomeViewModel
import com.android.swingmusic.home.presentation.library.PlaylistsViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.android.swingmusic.player.presentation.event.QueueEvent
import com.android.swingmusic.player.presentation.viewmodel.MediaControllerViewModel
import com.android.swingmusic.uicomponent.R
import com.android.swingmusic.uicomponent.presentation.theme.SpotifyBlack
import com.android.swingmusic.uicomponent.presentation.theme.SpotifyGreen
import com.android.swingmusic.uicomponent.presentation.theme.SpotifyWhite
import com.android.swingmusic.uicomponent.presentation.theme.SpotifyLightGray
import com.ramcosta.composedestinations.annotation.Destination
import kotlin.math.absoluteValue

@Destination
@Composable
fun Home(
    mediaControllerViewModel: MediaControllerViewModel,
    commonNavigator: CommonNavigator,
    authViewModel: AuthViewModel,
    homeViewModel: HomeViewModel = hiltViewModel(),
    playlistsViewModel: PlaylistsViewModel = hiltViewModel(),
) {
    val homeState by homeViewModel.state.collectAsStateWithLifecycle()
    val playlistsState by playlistsViewModel.state.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableStateOf("All") }
    val tabs = listOf("All", "Music", "Podcasts")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SpotifyBlack)
    ) {
        // Subtle top gradient (Spotify style)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF3B82F6).copy(alpha = 0.3f), // Dynamic color usually, fallback to soft blue
                            SpotifyBlack
                        )
                    )
                )
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp), // Space for MiniPlayer
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            item {
                SpotifyTopBar(
                    authViewModel = authViewModel,
                    selectedTab = selectedTab,
                    tabs = tabs,
                    onTabSelected = { selectedTab = it },
                    commonNavigator = commonNavigator
                )
            }

            item {
                SpotifyGrid(
                    recentlyPlayed = homeState.recentlyPlayed,
                    baseUrl = homeState.baseUrl ?: "",
                    commonNavigator = commonNavigator,
                    mediaControllerViewModel = mediaControllerViewModel
                )
            }

            if (homeState.recentlyPlayed.isNotEmpty()) {
                item {
                    SpotifyCarousel(
                        title = "History",
                        tracks = homeState.recentlyPlayed,
                        baseUrl = homeState.baseUrl ?: "",
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

            if (playlistsState.items.isNotEmpty()) {
                item {
                    SpotifyPlaylistCarousel(
                        title = "Your Playlists",
                        playlists = playlistsState.items,
                        baseUrl = playlistsState.baseUrl ?: "",
                        onClick = { id, name -> commonNavigator.gotoPlaylistDetail(id, name) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SpotifyTopBar(
    authViewModel: AuthViewModel,
    selectedTab: String,
    tabs: List<String>,
    onTabSelected: (String) -> Unit,
    commonNavigator: CommonNavigator
) {
    val loggedInUser by authViewModel.loggedInUser.collectAsStateWithLifecycle()
    val displayName = loggedInUser?.let {
        listOf(it.firstname, it.lastname)
            .filter(String::isNotBlank)
            .joinToString(" ")
            .ifBlank { it.username }
    } ?: "Guest"
    
    val palette = listOf(Color(0xFF3B82F6), Color(0xFFA855F7), Color(0xFFEC4899))
    val gradient = Brush.linearGradient(colors = palette)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile icon
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(brush = gradient)
                .clickable { commonNavigator.gotoSettings() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = (displayName.firstOrNull()?.uppercase() ?: "S"),
                color = SpotifyWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Pills
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tabs.forEach { tab ->
                val isSelected = selectedTab == tab
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(32.dp))
                        .background(if (isSelected) SpotifyGreen else Color(0xFF282828))
                        .clickable { onTabSelected(tab) }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab,
                        color = if (isSelected) SpotifyBlack else SpotifyWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun SpotifyGrid(
    recentlyPlayed: List<TrackDto>,
    baseUrl: String,
    commonNavigator: CommonNavigator,
    mediaControllerViewModel: MediaControllerViewModel,
) {
    val context = LocalContext.current
    val items = remember(recentlyPlayed, baseUrl) {
        val list = mutableListOf<BrowseTile>()
        list.add(
            BrowseTile("Liked Songs", R.drawable.fav_filled, Color(0xFF5E5CE6)) { commonNavigator.gotoFavorites() }
        )
        recentlyPlayed.take(5).forEachIndexed { idx, track ->
            list.add(
                BrowseTile(
                    title = track.title ?: "Unknown",
                    iconRes = null,
                    accent = Color.Transparent,
                    onClick = {
                        mediaControllerViewModel.onQueueEvent(
                            QueueEvent.RecreateQueue(
                                source = QueueSource.UNKNOWN,
                                queue = recentlyPlayed.map { it.toTrack() },
                                clickedTrackIndex = idx
                            )
                        )
                    }
                )
            )
        }
        val fallbacks = listOf(
            BrowseTile("Artists", R.drawable.ic_artist, Color(0xFFE22134)) { commonNavigator.gotoArtists() },
            BrowseTile("Albums", R.drawable.ic_album, Color(0xFF40C8E0)) { commonNavigator.gotoAlbums() },
            BrowseTile("History", R.drawable.ic_history, Color(0xFFFFD60A)) { commonNavigator.gotoHistory() },
        )
        for (fallback in fallbacks) {
            if (list.size >= 8) break
            list.add(fallback)
        }
        list.take(8)
    }

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        val rows = items.chunked(2)
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { item ->
                    val isTrack = item.iconRes == null
                    val trackIndex = if (isTrack) {
                        recentlyPlayed.indexOfFirst { it.title == item.title }
                    } else -1

                    val image = if (isTrack && trackIndex != -1) {
                        "${baseUrl}img/thumbnail/small/${recentlyPlayed[trackIndex].image ?: ""}"
                    } else null

                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF282828)) // Spotify Grid color
                            .clickable(onClick = item.onClick),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (image != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(image)
                                    .crossfade(true)
                                    .build(),
                                placeholder = painterResource(R.drawable.audio_fallback),
                                fallback = painterResource(R.drawable.audio_fallback),
                                error = painterResource(R.drawable.audio_fallback),
                                contentDescription = item.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp))
                            )
                        } else if (item.iconRes != null) {
                            val gradient = if (item.title == "Liked Songs") {
                                Brush.linearGradient(colors = listOf(Color(0xFF450E74), Color(0xFFC4B2F3)))
                            } else null
                            
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp))
                                    .background(gradient ?: Brush.linearGradient(listOf(item.accent.copy(alpha=0.3f), item.accent.copy(alpha=0.1f)))),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(item.iconRes),
                                    contentDescription = item.title,
                                    tint = if (item.title == "Liked Songs") SpotifyWhite else item.accent,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = item.title,
                            color = SpotifyWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                        )
                    }
                }
                if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

private data class BrowseTile(
    val title: String,
    val iconRes: Int?,
    val accent: Color,
    val onClick: () -> Unit,
)

@Composable
private fun SpotifyCarousel(
    title: String,
    tracks: List<TrackDto>,
    baseUrl: String,
    onClick: (Int) -> Unit,
) {
    Column {
        Text(
            text = title,
            color = SpotifyWhite,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(items = tracks) { track ->
                val index = tracks.indexOf(track)
                val context = LocalContext.current
                Column(
                    modifier = Modifier
                        .width(140.dp)
                        .clickable { onClick(index) },
                    horizontalAlignment = Alignment.Start
                ) {
                    AsyncImage(
                        modifier = Modifier.size(140.dp), // No rounded corners for albums usually, but maybe 2dp
                        model = ImageRequest.Builder(context)
                            .data("${baseUrl}img/thumbnail/small/${track.image ?: ""}")
                            .crossfade(true)
                            .build(),
                        placeholder = painterResource(R.drawable.audio_fallback),
                        error = painterResource(R.drawable.audio_fallback),
                        fallback = painterResource(R.drawable.audio_fallback),
                        contentDescription = track.title,
                        contentScale = ContentScale.Crop,
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = track.title ?: "Unknown",
                        color = SpotifyWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = track.artistsDto?.firstOrNull()?.name ?: "",
                        color = SpotifyLightGray,
                        fontSize = 12.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun SpotifyPlaylistCarousel(
    title: String,
    playlists: List<com.android.swingmusic.core.data.dto.PlaylistDto>,
    baseUrl: String,
    onClick: (Int, String) -> Unit,
) {
    Column {
        Text(
            text = title,
            color = SpotifyWhite,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(items = playlists) { playlist ->
                val context = LocalContext.current
                val imageName = playlist.image ?: playlist.thumb
                val imageUrl = remember(baseUrl, imageName) {
                    if (!imageName.isNullOrBlank()) "${baseUrl}img/playlist/${imageName}" else ""
                }
                
                Column(
                    modifier = Modifier
                        .width(140.dp)
                        .clickable { playlist.id?.let { id -> onClick(id, playlist.name ?: "") } },
                    horizontalAlignment = Alignment.Start
                ) {
                    AsyncImage(
                        modifier = Modifier
                            .size(140.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        model = ImageRequest.Builder(context)
                            .data(imageUrl)
                            .crossfade(true)
                            .build(),
                        placeholder = painterResource(R.drawable.play_list),
                        error = painterResource(R.drawable.play_list),
                        fallback = painterResource(R.drawable.play_list),
                        contentDescription = playlist.name,
                        contentScale = ContentScale.Crop,
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = playlist.name ?: "Unknown",
                        color = SpotifyWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Playlist",
                        color = SpotifyLightGray,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}
