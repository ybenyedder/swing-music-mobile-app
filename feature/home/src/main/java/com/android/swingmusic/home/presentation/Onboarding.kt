package com.android.swingmusic.home.presentation

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.swingmusic.common.presentation.navigator.CommonNavigator
import com.android.swingmusic.uicomponent.R
import com.android.swingmusic.uicomponent.presentation.theme.SwingDimens
import com.android.swingmusic.uicomponent.presentation.theme.SwingGray
import com.android.swingmusic.uicomponent.presentation.theme.SwingHighlightBlue
import com.android.swingmusic.uicomponent.presentation.theme.SwingMusicTheme
import com.android.swingmusic.uicomponent.presentation.theme.SwingWhite
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun OnboardingScreen(commonNavigator: CommonNavigator) {
    SwingMusicTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    horizontal = SwingDimens.Large,
                    vertical = SwingDimens.Larger
                ),
                verticalArrangement = Arrangement.spacedBy(SwingDimens.Medium)
            ) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            painter = painterResource(R.drawable.swing_logo_circle),
                            contentDescription = null,
                            modifier = Modifier
                                .size(96.dp)
                                .clip(RoundedCornerShape(SwingDimens.RadiusPill)),
                        )
                    }
                }
                item {
                    Text(
                        text = stringResource(R.string.welcome_title),
                        color = SwingWhite,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                item {
                    Text(
                        text = stringResource(R.string.welcome_subtitle),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp,
                    )
                }
                item { Spacer(modifier = Modifier.height(SwingDimens.Large)) }
                item { OnboardingStep(stringResource(R.string.onboarding_step1)) }
                item { OnboardingStep(stringResource(R.string.onboarding_step2)) }
                item { OnboardingStep(stringResource(R.string.onboarding_step3)) }
                item { Spacer(modifier = Modifier.height(SwingDimens.Large)) }
                item {
                    Button(
                        onClick = { commonNavigator.gotoLoginWithQrCode() },
                        colors = ButtonDefaults.buttonColors(containerColor = SwingHighlightBlue),
                        shape = RoundedCornerShape(SwingDimens.RadiusPill),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.onboarding_scan_qr),
                            color = SwingWhite,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                        )
                    }
                }
                item { Spacer(modifier = Modifier.height(SwingDimens.Small)) }
                item {
                    Button(
                        onClick = { commonNavigator.gotoLoginWithUsername() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SwingGray,
                            contentColor = SwingWhite,
                        ),
                        shape = RoundedCornerShape(SwingDimens.RadiusPill),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.onboarding_signin),
                            color = SwingWhite,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OnboardingStep(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(SwingDimens.RadiusMd))
            .background(SwingGray)
            .padding(SwingDimens.Medium),
    ) {
        Text(text = text, color = SwingWhite, fontSize = 14.sp)
    }
}
