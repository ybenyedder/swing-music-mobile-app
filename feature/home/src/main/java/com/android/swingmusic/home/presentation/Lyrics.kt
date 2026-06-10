package com.android.swingmusic.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.android.swingmusic.home.presentation.library.LyricsViewModel
import com.android.swingmusic.player.presentation.viewmodel.MediaControllerViewModel
import com.android.swingmusic.uicomponent.R
import com.android.swingmusic.uicomponent.presentation.theme.SwingDimens
import com.android.swingmusic.uicomponent.presentation.theme.SwingGray
import com.android.swingmusic.uicomponent.presentation.theme.SwingHighlightBlue
import com.android.swingmusic.uicomponent.presentation.theme.SwingMusicTheme
import com.android.swingmusic.uicomponent.presentation.theme.SwingWhite
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun LyricsScreen(
    mediaControllerViewModel: MediaControllerViewModel,
    lyricsViewModel: LyricsViewModel = hiltViewModel(),
) {
    val playerState by mediaControllerViewModel.playerUiState.collectAsStateWithLifecycle()
    val lyricsState by lyricsViewModel.state.collectAsStateWithLifecycle()
    val nowPlaying = playerState.nowPlayingTrack

    LaunchedEffect(nowPlaying?.trackHash) {
        lyricsViewModel.loadFor(
            trackHash = nowPlaying?.trackHash,
            title = nowPlaying?.title,
            artist = nowPlaying?.trackArtists?.firstOrNull()?.name
                ?: nowPlaying?.albumTrackArtists?.firstOrNull()?.name,
            album = nowPlaying?.album,
            durationSec = nowPlaying?.duration,
        )
    }

    val positionMs = remember(playerState.seekPosition, nowPlaying?.duration) {
        val durSec = nowPlaying?.duration ?: 0
        (playerState.seekPosition.coerceIn(0f, 1f) * durSec * 1000f).toLong()
    }

    SwingMusicTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            androidx.compose.ui.graphics.Color(0xFF4C4C4C), // Dark gray fallback
                            androidx.compose.ui.graphics.Color(0xFF121212)
                        )
                    )
                )
                .statusBarsPadding()
        ) {
            val listState = rememberLazyListState()
            val currentIndex = lyricsViewModel.currentLineIndex(positionMs)
            LaunchedEffect(currentIndex) {
                if (currentIndex >= 0 && lyricsState.syncedLines.isNotEmpty()) {
                    listState.animateScrollToItem((currentIndex + 1).coerceAtLeast(0))
                }
            }
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 24.dp, 
                    end = 24.dp, 
                    top = 48.dp, 
                    bottom = 200.dp // Space for bottom player
                ),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                when {
                    lyricsState.isLoading -> item { LoadingState() }
                    lyricsState.syncedLines.isNotEmpty() -> {
                        itemsIndexed(lyricsState.syncedLines) { i, line ->
                            val isCurrent = i == currentIndex
                            val isPast = i < currentIndex
                            val alpha = when {
                                isCurrent -> 1f
                                else -> 0.4f
                            }
                            Text(
                                text = if (line.text.isBlank()) "♪" else line.text,
                                color = androidx.compose.ui.graphics.Color.White.copy(alpha = alpha),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 36.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Start,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                    lyricsState.plainText.isNotBlank() -> item { PlainLyrics(text = lyricsState.plainText) }
                    else -> item { LyricsEmpty(message = lyricsState.error ?: "No lyrics yet") }
                }
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(SwingDimens.Large),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = SwingHighlightBlue)
    }
}

@Composable
private fun PlainLyrics(text: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SwingDimens.Large)
    ) {
        Text(
            text = text,
            color = SwingWhite.copy(alpha = 0.85f),
            fontSize = 15.sp,
            lineHeight = 22.sp,
        )
    }
}

@Composable
private fun LyricsEmpty(message: String) {
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
            painter = painterResource(R.drawable.lyrics_icon),
            contentDescription = null,
            tint = SwingHighlightBlue,
            modifier = Modifier.size(48.dp),
        )
        Spacer(modifier = Modifier.height(SwingDimens.Medium))
        val display = when (message) {
            "No lyrics found" -> stringResource(R.string.lyrics_none)
            "No track playing" -> stringResource(R.string.lyrics_no_track)
            else -> message
        }
        Text(
            text = display,
            color = SwingWhite,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(SwingDimens.Smallest))
        Text(
            text = stringResource(R.string.lyrics_provided_by),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp,
        )
    }
}
