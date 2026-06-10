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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.android.swingmusic.core.data.dto.PlaylistDto
import com.android.swingmusic.home.presentation.library.FavoritesViewModel
import com.android.swingmusic.home.presentation.library.PlaylistsViewModel
import com.android.swingmusic.uicomponent.R
import com.android.swingmusic.uicomponent.presentation.theme.SwingDimens
import com.android.swingmusic.uicomponent.presentation.theme.SwingGray5
import com.android.swingmusic.uicomponent.presentation.theme.SwingHighlightBlue
import com.android.swingmusic.uicomponent.presentation.theme.SwingMusicTheme
import com.android.swingmusic.uicomponent.presentation.theme.SwingWhite
import com.ramcosta.composedestinations.annotation.Destination
import kotlin.math.absoluteValue

@Destination
@Composable
fun PlaylistsScreen(
    navigator: CommonNavigator,
    viewModel: PlaylistsViewModel = hiltViewModel(),
    favoritesViewModel: FavoritesViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val favoritesState by favoritesViewModel.state.collectAsStateWithLifecycle()
    val loggedInUser by authViewModel.loggedInUser.collectAsStateWithLifecycle()

    val displayName = loggedInUser?.let {
        listOf(it.firstname, it.lastname)
            .filter(String::isNotBlank)
            .joinToString(" ")
            .ifBlank { it.username }
    } ?: "Guest"

    val gradient = remember(displayName) { gradientForName(displayName) }

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
                    bottom = SwingDimens.BottomBarSpace + 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header (Spotify Style)
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = SwingDimens.Large, vertical = SwingDimens.Small),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(brush = gradient),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = (displayName.firstOrNull()?.uppercase() ?: "?"),
                                    color = SwingWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                )
                            }
                            Text(
                                text = "Your Library",
                                color = SwingWhite,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_search),
                                contentDescription = "Search",
                                tint = SwingWhite,
                                modifier = Modifier
                                    .size(22.dp)
                                    .clickable { navigator.gotoSearch() }
                            )
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add",
                                tint = SwingWhite,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                // Filter chips
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = SwingDimens.Large),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Playlists Chip (Selected active green)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(SwingHighlightBlue)
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Playlists",
                                color = Color.Black,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                            )
                        }
                        // Artists Chip
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(SwingGray5)
                                .clickable { navigator.gotoArtists() }
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Artists",
                                color = SwingWhite,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                            )
                        }
                        // Albums Chip
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(SwingGray5)
                                .clickable { navigator.gotoAlbums() }
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Albums",
                                color = SwingWhite,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                            )
                        }
                    }
                }

                item { Spacer(Modifier.height(4.dp)) }

                // 1. Liked Songs Card
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = SwingDimens.Large, vertical = 6.dp)
                            .clickable { navigator.gotoFavorites() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(Color(0xFF450E74), Color(0xFFC4B2F3))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.fav_filled),
                                contentDescription = "Liked Songs",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Liked Songs",
                                color = SwingWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            val count = favoritesState.items.size
                            Text(
                                text = "Playlist • $count songs",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                // 2. Folders Card
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = SwingDimens.Large, vertical = 6.dp)
                            .clickable { navigator.gotoFolders() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(SwingGray5),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.folder_outlined_open),
                                contentDescription = "Folders",
                                tint = SwingHighlightBlue,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Folders",
                                color = SwingWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Storage directories",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                // 3. Custom Playlists
                when {
                    state.isLoading -> item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator(color = SwingHighlightBlue) }
                    }
                    state.error != null -> item {
                        Text(
                            text = state.error.orEmpty(),
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(SwingDimens.Large)
                        )
                    }
                    else -> {
                        items(items = state.items, key = { it.id ?: 0 }) { playlist ->
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
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(SwingHighlightBlue.copy(alpha = 0.15f)),
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
                    modifier = Modifier.size(64.dp).clip(RoundedCornerShape(4.dp))
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.play_list),
                    contentDescription = null,
                    tint = SwingHighlightBlue,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = playlist.name.orEmpty().ifBlank { stringResource(R.string.playlist_untitled) },
                color = SwingWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.playlist_tracks_count, playlist.count ?: 0),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp,
            )
        }
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
        colors = palette
    )
}
