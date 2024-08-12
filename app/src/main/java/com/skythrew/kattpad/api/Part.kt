package com.skythrew.kattpad.api

import com.skythrew.kattpad.api.requests.CommentsResult
import com.skythrew.kattpad.api.requests.StoryPartData
import io.ktor.client.statement.*

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
}