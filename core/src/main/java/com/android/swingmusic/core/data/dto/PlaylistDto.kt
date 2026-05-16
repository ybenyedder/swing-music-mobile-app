package com.android.swingmusic.core.data.dto

import com.google.gson.annotations.SerializedName

data class PlaylistDto(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("image") val image: String? = null,
    @SerializedName("thumb") val thumb: String? = null,
    @SerializedName("count") val count: Int? = 0,
    @SerializedName("duration") val duration: Int? = 0,
    @SerializedName("last_updated") val lastUpdated: String? = null,
    @SerializedName("has_image") val hasImage: Boolean? = false,
    @SerializedName("pinned") val pinned: Boolean? = false,
)

data class PlaylistsResponseDto(
    @SerializedName("data") val data: List<PlaylistDto>? = emptyList()
)

data class PlaylistTracksResponseDto(
    @SerializedName("info") val info: PlaylistDto? = null,
    @SerializedName("tracks") val tracks: List<TrackDto>? = emptyList(),
)
