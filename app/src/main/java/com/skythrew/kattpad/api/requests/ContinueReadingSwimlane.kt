package com.skythrew.kattpad.api.requests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class CurrentPart(
    val id: Int,
    val number: Int
)

@Serializable
data class SwimlaneItemData(
    val id: Int,
    val title: String,
    val cover: String,
    @SerialName("cover_requires_opt_in") val coverRequiresOptIn: Boolean,
    @SerialName("total_parts") val totalParts: Int,
    @SerialName("current_part") val currentPart: CurrentPart,
    @SerialName("new_parts") val newParts: Int,
    @Serializable(with = DateSerializer::class) @SerialName("last_pub_date") val lastPubDate: Date,
    @Serializable(with = DateSerializer::class) @SerialName("last_accessed") val lastAccessed: Date
)

@Serializable
data class SwimlaneItem(
    val heading: String,
    val type: String,
    val data: List<SwimlaneItemData>
)

@Serializable
data class ContinueReadingSwimlaneData(
    val heading: String,
    val items: List<SwimlaneItem>,
    val subheading: String
)

@Serializable
data class ContinueReadingSwimlane(
    val type: String,
    val data: ContinueReadingSwimlaneData
)

@Serializable
data class ContinueReadingParsedData(
    val currentRead: List<SwimlaneItemData>,
    val jumpBackIn: List<SwimlaneItemData>
)