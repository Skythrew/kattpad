package com.skythrew.kattpad.api.requests

import kotlinx.serialization.Serializable

@Serializable
data class CommentsResult(
    val comments: List<CommentData>
)
