package com.android.swingmusic.presentation.navigator

import androidx.navigation.NavController
import com.android.swingmusic.album.presentation.screen.destinations.AlbumWithInfoScreenDestination
import com.android.swingmusic.album.presentation.screen.destinations.AllAlbumScreenDestination
import com.android.swingmusic.artist.presentation.screen.destinations.AllArtistsScreenDestination
import com.android.swingmusic.artist.presentation.screen.destinations.ArtistInfoScreenDestination
import com.android.swingmusic.artist.presentation.screen.destinations.ViewAllScreenOnArtistDestination
import com.android.swingmusic.auth.presentation.screen.destinations.LoginWithQrCodeDestination
import com.android.swingmusic.auth.presentation.screen.destinations.LoginWithUsernameScreenDestination
import com.android.swingmusic.common.presentation.navigator.CommonNavigator
import com.android.swingmusic.folder.presentation.screen.destinations.FoldersAndTracksScreenDestination
import com.android.swingmusic.home.presentation.destinations.FavoritesScreenDestination
import com.android.swingmusic.home.presentation.destinations.HomeDestination
import com.android.swingmusic.home.presentation.destinations.MixesScreenDestination
import com.android.swingmusic.home.presentation.destinations.PlaylistDetailScreenDestination
import com.android.swingmusic.home.presentation.destinations.PlaylistsScreenDestination
import com.android.swingmusic.home.presentation.destinations.SettingsScreenDestination
import com.android.swingmusic.home.presentation.destinations.StatsScreenDestination
import com.android.swingmusic.player.presentation.screen.destinations.QueueScreenDestination
import com.android.swingmusic.search.presentation.screen.destinations.SearchScreenDestination
import com.android.swingmusic.search.presentation.screen.destinations.ViewAllSearchResultsDestination
import com.android.swingmusic.settings.presentation.destinations.AccountsScreenDestination
import com.android.swingmusic.settings.presentation.destinations.AdminUsersScreenDestination
import com.android.swingmusic.settings.presentation.destinations.AboutScreenDestination
import com.android.swingmusic.settings.presentation.destinations.AppearanceScreenDestination
import com.ramcosta.composedestinations.navigation.navigate

class CoreNavigator(
    private val navController: NavController
) : CommonNavigator {

    /**----------------------------------- Auth Navigator ----------------------------------------*/
    override fun gotoLoginWithUsername() {
        val targetDestination = LoginWithUsernameScreenDestination

        navController.navigate(targetDestination) {
            launchSingleTop = true
            restoreState = false

            popUpTo(navController.graph.startDestinationId) {
                inclusive = true
                saveState = false
            }
        }
    }

    override fun gotoLoginWithQrCode() {
        val targetDestination = LoginWithQrCodeDestination

        navController.navigate(targetDestination) {
            launchSingleTop = true
            restoreState = false

            popUpTo(navController.graph.startDestinationId) {
                inclusive = true
                saveState = false
            }
        }
    }

    override fun gotoHome() {
        val targetDestination = HomeDestination()

        navController.navigate(targetDestination) {
            launchSingleTop = true
            restoreState = false

            popUpTo(navController.graph.startDestinationId) {
                inclusive = true
                saveState = false
            }
        }
    }

    override fun gotoFolders() {
        navigateToTab(FoldersAndTracksScreenDestination().route)
    }

    override fun gotoAlbums() {
        navigateToTab(AllAlbumScreenDestination.route)
    }

    override fun gotoArtists() {
        navigateToTab(AllArtistsScreenDestination.route)
    }

    override fun gotoSearch() {
        navigateToTab(SearchScreenDestination.route)
    }

    override fun gotoPlaylists() {
        navController.navigate(PlaylistsScreenDestination.route) {
            launchSingleTop = true
        }
    }

    override fun gotoPlaylistDetail(playlistId: Int, playlistName: String) {
        val target = PlaylistDetailScreenDestination(
            playlistId = playlistId,
            playlistName = playlistName,
        )
        navController.navigate(target) {
            launchSingleTop = true
        }
    }

    override fun gotoFavorites() {
        navController.navigate(FavoritesScreenDestination.route) {
            launchSingleTop = true
        }
    }

    override fun gotoSettings() {
        navController.navigate(SettingsScreenDestination.route) {
            launchSingleTop = true
        }
    }

    override fun gotoStats() {
        navController.navigate(StatsScreenDestination.route) {
            launchSingleTop = true
        }
    }

    override fun gotoMixes() {
        navController.navigate(MixesScreenDestination.route) {
            launchSingleTop = true
        }
    }

    private fun navigateToTab(route: String) {
        navController.navigate(route) {
            launchSingleTop = true
            restoreState = false
            popUpTo(navController.graph.startDestinationId) {
                saveState = false
                inclusive = false
            }
        }
    }

    /**----------------------------------- Album Navigator --------------------------------------*/
    override fun gotoAlbumWithInfo(albumHash: String) {
        val targetDestination = AlbumWithInfoScreenDestination(albumHash)

        navController.navigate(targetDestination) {
            launchSingleTop = true
        }
    }

    override fun navigateBack() {
        navController.navigateUp()
    }

    /**----------------------------------- Player Navigator -------------------------------------*/
    override fun gotoQueueScreen() {
        val targetDestination = QueueScreenDestination

        navController.navigate(targetDestination) {
            launchSingleTop = true
        }

    }

    override fun gotoArtistInfo(artistHash: String) {
        val targetDestination = ArtistInfoScreenDestination(
            artistHash = artistHash,
            loadNewArtist = true
        )

        navController.navigate(targetDestination) {
            launchSingleTop = true
        }
    }

    override fun gotoViewAllOnArtistScreen(
        viewAllType: String,
        artistName: String,
        baseUrl: String
    ) {
        val targetDestination = ViewAllScreenOnArtistDestination(
            viewAllType = viewAllType,
            artistName = artistName,
            baseUrl = baseUrl
        )

        navController.navigate(targetDestination) {
            launchSingleTop = true
        }
    }

    override fun gotoViewAllSearchResultsScreen(
        viewAllType: String,
        searchParams: String
    ) {
        val targetDestination = ViewAllSearchResultsDestination(
            searchParams = searchParams,
            viewAllType = viewAllType
        )

        navController.navigate(targetDestination) {
            launchSingleTop = true
        }
    }

    override fun gotoSourceFolder(name: String, path: String) {
        val targetDestination = FoldersAndTracksScreenDestination(name, path)
        navController.navigate(targetDestination) {
            launchSingleTop = true
        }
    }

    override fun gotoAccounts() {
        navController.navigate(AccountsScreenDestination.route) {
            launchSingleTop = true
        }
    }

    override fun gotoAdminUsers() {
        navController.navigate(AdminUsersScreenDestination.route) {
            launchSingleTop = true
        }
    }

    override fun gotoAppearance() {
        navController.navigate(AppearanceScreenDestination.route) {
            launchSingleTop = true
        }
    }

    override fun gotoAbout() {
        navController.navigate(AboutScreenDestination.route) {
            launchSingleTop = true
        }
    }
}
