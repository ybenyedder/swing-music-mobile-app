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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.android.swingmusic.core.data.dto.TrackDto
import com.android.swingmusic.core.data.mapper.Map.toTrack
import com.android.swingmusic.core.domain.util.QueueSource
import com.android.swingmusic.home.presentation.library.FavoritesViewModel
import com.android.swingmusic.player.presentation.event.QueueEvent
import com.android.swingmusic.player.presentation.viewmodel.MediaControllerViewModel
import com.android.swingmusic.uicomponent.R
import com.android.swingmusic.uicomponent.presentation.theme.SwingDimens
import com.android.swingmusic.uicomponent.presentation.theme.SwingGray
import com.android.swingmusic.uicomponent.presentation.theme.SwingGray5
import com.android.swingmusic.uicomponent.presentation.theme.SwingMusicTheme
import com.android.swingmusic.uicomponent.presentation.theme.SwingPink
import com.android.swingmusic.uicomponent.presentation.theme.SwingWhite
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun FavoritesScreen(
    mediaControllerViewModel: MediaControllerViewModel,
    viewModel: FavoritesViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    SwingMusicTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = SwingDimens.BottomBarSpace),
            ) {
                item {
                    // Header Section
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                androidx.compose.ui.graphics.Brush.verticalGradient(
                                    colors = listOf(androidx.compose.ui.graphics.Color(0xFF450E74).copy(alpha = 0.8f), androidx.compose.ui.graphics.Color(0xFF121212))
                                )
                            )
                            .padding(top = 32.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
                    ) {
                        Column {
                            // Search & Back icons could go here
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_search),
                                    contentDescription = "Search",
                                    tint = androidx.compose.ui.graphics.Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(32.dp))
                            
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(
                                            androidx.compose.ui.graphics.Brush.linearGradient(
                                                colors = listOf(androidx.compose.ui.graphics.Color(0xFF450E74), androidx.compose.ui.graphics.Color(0xFFC4B2F3))
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.fav_filled),
                                        contentDescription = "Liked Songs",
                                        tint = androidx.compose.ui.graphics.Color.White,
                                        modifier = Modifier.size(48.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = stringResource(R.string.nav_favorites), // Usually "Liked Songs"
                                        color = SwingWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 24.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = stringResource(R.string.favorites_tracks_count, state.items.size),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Play button and Shuffle
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.arrow_downward), // Download icon
                                        contentDescription = "Download",
                                        tint = androidx.compose.ui.graphics.Color.Gray,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                                
                                IconButton(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(RoundedCornerShape(32.dp))
                                        .background(com.android.swingmusic.uicomponent.presentation.theme.SpotifyGreen),
                                    onClick = {
                                        if (state.items.isNotEmpty()) {
                                            mediaControllerViewModel.onQueueEvent(
                                                QueueEvent.RecreateQueue(
                                                    source = QueueSource.FAVORITE,
                                                    queue = state.items.map { it.toTrack() },
                                                    clickedTrackIndex = 0
                                                )
                                            )
                                        }
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.play_arrow_fill),
                                        tint = androidx.compose.ui.graphics.Color.Black,
                                        contentDescription = "Play Icon",
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                when {
                    state.isLoading -> item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator(color = SwingPink) }
                    }
                    state.error != null -> item {
                        EmptyState(
                            iconRes = R.drawable.fav_filled,
                            iconTint = MaterialTheme.colorScheme.error,
                            title = stringResource(R.string.favorites_load_error),
                            subtitle = state.error.orEmpty()
                        )
                    }
                    state.items.isEmpty() -> item { FavoritesEmptyState() }
                    else -> items(
                        items = state.items,
                        key = { it.trackHash ?: it.hashCode().toString() }
                    ) { track ->
                        val index = state.items.indexOf(track)
                        TrackRow(
                            track = track,
                            baseUrl = state.baseUrl ?: "",
                            onClick = {
                                mediaControllerViewModel.onQueueEvent(
                                    QueueEvent.RecreateQueue(
                                        source = QueueSource.FAVORITE,
                                        queue = state.items.map { it.toTrack() },
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
private fun TrackRow(track: TrackDto, baseUrl: String, onClick: () -> Unit) {
    val context = LocalContext.current
    val imageUrl = remember(baseUrl, track.image) {
        "${baseUrl}img/thumbnail/small/${track.image ?: ""}"
    }
    val request = remember(imageUrl) {
        ImageRequest.Builder(context)
            .data(imageUrl)
            .crossfade(false)
            .build()
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
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Spacer(Modifier.width(SwingDimens.Small))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.title.orEmpty().ifBlank { stringResource(R.string.unknown) },
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
        Icon(
            painter = painterResource(R.drawable.fav_filled),
            contentDescription = null,
            tint = SwingPink,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun FavoritesEmptyState() {
    EmptyState(
        iconRes = R.drawable.fav_filled,
        iconTint = SwingPink,
        title = stringResource(R.string.favorites_empty_title),
        subtitle = stringResource(R.string.favorites_empty_subtitle),
    )
}

@Composable
internal fun EmptyState(
    iconRes: Int,
    iconTint: androidx.compose.ui.graphics.Color,
    title: String,
    subtitle: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SwingDimens.Large)
            .padding(top = SwingDimens.Larger)
            .clip(RoundedCornerShape(SwingDimens.RadiusLg))
            .background(SwingGray)
            .padding(SwingDimens.Large),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(48.dp),
        )
        Spacer(modifier = Modifier.height(SwingDimens.Medium))
        Text(
            text = title,
            color = SwingWhite,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(SwingDimens.Smallest))
        Text(
            text = subtitle,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 13.sp,
        )
    }
}
