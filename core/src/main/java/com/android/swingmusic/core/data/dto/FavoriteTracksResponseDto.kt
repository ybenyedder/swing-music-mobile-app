package com.android.swingmusic.core.data.dto

import com.google.gson.annotations.SerializedName

data class FavoriteTracksResponseDto(
    @SerializedName("total") val total: Int? = 0,
    @SerializedName("tracks") val tracks: List<TrackDto>? = emptyList()
)
