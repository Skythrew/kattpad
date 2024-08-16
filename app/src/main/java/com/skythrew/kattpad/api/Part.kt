package com.skythrew.kattpad.api

import com.skythrew.kattpad.api.requests.CommentsResult
import com.skythrew.kattpad.api.requests.StoryPartData
import com.skythrew.kattpad.api.requests.VotePostData
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.json.Json

data class Part (
    private val client: Wattpad,
    val data: StoryPartData
) {
    suspend fun fetchComments(fields: Set<String> = setOf(), limit: Int = 0): List<Comment> {
        val commentsData = client.fetchObjData<CommentsResult>("v5", "comments/namespaces/parts/resources/${data.id}/comments", fields, limit)

        return commentsData.comments.map {data -> Comment(client, data)}
    }

    suspend fun fetchText(): String {
        if (data.textUrl == null)
            throw Exception("Cannot fetch part content if text_url field is not fetched")

        return client.simpleGet(data.textUrl.text).bodyAsText()
    }

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

    suspend fun toggleVote(): VotePostData? {
        if (!client.loggedIn || data.story == null)
            return null

        return if (data.voted!!)
            unvote()
        else
            vote()
    }
}