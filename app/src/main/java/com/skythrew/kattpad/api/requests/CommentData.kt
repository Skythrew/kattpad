package com.skythrew.kattpad.api.requests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class CommentInteraction(
    val resource: CommentResource,
    val sentimentType: String,
    @Serializable(with = DateSerializer::class) val created: Date,
    val status: String
)

@Serializable
data class CommentSentiment(
    val count: Int,
    val interaction: CommentInteraction? = null
)

@Serializable
data class CommentSentiments (
    @SerialName(":like:") val like: CommentSentiment? = null
)

@Serializable
data class CommentResource (
    val namespace: String,
    val resourceId: String
)

@Serializable
data class CommentData(
    val resource: CommentResource,
    val user: MinUserData,
    val commentId: CommentResource,
    val text: String,
    @Serializable(with = DateSerializer::class) val created: Date,
    @Serializable(with = DateSerializer::class) val modified: Date,
    val status: String,
    val sentiments: CommentSentiments,
    val replyCount: Int,
    val deeplink: String
)
