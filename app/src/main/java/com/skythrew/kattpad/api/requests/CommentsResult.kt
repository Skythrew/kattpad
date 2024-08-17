package com.skythrew.kattpad.api.requests

import kotlinx.serialization.Serializable

@Serializable
data class CommentsPagination(
    val after: CommentResource? = null
)

@Serializable
data class CommentsResult(
    val pagination: CommentsPagination,
    val comments: List<CommentData>
)
