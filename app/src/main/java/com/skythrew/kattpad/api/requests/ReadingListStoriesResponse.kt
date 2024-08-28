package com.skythrew.kattpad.api.requests

import kotlinx.serialization.Serializable

@Serializable
data class ReadingListStoriesResponse(
    val id: Int? = null,
    val name: String? = null,
    val stories: List<StoryData>,
    val total: Int? = null,
    val nextUrl: String? = null
)
