package com.android.swingmusic.home.presentation.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.swingmusic.auth.data.baseurlholder.BaseUrlHolder
import com.android.swingmusic.auth.data.tokenholder.AuthTokenHolder
import com.android.swingmusic.auth.domain.repository.AuthRepository
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
