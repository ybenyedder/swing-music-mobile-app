package com.android.swingmusic.player.presentation.screen

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.android.swingmusic.core.domain.util.PlaybackState
import com.android.swingmusic.player.presentation.event.PlayerUiEvent
import com.android.swingmusic.player.presentation.viewmodel.MediaControllerViewModel
import com.android.swingmusic.uicomponent.R
import com.android.swingmusic.uicomponent.presentation.theme.SwingMusicTheme
import com.android.swingmusic.uicomponent.presentation.theme.SpotifyGreen
import kotlin.math.roundToInt

@Composable
private fun MiniPlayer(
    trackTitle: String,
    trackArtist: String,
    trackImage: String,
    playbackState: PlaybackState,
    isBuffering: Boolean,
    playbackProgress: Float,
    isFavorite: Boolean,
    onClickPlayerItem: () -> Unit,
    onTogglePlaybackState: () -> Unit,
    onResumePlayBackFromError: () -> Unit,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    onToggleFavorite: () -> Unit,
    baseUrl: String,
) {
    var swipeDistance by remember { mutableFloatStateOf(0F) }
    val interactions = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Surface(
            color = Color(0xFF282828), // Spotify card color
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable(
                    interactionSource = interactions,
                    indication = null
                ) {
                    onClickPlayerItem()
                }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (swipeDistance > 50) {
                                onSwipeRight()
                            } else if (swipeDistance < -50) {
                                onSwipeLeft()
                            }
                            swipeDistance = 0F
                        }
                    ) { change, dragAmount ->
                        change.consume()
                        swipeDistance += dragAmount
                    }
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF282828))
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .offset { IntOffset(swipeDistance.roundToInt() / 3, y = 0) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            model = ImageRequest.Builder(LocalContext.current)
                                .data("${baseUrl}img/thumbnail/small/${trackImage}")
                                .crossfade(true)
                                .build(),
                            placeholder = painterResource(R.drawable.audio_fallback),
                            fallback = painterResource(R.drawable.audio_fallback),
                            error = painterResource(R.drawable.audio_fallback),
                            contentDescription = "Track Image",
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = trackTitle,
                                maxLines = 1,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold, // Spotify uses bolder titles
                                overflow = TextOverflow.Ellipsis,
                                color = Color.White
                            )
                            if (trackArtist.isNotBlank()) {
                                Text(
                                    text = trackArtist,
                                    maxLines = 1,
                                    style = MaterialTheme.typography.bodySmall,
                                    overflow = TextOverflow.Ellipsis,
                                    color = Color(0xFFB3B3B3), // Spotify secondary text color
                                )
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onToggleFavorite) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (isFavorite) SpotifyGreen else Color(0xFFB3B3B3),
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                if (playbackState == PlaybackState.ERROR) {
                                    onResumePlayBackFromError()
                                } else {
                                    onTogglePlaybackState()
                                }
                            }
                        ) {
                            if (isBuffering) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp,
                                    strokeCap = StrokeCap.Round,
                                    color = Color.White
                                )
                            } else {
                                val iconRes = when (playbackState) {
                                    PlaybackState.PLAYING -> Icons.Filled.Pause
                                    else -> Icons.Filled.PlayArrow
                                }
                                Icon(
                                    imageVector = iconRes,
                                    contentDescription = "Play/Pause",
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }

                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp), // Very thin like Spotify
                    gapSize = 0.dp,
                    drawStopIndicator = {},
                    progress = { playbackProgress },
                    color = Color.White,
                    trackColor = Color(0xFF404040),
                    strokeCap = StrokeCap.Square
                )
            }
        }
    }
}

@Composable
fun MiniPlayer(
    mediaControllerViewModel: MediaControllerViewModel,
    onClickPlayerItem: () -> Unit
) {
    val playerUiState by mediaControllerViewModel.playerUiState.collectAsStateWithLifecycle()
    val baseUrl by mediaControllerViewModel.baseUrl.collectAsStateWithLifecycle()

    playerUiState.nowPlayingTrack?.let { track ->
        val artistLabel = track.trackArtists.joinToString(", ") { it.name }
        MiniPlayer(
            trackTitle = track.title,
            trackArtist = artistLabel,
            trackImage = track.image,
            playbackState = playerUiState.playbackState,
            isBuffering = playerUiState.isBuffering,
            playbackProgress = playerUiState.seekPosition,
            isFavorite = track.isFavorite,
            baseUrl = baseUrl ?: "",
            onClickPlayerItem = {
                onClickPlayerItem()
            },
            onTogglePlaybackState = {
                mediaControllerViewModel.onPlayerUiEvent(PlayerUiEvent.OnTogglePlayerState)
            },
            onSwipeLeft = {
                mediaControllerViewModel.onPlayerUiEvent(PlayerUiEvent.OnNext)
            },
            onSwipeRight = {
                mediaControllerViewModel.onPlayerUiEvent(PlayerUiEvent.OnPrev)
            },
            onResumePlayBackFromError = {
                mediaControllerViewModel.onPlayerUiEvent(PlayerUiEvent.OnResumePlaybackFromError)
            },
            onToggleFavorite = {
                mediaControllerViewModel.onPlayerUiEvent(PlayerUiEvent.OnToggleFavorite(!track.isFavorite, track.trackHash))
            }
        )
    }
}
