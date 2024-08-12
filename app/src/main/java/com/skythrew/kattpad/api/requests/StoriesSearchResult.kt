package com.skythrew.kattpad.api.requests

import kotlinx.serialization.Serializable

@Serializable
data class StoriesSearchResult(
    val total: Int,
    val stories: List<SearchStoryData>
)
