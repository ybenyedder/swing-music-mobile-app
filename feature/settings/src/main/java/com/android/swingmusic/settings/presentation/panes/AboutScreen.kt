package com.android.swingmusic.settings.presentation.panes

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.swingmusic.common.presentation.navigator.CommonNavigator
import com.android.swingmusic.uicomponent.R
import com.android.swingmusic.uicomponent.presentation.theme.SwingDimens
import com.android.swingmusic.uicomponent.presentation.theme.SwingGray5
import com.android.swingmusic.uicomponent.presentation.theme.SwingHighlightBlue
import com.android.swingmusic.uicomponent.presentation.theme.SwingMusicTheme
import com.android.swingmusic.uicomponent.presentation.theme.SwingTeal
import com.android.swingmusic.uicomponent.presentation.theme.SwingWhite
import com.ramcosta.composedestinations.annotation.Destination

private const val GITHUB_URL = "https://github.com/swingmx/swingmusic"
private const val CLIENT_URL = "https://github.com/CyperKnight/SwingMusic-Android"

@Destination
@Composable
fun AboutScreen(commonNavigator: CommonNavigator) {
    val context = LocalContext.current

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
                        text = stringResource(R.string.about_title),
                        color = SwingWhite,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                item { Spacer(modifier = Modifier.height(SwingDimens.Medium)) }
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = SwingDimens.Large)
                            .clip(RoundedCornerShape(SwingDimens.RadiusLg))
                            .background(SwingGray5)
                            .padding(SwingDimens.Large),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(SwingHighlightBlue.copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.swing_music_logo_outlined),
                                contentDescription = null,
                                tint = SwingHighlightBlue,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(Modifier.height(SwingDimens.Medium))
                        Text(
                            text = "Swing Music",
                            color = SwingWhite,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = stringResource(R.string.about_client),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp,
                        )
                        Spacer(Modifier.height(SwingDimens.Small))
                        Text(
                            text = "v1.5",
                            color = SwingTeal,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                item { Spacer(modifier = Modifier.height(SwingDimens.Medium)) }
                item {
                    LinkRow(
                        title = stringResource(R.string.about_server_source),
                        subtitle = "github.com/swingmx/swingmusic",
                        iconRes = R.drawable.swing_music_logo_outlined,
                    ) {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(GITHUB_URL)))
                    }
                }
                item {
                    LinkRow(
                        title = stringResource(R.string.about_client_source),
                        subtitle = "github.com/CyperKnight/SwingMusic-Android",
                        iconRes = R.drawable.swing_music_logo_outlined,
                    ) {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(CLIENT_URL)))
                    }
                }
            }
        }
    }
}

@Composable
private fun LinkRow(
    title: String,
    subtitle: String,
    iconRes: Int,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SwingDimens.Large)
            .clip(RoundedCornerShape(SwingDimens.RadiusMd))
            .background(SwingGray5)
            .clickable(onClick = onClick)
            .padding(horizontal = SwingDimens.Medium, vertical = SwingDimens.Medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(SwingDimens.RadiusSm))
                .background(SwingHighlightBlue.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = SwingHighlightBlue,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(Modifier.width(SwingDimens.Small))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = SwingWhite, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Text(text = subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
        }
    }
}
