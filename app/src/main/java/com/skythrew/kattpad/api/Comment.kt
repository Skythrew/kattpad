package com.skythrew.kattpad.api

import com.skythrew.kattpad.api.requests.CommentData
import com.skythrew.kattpad.api.requests.CommentsResult

class Comment (
    private val client: Wattpad,
    val data: CommentData
) {
    suspend fun fetchReplies(fields: Set<String> = setOf(), limit: Int = 0): List<Comment> {
        val repliesResult = client.fetchObjData<CommentsResult>("v5", "comments/namespaces/comments/resources/${data.commentId.resourceId}/comments", fields, limit)

        return repliesResult.comments.map {data -> Comment(client, data) }
    }
}