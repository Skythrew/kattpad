package com.skythrew.kattpad.api.requests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LibraryData(
    val stories: List<StoryData>,
    val total: Int,
    @SerialName("last_sync_timestamp") val lastSyncTimestamp: String
)
