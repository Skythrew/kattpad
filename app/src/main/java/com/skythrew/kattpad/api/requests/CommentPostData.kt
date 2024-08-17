package com.skythrew.kattpad.api.requests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class CommentPostAuthor(
    val avatar: String,
    @SerialName("name") val username: String
)

@Serializable
data class CommentPostData(
    val author: CommentPostAuthor,
    val body: String,
    @Serializable(with = DateSerializer::class) val createDate: Date,
    val startPosition: Int,
    val endPosition: Int,
    val id: String,
    val isOffensive: Boolean,
    val isReply: Boolean,
    val numReplies: Int,
    val paragraphId: Int?,
    val parentId: String?,
    val partId: Int,
    val deeplink: String,
    val legacyId: Int
)
