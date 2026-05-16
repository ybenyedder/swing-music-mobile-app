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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.swingmusic.uicomponent.R
import com.android.swingmusic.uicomponent.presentation.theme.SwingDimens
import com.android.swingmusic.uicomponent.presentation.theme.SwingGreen
import com.android.swingmusic.uicomponent.presentation.theme.SwingMusicTheme
import com.android.swingmusic.uicomponent.presentation.theme.SwingPurple
import com.android.swingmusic.uicomponent.presentation.theme.SwingTeal
import com.android.swingmusic.uicomponent.presentation.theme.SwingWhite
import com.android.swingmusic.uicomponent.presentation.theme.SwingYellow
import com.ramcosta.composedestinations.annotation.Destination

private data class StatTile(val label: String, val value: String, val accent: Color)

@Destination
@Composable
fun StatsScreen() {
    val tiles = listOf(
        StatTile("Tracks played", "—", SwingGreen),
        StatTile("Minutes listened", "—", SwingPurple),
        StatTile("Top artist", "—", SwingTeal),
        StatTile("Top album", "—", SwingYellow),
    )
    SwingMusicTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = SwingDimens.Large, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(SwingDimens.Medium)
            ) {
                item { ScreenHeader("Stats", "Your listening activity", R.drawable.ic_artist, SwingTeal) }
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
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(stat: StatTile) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(SwingDimens.RadiusMd))
            .background(stat.accent.copy(alpha = 0.12f))
            .padding(SwingDimens.Medium),
    ) {
        Text(
            text = stat.label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp,
        )
        Spacer(modifier = Modifier.height(SwingDimens.Smallest))
        Text(
            text = stat.value,
            color = SwingWhite,
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.sp,
        )
    }
}
