package com.android.swingmusic.uicomponent.presentation.theme

import androidx.compose.ui.unit.dp

object SwingDimens {
    // Spacing — mirrors web SCSS sizes ($smallest .. $larger)
    val Smallest = 2.dp   // $smallest 0.15rem
    val Smaller = 4.dp    // $smaller 0.25rem
    val Small = 8.dp      // $small 0.5rem
    val Medium = 12.dp    // $medium 0.75rem
    val Large = 24.dp     // $large 1.5rem
    val Larger = 32.dp    // $larger 2rem

    // Layout — mirrors web $navheight / $padbottom / card sizes
    val NavHeight = 72.dp        // $navheight 4.5rem
    val SideBarWidth = 240.dp    // 15rem
    val BottomBarHeight = 82.dp  // 5.125rem
    val BannerHeight = 288.dp    // 18rem
    val SongItemHeight = 64.dp   // 4rem
    val CardWidth = 172.dp       // 10.75rem
    val ContentPadTop = 16.dp
    val ContentPadBottom = 64.dp // $padbottom 4rem
    val BottomBarSpace = 200.dp  // mini-player + spacer + nav bar + system nav inset
    val PadLeft = 32.dp          // approximated from web's clamp(2rem, ..., 5rem)
    val PadRight = 32.dp

    // Radii
    val RadiusSm = 8.dp
    val RadiusMd = 12.dp
    val RadiusLg = 16.dp
    val RadiusXl = 24.dp
    val RadiusPill = 999.dp
}
