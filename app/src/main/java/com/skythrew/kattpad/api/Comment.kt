package com.skythrew.kattpad.api

import com.skythrew.kattpad.api.requests.CommentData
import com.skythrew.kattpad.api.requests.CommentsResult
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode

/**
 * A class representing a comment on a story part.
 *
 * @property client The client to use for requests
 * @property data Comment data
 */
class Comment (
    private val client: Wattpad,
    val data: CommentData
) {
    /**
     * Deletes the comment
     *
     * @return `true` if the request succeeds, `false` otherwise, `null` if the client is not logged in
     */
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

    /**
     * Likes the comment
     *
     * @return `true` if the request succeeds, `false` otherwise, `null` if the client is not logged in
     */
    suspend fun like(): Boolean? {
        if (!client.loggedIn)
            return null

        val res = client.putAPI("v5", "comments/namespaces/comments/resources/${data.commentId.resourceId}/sentiments/:like:") {}

        return res.status == HttpStatusCode.OK
    }

    /**
     * Deletes the like from the comment
     *
     * @return `true` if the request succeeds, `false` otherwise, `null` if the client is not logged in
     */
    suspend fun unlike(): Boolean? {
        if (!client.loggedIn)
            return null

        val res = client.deleteAPI("v5", "comments/namespaces/comments/resources/${data.commentId.resourceId}/sentiments/:like:") {}

        return res.status == HttpStatusCode.OK
    }

    /**
     * Fetches the comment's replies
     *
     * @param fields Request fields to fetch (defaults to all fields)
     * @param limit The maximum number of replies to fetch (may be useless ??)
     */
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