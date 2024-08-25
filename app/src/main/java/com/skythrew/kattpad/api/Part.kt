package com.skythrew.kattpad.api

import com.skythrew.kattpad.api.requests.CommentData
import com.skythrew.kattpad.api.requests.CommentPostData
import com.skythrew.kattpad.api.requests.CommentResource
import com.skythrew.kattpad.api.requests.CommentSentiments
import com.skythrew.kattpad.api.requests.CommentsResult
import com.skythrew.kattpad.api.requests.MinUserData
import com.skythrew.kattpad.api.requests.StoryPartData
import com.skythrew.kattpad.api.requests.VotePostData
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.formUrlEncode
import io.ktor.http.parameters
import kotlinx.serialization.json.Json

/**
 * A class representing a story part.
 *
 * @property client The Wattpad client to use for requests
 * @property data The story part data
 */
data class Part (
    private val client: Wattpad,
    val data: StoryPartData
) {
    /**
     * Fetches comments on the story part
     *
     * @param limit The maximum number of comments to fetch
     * @param after A comment ID which is used to only fetch comments after the one with the given ID
     */
    suspend fun fetchComments(limit: Int = 0, after: String? = null): Pair<Boolean, List<Comment>> {
        val res = client.getAPI("v5", "comments/namespaces/parts/resources/${data.id}/comments") {
            url {
                parameters.append("limit", limit.toString())
                if(after != null) parameters.append("after", after)
            }
        }

        val commentsData = client.jsonDecoder.decodeFromString<CommentsResult>(res.bodyAsText())

        return Pair(commentsData.pagination.after != null, commentsData.comments.map {data -> Comment(client, data)})
    }

    /**
     * Fetches the text of the story part
     */
    suspend fun fetchText(): String {
        if (data.textUrl == null)
            throw Exception("Cannot fetch part content if text_url field is not fetched")

        return client.simpleGet(data.textUrl.text).bodyAsText()
    }

    /**
     * Votes for the story part
     *
     * @return `VotePostData`, `null` if there's any error
     */
    suspend fun vote(): VotePostData? {
        if (!client.loggedIn || data.story == null)
            return null

        val res = client.makeRequest("v3", "stories/${data.story.id}/parts/${data.id}/votes") {
            method = HttpMethod.Post
        }

        if (res.status == HttpStatusCode.OK) {
            data.voted = true

            return Json.decodeFromString<VotePostData>(res.bodyAsText())
        } else {
            return null
        }
    }

    /**
     * Deletes the vote for the story part
     *
     * @return `VotePostData`, `null` if there's any error
     */
    suspend fun unvote(): VotePostData? {
        if (!client.loggedIn || data.story == null)
            return null

        val res = client.makeRequest("v3", "stories/${data.story.id}/parts/${data.id}/votes") {
            method = HttpMethod.Delete
        }

        if (res.status == HttpStatusCode.OK) {
            data.voted = false

            return Json.decodeFromString<VotePostData>(res.bodyAsText())
        } else {
            return null
        }
    }

    /**
     * Votes for the story part if the user has not voted yet, deletes the vote otherwise
     *
     * @return `VotePostData`, `null` if there's any error
     */
    suspend fun toggleVote(): VotePostData? {
        if (!client.loggedIn || data.story == null)
            return null

        return if (data.voted!!)
            unvote()
        else
            vote()
    }

    /**
     * Comments on the story part
     *
     * @param text The comment text
     * @return `Comment`, `null` if there's any error
     */
    suspend fun comment(text: String): Comment? {
        if (!client.loggedIn)
            return null

        val res = client.postAPI("v4", "parts/${data.id}/comments") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("body" to text))
        }

        if (res.status == HttpStatusCode.OK) {
            val postData = Json.decodeFromString<CommentPostData>(res.bodyAsText())
            val commentData = CommentData(
                resource = CommentResource("", ""),
                user = MinUserData(postData.author.username, postData.author.avatar),
                commentId = CommentResource("comments", postData.id),
                text = postData.body,
                created = postData.createDate,
                modified = postData.createDate,
                status = "",
                sentiments = CommentSentiments(),
                replyCount = 0,
                deeplink = postData.deeplink
            )

            return Comment(client, commentData)
        }
        else
            return null
    }

    /**
     * Marks the story part as the part currently being read by the user (eg. to calculate the number of chapters left)
     *
     * @return `true` if the request succeeds, `false` otherwise, `null` if the client is not logged in
     */
    suspend fun syncReadingPosition(): Boolean? {
        if (!client.loggedIn)
            return null

        val res =  client.postAPI("v2", "syncreadingposition") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(parameters {
                append("story_id", data.id!!.toString())
                append("position", "1")
            }.formUrlEncode())
        }

        return res.status == HttpStatusCode.OK
    }
}