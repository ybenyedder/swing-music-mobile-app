package com.android.swingmusic.settings.presentation.panes

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
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
import com.android.swingmusic.uicomponent.presentation.theme.SwingPurple
import com.android.swingmusic.uicomponent.presentation.theme.SwingTeal
import com.android.swingmusic.uicomponent.presentation.theme.SwingWhite
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun AppearanceScreen(commonNavigator: CommonNavigator) {
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
                verticalArrangement = Arrangement.spacedBy(SwingDimens.Medium)
            ) {
                item {
                    Text(
                        modifier = Modifier.padding(horizontal = SwingDimens.Large),
                        text = stringResource(R.string.appearance_title),
                        color = SwingWhite,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
                item { ThemeHeroCard() }
                item { ComingSoonCard() }
            }
        }
    }
}

@Composable
private fun ThemeHeroCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SwingDimens.Large)
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        SwingPurple.copy(alpha = 0.30f),
                        SwingHighlightBlue.copy(alpha = 0.22f),
                        SwingTeal.copy(alpha = 0.18f),
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
                )
            )
            .padding(SwingDimens.Large),
        verticalArrangement = Arrangement.spacedBy(SwingDimens.Small),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(SwingPurple.copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.swing_music_logo_outlined),
                contentDescription = null,
                tint = SwingPurple,
                modifier = Modifier.size(22.dp),
            )
        }
        Text(
            text = stringResource(R.string.appearance_theme_name),
            color = SwingWhite,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = stringResource(R.string.appearance_theme_sub),
            color = SwingWhite.copy(alpha = 0.75f),
            fontSize = 13.sp,
            lineHeight = 18.sp,
        )
    }
}

@Composable
private fun ComingSoonCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SwingDimens.Large)
            .clip(RoundedCornerShape(20.dp))
            .background(SwingGray5)
            .padding(SwingDimens.Large),
        verticalArrangement = Arrangement.spacedBy(SwingDimens.Small),
    ) {
        Text(
            text = stringResource(R.string.appearance_coming_soon),
            color = SwingHighlightBlue,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp,
        )
        Spacer(Modifier.height(2.dp))
        FeatureLine(stringResource(R.string.appearance_feature_themes))
        FeatureLine(stringResource(R.string.appearance_feature_accent))
        FeatureLine(stringResource(R.string.appearance_feature_density))
        FeatureLine(stringResource(R.string.appearance_feature_artist_shape))
    }
}

@Composable
private fun FeatureLine(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
    ) {
        Text(
            text = "·  $text",
            color = SwingWhite.copy(alpha = 0.78f),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}
