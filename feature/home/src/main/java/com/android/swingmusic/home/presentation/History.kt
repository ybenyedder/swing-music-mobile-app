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
import com.android.swingmusic.home.presentation.library.HistoryViewModel
import com.android.swingmusic.player.presentation.event.QueueEvent
import com.android.swingmusic.player.presentation.viewmodel.MediaControllerViewModel
import com.android.swingmusic.uicomponent.R
import com.android.swingmusic.uicomponent.presentation.theme.SwingDimens
import com.android.swingmusic.uicomponent.presentation.theme.SwingGray5
import com.android.swingmusic.uicomponent.presentation.theme.SwingHighlightBlue
import com.android.swingmusic.uicomponent.presentation.theme.SwingMusicTheme
import com.android.swingmusic.uicomponent.presentation.theme.SwingWhite
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun HistoryScreen(
    mediaControllerViewModel: MediaControllerViewModel,
    viewModel: HistoryViewModel = hiltViewModel(),
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
                contentPadding = PaddingValues(top = SwingDimens.Large, bottom = SwingDimens.BottomBarSpace),
                verticalArrangement = Arrangement.spacedBy(SwingDimens.Small)
            ) {
                item {
                    ScreenHeader(
                        title = stringResource(R.string.nav_history),
                        subtitle = stringResource(R.string.history_subtitle),
                        iconRes = R.drawable.ic_history,
                        accent = SwingHighlightBlue,
                    )
                }
                item { Spacer(Modifier.height(SwingDimens.Small)) }
                when {
                    state.isLoading -> item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator(color = SwingHighlightBlue) }
                    }
                    state.items.isEmpty() -> item {
                        EmptyState(
                            iconRes = R.drawable.ic_history,
                            iconTint = SwingHighlightBlue,
                            title = stringResource(R.string.history_empty_title),
                            subtitle = stringResource(R.string.history_empty_subtitle),
                        )
                    }
                    else -> items(
                        items = state.items,
                        key = { it.trackHash ?: it.hashCode().toString() }
                    ) { track ->
                        val index = state.items.indexOf(track)
                        HistoryTrackRow(
                            track = track,
                            baseUrl = state.baseUrl ?: "",
                            onClick = {
                                mediaControllerViewModel.onQueueEvent(
                                    QueueEvent.RecreateQueue(
                                        source = QueueSource.UNKNOWN,
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
private fun HistoryTrackRow(track: TrackDto, baseUrl: String, onClick: () -> Unit) {
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
    }
}
