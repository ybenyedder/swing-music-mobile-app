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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.android.swingmusic.common.presentation.navigator.CommonNavigator
import com.android.swingmusic.core.data.dto.TrackDto
import com.android.swingmusic.core.data.mapper.Map.toTrack
import com.android.swingmusic.core.domain.util.QueueSource
import com.android.swingmusic.home.presentation.library.PlaylistsViewModel
import com.android.swingmusic.player.presentation.event.QueueEvent
import com.android.swingmusic.player.presentation.viewmodel.MediaControllerViewModel
import com.android.swingmusic.uicomponent.R
import com.android.swingmusic.uicomponent.presentation.theme.SwingDimens
import com.android.swingmusic.uicomponent.presentation.theme.SwingGray5
import com.android.swingmusic.uicomponent.presentation.theme.SwingMusicTheme
import com.android.swingmusic.uicomponent.presentation.theme.SwingOrange
import com.android.swingmusic.uicomponent.presentation.theme.SwingWhite
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun PlaylistDetailScreen(
    mediaControllerViewModel: MediaControllerViewModel,
    navigator: CommonNavigator,
    playlistId: Int,
    playlistName: String,
    viewModel: PlaylistsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val baseUrl = state.baseUrl ?: ""
    var tracks by remember { mutableStateOf<List<TrackDto>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(playlistId) {
        loading = true
        tracks = viewModel.loadPlaylistTracks(playlistId)
        loading = false
    }

    SwingMusicTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = SwingDimens.Large, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(SwingDimens.Small)
            ) {
                item {
                    PlaylistDetailHeader(
                        name = playlistName,
                        trackCount = tracks.size,
                        onBack = { navigator.navigateBack() },
                        onPlayAll = {
                            if (tracks.isNotEmpty()) {
                                mediaControllerViewModel.onQueueEvent(
                                    QueueEvent.RecreateQueue(
                                        source = QueueSource.PLAYLIST(
                                            id = playlistId.toString(),
                                            name = playlistName
                                        ),
                                        queue = tracks.map { it.toTrack() },
                                        clickedTrackIndex = 0
                                    )
                                )
                            }
                        }
                    )
                }
                item { Spacer(Modifier.height(SwingDimens.Small)) }

                when {
                    loading -> item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator(color = SwingOrange) }
                    }
                    tracks.isEmpty() -> item {
                        EmptyState(
                            iconRes = R.drawable.play_list,
                            iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
                            title = androidx.compose.ui.res.stringResource(R.string.playlist_empty_title),
                            subtitle = androidx.compose.ui.res.stringResource(R.string.playlist_empty_subtitle),
                        )
                    }
                    else -> itemsIndexed(
                        items = tracks,
                        key = { _, t -> t.trackHash ?: t.hashCode().toString() }
                    ) { index, track ->
                        PlaylistTrackRow(
                            track = track,
                            baseUrl = baseUrl,
                            onClick = {
                                mediaControllerViewModel.onQueueEvent(
                                    QueueEvent.RecreateQueue(
                                        source = QueueSource.PLAYLIST(
                                            id = playlistId.toString(),
                                            name = playlistName
                                        ),
                                        queue = tracks.map { it.toTrack() },
                                        clickedTrackIndex = index
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaylistDetailHeader(
    name: String,
    trackCount: Int,
    onBack: () -> Unit,
    onPlayAll: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SwingDimens.Large),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SwingDimens.Medium),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(SwingDimens.RadiusPill))
                .background(SwingGray5)
                .clickable { onBack() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = SwingWhite,
                modifier = Modifier.size(20.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name.ifBlank { "Playlist" },
                color = SwingWhite,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "$trackCount tracks",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp,
            )
        }
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(SwingDimens.RadiusPill))
                .background(SwingOrange)
                .clickable { onPlayAll() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.play_arrow),
                contentDescription = "Play all",
                tint = Color.Black,
                modifier = Modifier.size(22.dp),
            )
        }
    }
}

@Composable
private fun PlaylistTrackRow(track: TrackDto, baseUrl: String, onClick: () -> Unit) {
    val context = LocalContext.current
    val imageUrl = remember(baseUrl, track.image) {
        "${baseUrl}img/thumbnail/small/${track.image ?: ""}"
    }
    val request = remember(imageUrl) {
        ImageRequest.Builder(context).data(imageUrl).crossfade(false).build()
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SwingDimens.Large, vertical = 6.dp)
            .clip(RoundedCornerShape(SwingDimens.RadiusMd))
            .background(SwingGray5)
            .clickable { onClick() }
            .padding(SwingDimens.Small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = request,
            contentDescription = null,
            placeholder = painterResource(R.drawable.audio_fallback),
            fallback = painterResource(R.drawable.audio_fallback),
            error = painterResource(R.drawable.audio_fallback),
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp))
        )
        Spacer(Modifier.width(SwingDimens.Small))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.title.orEmpty().ifBlank { "Unknown" },
                color = SwingWhite,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = (track.artistsDto?.joinToString { it.name.orEmpty() }
                    ?: track.albumTrackArtistDto?.joinToString { it.name.orEmpty() }
                    ?: "").ifBlank { track.album.orEmpty() },
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
