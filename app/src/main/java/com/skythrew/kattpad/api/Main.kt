package com.skythrew.kattpad.api

import com.skythrew.kattpad.api.requests.ListData
import com.skythrew.kattpad.api.requests.SearchResult
import com.skythrew.kattpad.api.requests.StoriesSearchResult
import com.skythrew.kattpad.api.requests.StoryData
import com.skythrew.kattpad.api.requests.StoryPartData
import com.skythrew.kattpad.api.requests.UserData

class Wattpad (cookie: String = "") : Authentication(cookie) {
    suspend fun fetchList(id: Int, fields: Set<String> = setOf()): WattpadList {
        val listData = fetchObjData<ListData>("v3", "lists/$id", fields, 0, 0)

        return WattpadList(this, listData)
    }

    suspend fun fetchStory(id: Int, fields: Set<String> = setOf()): Story {
        val storyData = fetchObjData<StoryData>("v3", "stories/$id", setOf("id", "user").plus(fields), 0, 0)

        return Story(this, storyData)
    }

    suspend fun fetchStoryPart(id: Int, fields: Set<String> = setOf()): Part {
        val storyPartData = fetchObjData<StoryPartData>("v4", "parts/$id", setOf("id", "text_url").plus(fields), 0, 0)

        return Part(this, storyPartData)
    }

    suspend fun fetchUser(username: String, fields: Set<String> = setOf()): User {
        val userData = fetchObjData<UserData>("v3", "users/$username", fields, 0, 0)

        return User(this, userData)
    }

    suspend fun searchStory(title: String, offset: Int = 0): List<Story> {
        val params = mapOf(
            "query" to title,
            "offset" to offset.toString()
        )

        val result = fetch<StoriesSearchResult>("v4", "search/stories/", params)

        return result.stories.map {storyData ->
            Story(
                this,
                StoryData(
                    storyData.id,
                    storyData.title,
                    storyData.length,
                    storyData.createDate,
                    storyData.modifyDate,
                    storyData.voteCount,
                    storyData.readCount,
                    storyData.commentCount,
                    storyData.language,
                    storyData.description,
                    storyData.cover,
                    storyData.coverTimestamp,
                    storyData.completed,
                    storyData.categories,
                    storyData.tags,
                    storyData.rating,
                    storyData.mature,
                    storyData.copyright,
                    storyData.url,
                    null,
                    storyData.numParts,
                    null,
                    when (storyData.lastPublishedPart) {
                        null -> null
                        else -> StoryPartData(
                            createDate = storyData.lastPublishedPart.createDate
                        )
                    },
                    null,
                    storyData.user
                    )
            )
        }
    }

    suspend fun searchUser(username: String, offset: Int = 0): List<User> {
        val params = mapOf(
            "query" to username,
            "offset" to offset.toString()
        )

        val result = fetch<List<UserData>>("v4", "search/users/", params)

        return result.map { data -> User(this, data)}
    }

    suspend fun search(query: String): SearchResult {
        val userSearch = searchUser(query)
        val storySearch = searchStory(query)

        return SearchResult(userSearch, storySearch)
    }
}