package com.android.swingmusic.settings.presentation.panes

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.swingmusic.common.presentation.navigator.CommonNavigator
import com.android.swingmusic.uicomponent.presentation.theme.SwingDimens
import com.android.swingmusic.uicomponent.presentation.theme.SwingGray5
import com.android.swingmusic.uicomponent.presentation.theme.SwingHighlightBlue
import com.android.swingmusic.uicomponent.presentation.theme.SwingMusicTheme
import com.android.swingmusic.uicomponent.presentation.theme.SwingWhite
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun AppearanceScreen(commonNavigator: CommonNavigator) {
    var sidebarLayout by remember { mutableStateOf(true) }
    var simpleArtistHeader by remember { mutableStateOf(true) }
    var displayFoldersAsList by remember { mutableStateOf(false) }
    var nowPlayingOnTab by remember { mutableStateOf(true) }
    var inlineFavoriteIcon by remember { mutableStateOf(false) }

    SwingMusicTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 80.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(SwingDimens.Small)
            ) {
                item {
                    Text(
                        modifier = Modifier.padding(horizontal = SwingDimens.Large),
                        text = "Appearance",
                        color = SwingWhite,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                item { Spacer(modifier = Modifier.height(SwingDimens.Small)) }
                item {
                    SwitchRow(
                        title = "Use no sidebar layout",
                        subtitle = "Wider content area on tablets",
                        badge = "new",
                        checked = sidebarLayout,
                        onCheckedChange = { sidebarLayout = it }
                    )
                }
                item {
                    SwitchRow(
                        title = "Simple artist page header",
                        subtitle = "Disable the gradient layout and use a circular image",
                        checked = simpleArtistHeader,
                        onCheckedChange = { simpleArtistHeader = it }
                    )
                }
                item {
                    SwitchRow(
                        title = "Display folders in list mode",
                        subtitle = null,
                        checked = displayFoldersAsList,
                        onCheckedChange = { displayFoldersAsList = it }
                    )
                }
                item {
                    SwitchRow(
                        title = "Show Now Playing on tab title",
                        subtitle = "Replace current page info with the playing track",
                        checked = nowPlayingOnTab,
                        onCheckedChange = { nowPlayingOnTab = it }
                    )
                }
                item {
                    SwitchRow(
                        title = "Show inline favorite icon",
                        subtitle = "Show the favorite button next to the track duration",
                        checked = inlineFavoriteIcon,
                        onCheckedChange = { inlineFavoriteIcon = it }
                    )
                }
            }
        }
    }
}

@Composable
private fun SwitchRow(
    title: String,
    subtitle: String?,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    badge: String? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SwingDimens.Large)
            .clip(RoundedCornerShape(SwingDimens.RadiusMd))
            .background(SwingGray5)
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = SwingDimens.Medium, vertical = SwingDimens.Medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    color = SwingWhite,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                )
                if (badge != null) {
                    Spacer(Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(SwingDimens.RadiusSm))
                            .background(SwingHighlightBlue.copy(alpha = 0.25f))
                            .padding(horizontal = 6.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = badge,
                            color = SwingHighlightBlue,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 10.sp,
                        )
                    }
                }
            }
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = SwingWhite,
                checkedTrackColor = SwingHighlightBlue,
                uncheckedThumbColor = SwingWhite.copy(alpha = 0.7f),
                uncheckedTrackColor = SwingGray5,
            )
        )
    }
}
