package com.android.swingmusic.home.presentation.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.swingmusic.auth.data.baseurlholder.BaseUrlHolder
import com.android.swingmusic.auth.data.tokenholder.AuthTokenHolder
import com.android.swingmusic.auth.domain.repository.AuthRepository
import com.android.swingmusic.core.data.dto.ArtistDto
import com.android.swingmusic.core.data.dto.PlaylistDto
import com.android.swingmusic.core.data.dto.TrackDto
import com.android.swingmusic.network.data.api.service.NetworkApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class LibraryListState<T>(
    val isLoading: Boolean = false,
    val items: List<T> = emptyList(),
    val error: String? = null,
    val baseUrl: String? = null,
)

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val networkApiService: NetworkApiService,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(LibraryListState<TrackDto>())
    val state: StateFlow<LibraryListState<TrackDto>> = _state.asStateFlow()

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val baseUrl = (BaseUrlHolder.baseUrl ?: authRepository.getBaseUrl())?.trimEnd('/')
            val token = AuthTokenHolder.accessToken ?: authRepository.getAccessToken()
            if (baseUrl.isNullOrBlank() || token.isNullOrBlank()) {
                _state.update { it.copy(isLoading = false, error = "Not logged in") }
                return@launch
            }
            try {
                val res = networkApiService.getFavoriteTracks(
                    url = "$baseUrl/favorites/tracks",
                    bearerToken = "Bearer $token",
                    start = 0,
                    limit = 200,
                )
                _state.update {
                    it.copy(
                        isLoading = false,
                        items = res.tracks.orEmpty(),
                        baseUrl = "$baseUrl/",
                        error = null
                    )
                }
            } catch (e: Exception) {
                Timber.tag("FAV").e(e, "fetch failed")
                _state.update {
                    it.copy(isLoading = false, error = e.message ?: "Failed to load favorites")
                }
            }
        }
    }
}

@HiltViewModel
class PlaylistsViewModel @Inject constructor(
    private val networkApiService: NetworkApiService,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(LibraryListState<PlaylistDto>())
    val state: StateFlow<LibraryListState<PlaylistDto>> = _state.asStateFlow()

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val baseUrl = (BaseUrlHolder.baseUrl ?: authRepository.getBaseUrl())?.trimEnd('/')
            val token = AuthTokenHolder.accessToken ?: authRepository.getAccessToken()
            if (baseUrl.isNullOrBlank() || token.isNullOrBlank()) {
                _state.update { it.copy(isLoading = false, error = "Not logged in") }
                return@launch
            }
            try {
                val res = networkApiService.getAllPlaylists(
                    url = "$baseUrl/playlists",
                    bearerToken = "Bearer $token",
                )
                _state.update {
                    it.copy(
                        isLoading = false,
                        items = res.data.orEmpty(),
                        baseUrl = "$baseUrl/",
                        error = null
                    )
                }
            } catch (e: Exception) {
                Timber.tag("PLAYLISTS").e(e, "fetch failed")
                _state.update {
                    it.copy(isLoading = false, error = e.message ?: "Failed to load playlists")
                }
            }
        }
    }

    suspend fun loadPlaylistTracks(playlistId: Int): List<TrackDto> {
        val baseUrl = (BaseUrlHolder.baseUrl ?: authRepository.getBaseUrl())?.trimEnd('/')
        val token = AuthTokenHolder.accessToken ?: authRepository.getAccessToken()
        if (baseUrl.isNullOrBlank() || token.isNullOrBlank()) return emptyList()
        return try {
            val res = networkApiService.getPlaylistTracks(
                url = "$baseUrl/playlists/$playlistId?limit=-1",
                bearerToken = "Bearer $token",
            )
            res.tracks.orEmpty()
        } catch (e: Exception) {
            Timber.tag("PLAYLISTS").e(e, "load tracks failed")
            emptyList()
        }
    }
}

data class HomeState(
    val isLoading: Boolean = false,
    val recentlyPlayed: List<TrackDto> = emptyList(),
    val topArtists: List<ArtistDto> = emptyList(),
    val baseUrl: String = "",
    val error: String? = null,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val networkApiService: NetworkApiService,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val baseUrl = (BaseUrlHolder.baseUrl ?: authRepository.getBaseUrl())?.trimEnd('/')
            val token = AuthTokenHolder.accessToken ?: authRepository.getAccessToken()
            if (baseUrl.isNullOrBlank() || token.isNullOrBlank()) {
                _state.update { it.copy(isLoading = false, error = "Not logged in") }
                return@launch
            }
            val bearer = "Bearer $token"
            val urlBase = "$baseUrl/"

            val recents = try {
                networkApiService.getRecentlyPlayedTracks(
                    url = "${urlBase}getall/tracks",
                    bearerToken = bearer,
                ).tracks.orEmpty()
            } catch (e: Exception) {
                Timber.tag("HOME").e(e, "recently played failed")
                emptyList()
            }

            val top = try {
                networkApiService.getTopArtists(
                    url = "${urlBase}getall/artists",
                    bearerToken = bearer,
                ).artistsDto.orEmpty()
            } catch (e: Exception) {
                Timber.tag("HOME").e(e, "top artists failed")
                emptyList()
            }

            _state.update {
                it.copy(
                    isLoading = false,
                    recentlyPlayed = recents,
                    topArtists = top,
                    baseUrl = urlBase,
                    error = if (recents.isEmpty() && top.isEmpty()) "No data" else null,
                )
            }
        }
    }
}

data class StatsState(
    val isLoading: Boolean = false,
    val totalArtists: Int = 0,
    val totalAlbums: Int = 0,
    val totalFavorites: Int = 0,
    val topArtistName: String = "—",
    val error: String? = null,
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val networkApiService: NetworkApiService,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(StatsState())
    val state: StateFlow<StatsState> = _state.asStateFlow()

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val baseUrl = (BaseUrlHolder.baseUrl ?: authRepository.getBaseUrl())?.trimEnd('/')
            val token = AuthTokenHolder.accessToken ?: authRepository.getAccessToken()
            if (baseUrl.isNullOrBlank() || token.isNullOrBlank()) {
                _state.update { it.copy(isLoading = false, error = "Not logged in") }
                return@launch
            }
            val bearer = "Bearer $token"
            val urlBase = "$baseUrl/"

            val artistsTotal = try {
                networkApiService.getArtistsCount(
                    url = "${urlBase}getall/artists",
                    bearerToken = bearer,
                ).total ?: 0
            } catch (e: Exception) { Timber.tag("STATS").e(e, "artistsCount"); 0 }

            val albumsTotal = try {
                networkApiService.getAlbumsCount(
                    url = "${urlBase}getall/albums",
                    bearerToken = bearer,
                ).total ?: 0
            } catch (e: Exception) { Timber.tag("STATS").e(e, "albumsCount"); 0 }

            val favTotal = try {
                networkApiService.getFavoriteTracks(
                    url = "${urlBase}favorites/tracks",
                    bearerToken = bearer,
                    start = 0, limit = 1
                ).total ?: 0
            } catch (e: Exception) { Timber.tag("STATS").e(e, "favTotal"); 0 }

            val topName = try {
                networkApiService.getTopArtists(
                    url = "${urlBase}getall/artists",
                    bearerToken = bearer,
                    limit = 1,
                ).artistsDto?.firstOrNull()?.name ?: "—"
            } catch (e: Exception) { Timber.tag("STATS").e(e, "topArtist"); "—" }

            _state.update {
                it.copy(
                    isLoading = false,
                    totalArtists = artistsTotal,
                    totalAlbums = albumsTotal,
                    totalFavorites = favTotal,
                    topArtistName = topName,
                    error = null,
                )
            }
        }
    }
}
