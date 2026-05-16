package com.android.swingmusic.presentation.navigator

import androidx.annotation.DrawableRes
import com.android.swingmusic.home.presentation.destinations.FavoritesScreenDestination
import com.android.swingmusic.home.presentation.destinations.HomeDestination
import com.android.swingmusic.home.presentation.destinations.PlaylistsScreenDestination
import com.ramcosta.composedestinations.spec.DestinationSpec
import com.android.swingmusic.uicomponent.R as UiComponent

sealed class BottomNavItem(
    var title: String,
    @param:DrawableRes var icon: Int,
    var destination: DestinationSpec<*>
) {
    data object Home : BottomNavItem(
        title = "Home",
        icon = UiComponent.drawable.ic_home,
        destination = HomeDestination
    )

    data object Favorites : BottomNavItem(
        title = "Favorites",
        icon = UiComponent.drawable.fav_filled,
        destination = FavoritesScreenDestination
    )

    data object Playlists : BottomNavItem(
        title = "Playlists",
        icon = UiComponent.drawable.ic_playlist,
        destination = PlaylistsScreenDestination
    )
}
