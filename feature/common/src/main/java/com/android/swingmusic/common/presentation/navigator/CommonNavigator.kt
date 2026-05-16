package com.android.swingmusic.common.presentation.navigator

interface CommonNavigator {

    fun gotoLoginWithUsername()

    fun gotoLoginWithQrCode()

    fun gotoHome()

    fun gotoFolders()

    fun gotoAlbums()

    fun gotoArtists()

    fun gotoSearch()

    fun gotoPlaylists()

    fun gotoPlaylistDetail(playlistId: Int, playlistName: String)

    fun gotoFavorites()

    fun gotoSettings()

    fun gotoStats()

    fun gotoMixes()

    fun gotoAlbumWithInfo(albumHash: String)

    fun navigateBack()

    fun gotoQueueScreen()

    fun gotoArtistInfo(artistHash: String)

    fun gotoViewAllOnArtistScreen(viewAllType: String, artistName: String, baseUrl: String)

    fun gotoViewAllSearchResultsScreen(viewAllType: String, searchParams: String)

    fun gotoSourceFolder(name: String, path: String)

    fun gotoAccounts()

    fun gotoAdminUsers()

    fun gotoAppearance()

    fun gotoAbout()
}
