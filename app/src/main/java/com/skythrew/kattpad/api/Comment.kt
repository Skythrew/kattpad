package com.skythrew.kattpad.api

import com.skythrew.kattpad.api.requests.CommentData
import com.skythrew.kattpad.api.requests.CommentsResult
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode

class Comment (
    private val client: Wattpad,
    val data: CommentData
) {
    suspend fun delete(): Boolean? {
        if (!client.loggedIn)
            return null

        if (client.username != data.user.username)
            return null

        val res = client.makeRequest("v4", "comments/${data.commentId.resourceId}") {
            method = HttpMethod.Delete
        }

        return res.status == HttpStatusCode.OK
    }

    suspend fun like(): Boolean? {
        if (!client.loggedIn)
            return null

        val res = client.putAPI("v5", "comments/namespaces/comments/resources/${data.commentId.resourceId}/sentiments/:like:") {}

        return res.status == HttpStatusCode.OK
    }

    suspend fun unlike(): Boolean? {
        if (!client.loggedIn)
            return null

        val res = client.deleteAPI("v5", "comments/namespaces/comments/resources/${data.commentId.resourceId}/sentiments/:like:") {}

        return res.status == HttpStatusCode.OK
    }

    suspend fun fetchReplies(fields: Set<String> = setOf(), limit: Int = 0): List<Comment> {
        val repliesResult = client.fetchObjData<CommentsResult>(
            api = "v5",
            path = "comments/namespaces/comments/resources/${data.commentId.resourceId}/comments",
            fields = fields,
            limit = limit,
            offset = 0
        )

        return repliesResult.comments.map {data -> Comment(client, data) }
    }
}