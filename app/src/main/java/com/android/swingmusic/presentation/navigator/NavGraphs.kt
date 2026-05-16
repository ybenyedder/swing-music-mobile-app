package com.android.swingmusic.presentation.navigator

import com.android.swingmusic.album.presentation.screen.destinations.AlbumWithInfoScreenDestination
import com.android.swingmusic.album.presentation.screen.destinations.AllAlbumScreenDestination
import com.android.swingmusic.artist.presentation.screen.destinations.AllArtistsScreenDestination
import com.android.swingmusic.artist.presentation.screen.destinations.ArtistInfoScreenDestination
import com.android.swingmusic.artist.presentation.screen.destinations.ViewAllScreenOnArtistDestination
import com.android.swingmusic.auth.presentation.screen.destinations.LoginWithQrCodeDestination
import com.android.swingmusic.auth.presentation.screen.destinations.LoginWithUsernameScreenDestination
import com.android.swingmusic.folder.presentation.screen.destinations.FoldersAndTracksScreenDestination
import com.android.swingmusic.home.presentation.destinations.FavoritesScreenDestination
import com.android.swingmusic.home.presentation.destinations.HomeDestination
import com.android.swingmusic.home.presentation.destinations.LyricsScreenDestination
import com.android.swingmusic.home.presentation.destinations.MixesScreenDestination
import com.android.swingmusic.home.presentation.destinations.OnboardingScreenDestination
import com.android.swingmusic.home.presentation.destinations.PlaylistDetailScreenDestination
import com.android.swingmusic.home.presentation.destinations.PlaylistsScreenDestination
import com.android.swingmusic.home.presentation.destinations.SettingsScreenDestination
import com.android.swingmusic.home.presentation.destinations.StatsScreenDestination
import com.android.swingmusic.player.presentation.screen.destinations.NowPlayingScreenDestination
import com.android.swingmusic.player.presentation.screen.destinations.QueueScreenDestination
import com.android.swingmusic.search.presentation.screen.destinations.SearchScreenDestination
import com.android.swingmusic.search.presentation.screen.destinations.ViewAllSearchResultsDestination
import com.android.swingmusic.settings.presentation.destinations.AccountsScreenDestination
import com.android.swingmusic.settings.presentation.destinations.AdminUsersScreenDestination
import com.android.swingmusic.settings.presentation.destinations.AboutScreenDestination
import com.android.swingmusic.settings.presentation.destinations.AppearanceScreenDestination
import com.ramcosta.composedestinations.spec.DestinationSpec
import com.ramcosta.composedestinations.spec.NavGraphSpec
import com.ramcosta.composedestinations.spec.Route

object NavGraphs {
    fun root(isUserLoggedIn: Boolean) = object : NavGraphSpec {
        override val route: String = "root"

        override val startRoute: Route =
            if (isUserLoggedIn) HomeDestination else LoginWithQrCodeDestination

        override val destinationsByRoute: Map<String, DestinationSpec<*>>
            get() {
                val preAuthDestSpec = listOf(
                    LoginWithQrCodeDestination,
                    LoginWithUsernameScreenDestination
                )

                val pastAuthDestSpec = listOf(
                    // shown on bottom nav
                    HomeDestination,
                    FoldersAndTracksScreenDestination,
                    AllAlbumScreenDestination,
                    AllArtistsScreenDestination,
                    SearchScreenDestination,

                    // inner destinations
                    NowPlayingScreenDestination,
                    QueueScreenDestination,
                    AlbumWithInfoScreenDestination,
                    ViewAllScreenOnArtistDestination,
                    ArtistInfoScreenDestination,
                    ViewAllSearchResultsDestination,
                    PlaylistsScreenDestination,
                    PlaylistDetailScreenDestination,
                    FavoritesScreenDestination,
                    SettingsScreenDestination,
                    StatsScreenDestination,
                    MixesScreenDestination,
                    LyricsScreenDestination,
                    OnboardingScreenDestination,
                    AccountsScreenDestination,
                    AdminUsersScreenDestination,
                    AppearanceScreenDestination,
                    AboutScreenDestination,
                )

                return (preAuthDestSpec + pastAuthDestSpec).associateBy { it.route }
            }

        override val nestedNavGraphs: List<NavGraphSpec> = emptyList()
    }
}
