package com.android.swingmusic.presentation.navigator

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.android.swingmusic.home.presentation.destinations.HomeDestination
import com.android.swingmusic.home.presentation.destinations.PlaylistsScreenDestination
import com.android.swingmusic.search.presentation.screen.destinations.SearchScreenDestination
import com.ramcosta.composedestinations.spec.DestinationSpec

sealed class BottomNavItem(
    var title: String,
    var selectedIcon: ImageVector,
    var unselectedIcon: ImageVector,
    var destination: DestinationSpec<*>
) {
    data object Home : BottomNavItem(
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        destination = HomeDestination
    )

    data object Search : BottomNavItem(
        title = "Search",
        selectedIcon = Icons.Filled.Search,
        unselectedIcon = Icons.Outlined.Search,
        destination = SearchScreenDestination
    )

    data object Library : BottomNavItem(
        title = "Your Library",
        selectedIcon = Icons.Filled.LibraryMusic,
        unselectedIcon = Icons.Outlined.LibraryMusic,
        destination = PlaylistsScreenDestination
    )
}
