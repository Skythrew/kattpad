package com.skythrew.kattpad.api.requests

import kotlinx.serialization.Serializable

@Serializable
data class ListsResult(
    val lists: List<ListData>,
    val total: Int
)
