package com.android.swingmusic.home.presentation.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.swingmusic.core.domain.model.LyricsLine
import com.android.swingmusic.network.data.api.service.LrcLibApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class LyricsState(
    val isLoading: Boolean = false,
    val trackHash: String? = null,
    val trackTitle: String = "",
    val artistName: String = "",
    val syncedLines: List<LyricsLine> = emptyList(),
    val plainText: String = "",
    val error: String? = null,
)

@HiltViewModel
class LyricsViewModel @Inject constructor(
    private val lrcLibApi: LrcLibApiService,
) : ViewModel() {

    private val _state = MutableStateFlow(LyricsState())
    val state: StateFlow<LyricsState> = _state.asStateFlow()

    fun loadFor(
        trackHash: String?,
        title: String?,
        artist: String?,
        album: String?,
        durationSec: Int?,
    ) {
        if (trackHash == null || title.isNullOrBlank() || artist.isNullOrBlank()) {
            _state.update { LyricsState(error = "No track playing") }
            return
        }
        if (_state.value.trackHash == trackHash && _state.value.syncedLines.isNotEmpty()) return

        viewModelScope.launch {
            _state.update {
                LyricsState(
                    isLoading = true,
                    trackHash = trackHash,
                    trackTitle = title,
                    artistName = artist,
                )
            }
            val dto = try {
                lrcLibApi.getLyrics(
                    trackName = title,
                    artistName = artist,
                    albumName = album,
                    duration = durationSec,
                )
            } catch (e: Exception) {
                Timber.tag("LYRICS").w(e, "exact get failed, fallback to search")
                try {
                    lrcLibApi.searchLyrics(trackName = title, artistName = artist).firstOrNull()
                } catch (e2: Exception) {
                    Timber.tag("LYRICS").e(e2, "search failed")
                    null
                }
            }

            if (dto == null) {
                _state.update {
                    it.copy(isLoading = false, error = "No lyrics found")
                }
                return@launch
            }

            val synced = parseLrc(dto.syncedLyrics.orEmpty())
            _state.update {
                it.copy(
                    isLoading = false,
                    syncedLines = synced,
                    plainText = dto.plainLyrics.orEmpty(),
                    error = if (synced.isEmpty() && dto.plainLyrics.isNullOrBlank()) "No lyrics found" else null,
                )
            }
        }
    }

    fun currentLineIndex(positionMs: Long): Int {
        val lines = _state.value.syncedLines
        if (lines.isEmpty()) return -1
        var lo = 0
        var hi = lines.size - 1
        var ans = -1
        while (lo <= hi) {
            val mid = (lo + hi) ushr 1
            if (lines[mid].timeMs <= positionMs) {
                ans = mid
                lo = mid + 1
            } else hi = mid - 1
        }
        return ans
    }
}

private val LRC_TAG = Regex("""\[(\d+):(\d+)(?:[.:](\d+))?]""")

internal fun parseLrc(raw: String): List<LyricsLine> {
    if (raw.isBlank()) return emptyList()
    val out = mutableListOf<LyricsLine>()
    raw.lineSequence().forEach { line ->
        val matches = LRC_TAG.findAll(line).toList()
        if (matches.isEmpty()) return@forEach
        val text = line.substring(matches.last().range.last + 1).trim()
        matches.forEach { m ->
            val min = m.groupValues[1].toLongOrNull() ?: return@forEach
            val sec = m.groupValues[2].toLongOrNull() ?: return@forEach
            val fracStr = m.groupValues[3]
            val frac = when {
                fracStr.isEmpty() -> 0L
                fracStr.length == 2 -> fracStr.toLong() * 10
                fracStr.length == 3 -> fracStr.toLong()
                else -> fracStr.padEnd(3, '0').take(3).toLong()
            }
            out += LyricsLine(min * 60_000 + sec * 1000 + frac, text)
        }
    }
    return out.sortedBy { it.timeMs }
}
