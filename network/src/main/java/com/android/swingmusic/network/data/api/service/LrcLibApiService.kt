package com.android.swingmusic.network.data.api.service

import com.android.swingmusic.core.data.dto.LrcLibLyricsDto
import retrofit2.http.GET
import retrofit2.http.Query

interface LrcLibApiService {
    @GET("api/get")
    suspend fun getLyrics(
        @Query("track_name") trackName: String,
        @Query("artist_name") artistName: String,
        @Query("album_name") albumName: String? = null,
        @Query("duration") duration: Int? = null,
    ): LrcLibLyricsDto

    @GET("api/search")
    suspend fun searchLyrics(
        @Query("track_name") trackName: String,
        @Query("artist_name") artistName: String? = null,
    ): List<LrcLibLyricsDto>
}
