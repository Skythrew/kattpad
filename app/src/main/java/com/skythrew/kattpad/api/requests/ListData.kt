package com.skythrew.kattpad.api.requests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ListData(
    val id: Int,
    val name: String? = null,
    val user: MinUserData? = null,
    val numStories: Int? = null,
    @SerialName("sample_covers") val sampleCovers: List<String>? = null,
    val cover: String? = null,
    val featured: Boolean? = null,
    val tags: Set<String>? = null,
    val stories: List<StoryData>? = null
)