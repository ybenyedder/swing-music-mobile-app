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
import com.android.swingmusic.common.presentation.navigator.CommonNavigator
import com.android.swingmusic.core.data.dto.PlaylistDto
import com.android.swingmusic.home.presentation.library.PlaylistsViewModel
import com.android.swingmusic.uicomponent.R
import com.android.swingmusic.uicomponent.presentation.theme.SwingDimens
import com.android.swingmusic.uicomponent.presentation.theme.SwingGray5
import com.android.swingmusic.uicomponent.presentation.theme.SwingMusicTheme
import com.android.swingmusic.uicomponent.presentation.theme.SwingOrange
import com.android.swingmusic.uicomponent.presentation.theme.SwingWhite
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun PlaylistsScreen(
    navigator: CommonNavigator,
    viewModel: PlaylistsViewModel = hiltViewModel(),
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
                        stringResource(R.string.nav_playlists),
                        stringResource(R.string.playlist_tracks_count, state.items.size),
                        R.drawable.play_list,
                        SwingOrange
                    )
                }
                item { Spacer(Modifier.height(SwingDimens.Small)) }
                when {
                    state.isLoading -> item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator(color = SwingOrange) }
                    }
                    state.error != null -> item {
                        EmptyState(
                            iconRes = R.drawable.play_list,
                            iconTint = MaterialTheme.colorScheme.error,
                            title = stringResource(R.string.playlists_load_error),
                            subtitle = state.error.orEmpty(),
                        )
                    }
                    state.items.isEmpty() -> item { PlaylistsEmptyState() }
                    else -> items(items = state.items, key = { it.id ?: 0 }) { playlist ->
                        PlaylistRow(
                            playlist = playlist,
                            baseUrl = state.baseUrl ?: "",
                            onClick = {
                                val pid = playlist.id ?: return@PlaylistRow
                                navigator.gotoPlaylistDetail(pid, playlist.name.orEmpty())
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaylistRow(playlist: PlaylistDto, baseUrl: String, onClick: () -> Unit) {
    val context = LocalContext.current
    val imageName = playlist.image ?: playlist.thumb
    val imageUrl = remember(baseUrl, imageName) {
        if (!imageName.isNullOrBlank()) "${baseUrl}img/playlist/${imageName}" else ""
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
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(SwingOrange.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            if (imageUrl.isNotBlank()) {
                AsyncImage(
                    model = request,
                    contentDescription = null,
                    placeholder = painterResource(R.drawable.audio_fallback),
                    fallback = painterResource(R.drawable.audio_fallback),
                    error = painterResource(R.drawable.audio_fallback),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(56.dp).clip(RoundedCornerShape(10.dp))
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.play_list),
                    contentDescription = null,
                    tint = SwingOrange,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(Modifier.width(SwingDimens.Small))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = playlist.name.orEmpty().ifBlank { stringResource(R.string.playlist_untitled) },
                color = SwingWhite,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = stringResource(R.string.playlist_tracks_count, playlist.count ?: 0),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
            )
        }
    }
}

@Composable
internal fun ScreenHeader(title: String, subtitle: String, iconRes: Int, accent: androidx.compose.ui.graphics.Color) {
    Row(
        modifier = Modifier.padding(horizontal = SwingDimens.Large),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SwingDimens.Medium),
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(SwingDimens.RadiusMd))
                .background(accent.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = title,
                tint = accent,
                modifier = Modifier.size(24.dp),
            )
        }
        Column {
            Text(
                text = title,
                color = SwingWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = subtitle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp,
            )
        }
    }
}

@Composable
private fun PlaylistsEmptyState() {
    EmptyState(
        iconRes = R.drawable.play_list,
        iconTint = SwingOrange,
        title = stringResource(R.string.playlists_empty_title),
        subtitle = stringResource(R.string.playlists_empty_subtitle),
    )
}
