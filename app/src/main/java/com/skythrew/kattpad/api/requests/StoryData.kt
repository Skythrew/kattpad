package com.skythrew.kattpad.api.requests

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import java.util.*

@Serializable
data class StoryLanguage(
    val id: Int,
    val name: String? = null
)

@Serializable
data class StoryPartTextUrl(
    val text: String,
    @SerialName("refresh_token") val refreshToken: String
)

@Serializable
data class StoryPartData (
    val id: Int? = null,
    val title: String? = null,
    val url: String? = null,
    val rating: Int? = null,
    val draft: Boolean? = null,
    @Serializable(with = DateSerializer::class) val createDate: Date? = null,
    @Serializable(with = DateSerializer::class) val modifyDate: Date? = null,
    val length: Int? = null,
    val videoId: String? = null,
    val photoUrl: String? = null,
    val commentCount: Int? = null,
    val voteCount: Int? = null,
    val readCount: Int? = null,
    @SerialName("text_url") val textUrl: StoryPartTextUrl? = null,
    @SerialName("group") val story: StoryData? = null
)

@Serializable
data class StoryData(
    val id: Int,
    val title: String? = null,
    val length: Int? = null,
    @Serializable(with = DateSerializer::class) val createDate: Date? = null,
    @Serializable(with = DateSerializer::class) val modifyDate: Date? = null,
    val voteCount: Int? = null,
    val readCount: Int? = null,
    val commentCount: Int? = null,
    val language: StoryLanguage? = null,
    val description: String? = null,
    val cover: String? = null,
    @Serializable(with = DateSerializer::class) @SerialName("cover_timestamp") val coverTimestamp: Date? = null,
    val completed: Boolean? = null,
    val categories: List<Int>? = null,
    val tags: List<String>? = null,
    val rating: Int? = null,
    val mature: Boolean? = null,
    val copyright: Int? = null,
    val url: String? = null,
    val firstPartId: Int? = null,
    val numParts: Int? = null,
    val firstPublishedPart: StoryPartData? = null,
    val lastPublishedPart: StoryPartData? = null,
    val parts: List<StoryPartData>? = null,
    val user: MinUserData
)

@Serializable
data class SearchStoryPartData (
    @Serializable(with = SimpleDateSerializer::class) val createDate: Date? = null,
)

@Serializable
data class SearchStoryData(
    val id: Int,
    val title: String? = null,
    val length: Int? = null,
    @Serializable(with = SimpleDateSerializer::class) val createDate: Date? = null,
    @Serializable(with = SimpleDateSerializer::class) val modifyDate: Date? = null,
    val voteCount: Int? = null,
    val readCount: Int? = null,
    val commentCount: Int? = null,
    val language: StoryLanguage? = null,
    val description: String? = null,
    val cover: String? = null,
    @Serializable(with = SimpleDateSerializer::class) @SerialName("cover_timestamp") val coverTimestamp: Date? = null,
    val completed: Boolean? = null,
    val categories: List<Int>? = null,
    val tags: List<String>? = null,
    val rating: Int? = null,
    val mature: Boolean? = null,
    val copyright: Int? = null,
    val url: String? = null,
    val numParts: Int? = null,
    val lastPublishedPart: SearchStoryPartData? = null,
    val user: MinUserData
)