package com.android.swingmusic.home.presentation

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.material3.Icon
import com.android.swingmusic.home.presentation.library.StatsViewModel
import com.android.swingmusic.uicomponent.R
import com.android.swingmusic.uicomponent.presentation.theme.SwingDimens
import com.android.swingmusic.uicomponent.presentation.theme.SwingGray5
import com.android.swingmusic.uicomponent.presentation.theme.SwingGreen
import com.android.swingmusic.uicomponent.presentation.theme.SwingMusicTheme
import com.android.swingmusic.uicomponent.presentation.theme.SwingPink
import com.android.swingmusic.uicomponent.presentation.theme.SwingPurple
import com.android.swingmusic.uicomponent.presentation.theme.SwingTeal
import com.android.swingmusic.uicomponent.presentation.theme.SwingWhite
import com.android.swingmusic.uicomponent.presentation.theme.SwingYellow
import com.ramcosta.composedestinations.annotation.Destination

private data class StatTile(
    val label: String,
    val value: String,
    val iconRes: Int,
    val accent: Color,
)

@Destination
@Composable
fun StatsScreen(
    statsViewModel: StatsViewModel = hiltViewModel(),
) {
    val state by statsViewModel.state.collectAsStateWithLifecycle()
    val tiles = listOf(
        StatTile(stringResource(R.string.stats_artists), formatThousands(state.totalArtists), R.drawable.ic_artist, SwingPink),
        StatTile(stringResource(R.string.stats_albums), formatThousands(state.totalAlbums), R.drawable.ic_album, SwingPurple),
        StatTile(stringResource(R.string.stats_favorites), formatThousands(state.totalFavorites), R.drawable.fav_filled, SwingGreen),
        StatTile(stringResource(R.string.stats_top_artist), state.topArtistName, R.drawable.ic_artist, SwingTeal),
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
                contentPadding = PaddingValues(top = SwingDimens.Large, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(SwingDimens.Large)
            ) {
                item { StatsHero(totalArtists = state.totalArtists, totalAlbums = state.totalAlbums) }
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = SwingDimens.Large),
                        verticalArrangement = Arrangement.spacedBy(SwingDimens.Medium),
                    ) {
                        tiles.chunked(2).forEach { rowTiles ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(SwingDimens.Medium),
                            ) {
                                rowTiles.forEach { stat ->
                                    Box(modifier = Modifier.weight(1f)) {
                                        StatCard(stat)
                                    }
                                }
                                if (rowTiles.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
                if (state.error != null && state.totalArtists == 0 && state.totalAlbums == 0) {
                    item {
                        Text(
                            text = state.error ?: "",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 13.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(SwingDimens.Large)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatsHero(totalArtists: Int, totalAlbums: Int) {
    val combined = totalArtists + totalAlbums
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SwingDimens.Large)
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        SwingPink.copy(alpha = 0.28f),
                        SwingPurple.copy(alpha = 0.22f),
                        SwingTeal.copy(alpha = 0.18f),
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
                )
            )
            .padding(SwingDimens.Large),
        verticalArrangement = Arrangement.spacedBy(SwingDimens.Small),
    ) {
        Text(
            text = stringResource(R.string.nav_stats).uppercase(),
            color = SwingWhite.copy(alpha = 0.6f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
        )
        Text(
            text = stringResource(R.string.stats_subtitle_hero),
            color = SwingWhite,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 30.sp,
        )
        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(SwingGreen)
            )
            Spacer(Modifier.size(8.dp))
            Text(
                text = stringResource(R.string.stats_items_tracked, combined),
                color = SwingWhite.copy(alpha = 0.8f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun StatCard(stat: StatTile) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(SwingGray5)
            .padding(SwingDimens.Medium),
        verticalArrangement = Arrangement.spacedBy(SwingDimens.Small),
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(stat.accent.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(stat.iconRes),
                contentDescription = stat.label,
                tint = stat.accent,
                modifier = Modifier.size(18.dp),
            )
        }
        Text(
            text = stat.value,
            color = SwingWhite,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = stat.label.uppercase(),
            color = SwingWhite.copy(alpha = 0.55f),
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.8.sp,
        )
    }
}

private fun formatThousands(n: Int): String {
    if (n < 1000) return n.toString()
    return n.toString().reversed().chunked(3).joinToString(" ").reversed()
}
