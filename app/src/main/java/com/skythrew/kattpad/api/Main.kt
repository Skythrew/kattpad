package com.skythrew.kattpad.api

import com.skythrew.kattpad.api.requests.ListData
import com.skythrew.kattpad.api.requests.NotificationResponse
import com.skythrew.kattpad.api.requests.StoriesSearchResult
import com.skythrew.kattpad.api.requests.StoryData
import com.skythrew.kattpad.api.requests.StoryPartData
import com.skythrew.kattpad.api.requests.UserData
import io.ktor.http.HttpStatusCode

/**
 * The Wattpad client
 *
 * This client should be used to fetch all the needed remote Wattpad data.
 */
class Wattpad : Authentication() {
    /**
     * Fetches the library of the current client user
     * @return `Library` instance or `null` if the client is not logged in
     */
    suspend fun fetchLibrary(): Library? {
        if (!this.loggedIn)
            return null

        return Library(this, User(this, UserData(username = this.username!!)).fetchLibrary())
    }

    /**
     * Fetches a reading list by its ID.
     *
     * @param id The reading list ID
     * @param fields Request fields to fetch (defaults to all fields)
     * @return `WattpadList` instance
     */
    suspend fun fetchList(id: Int, fields: Set<String> = setOf()): WattpadList {
        val listData = fetchObjData<ListData>("v3", "lists/$id", fields, 0, 0)

        return WattpadList(this, listData)
    }

    /**
     * Fetches the logged user's notifications
     *
     * @param fields Request fields to fetch (defaults to all fields)
     * @param limit The maximum number of notifications to fetch
     * @param newestId The newest notification to fetch (acts as a kind of offset)
     * @return `NotificationResponse` or `null` if the client is not logged in
     */
    suspend fun fetchNotifications(fields: Set<String> = setOf(), limit: Int = 0, newestId: Long? = null): NotificationResponse? {
        if (!this.loggedIn)
            return null

        val res = fetch<NotificationResponse>("v3", "users/${this.username}/notifications", mapOf(
            "limit" to limit.toString(),
            when{
                newestId != null -> "newest_id" to newestId.toString()
                else -> {"" to ""}
            }
        ))

        return res
    }

    /**
     * Fetches a story by its ID.
     *
     * @param id Story id
     * @param fields Request fields to fetch (defaults to all fields)
     * @return `Story` instance
     */
    suspend fun fetchStory(id: Int, fields: Set<String> = setOf()): Story {
        val storyData = fetchObjData<StoryData>("v3", "stories/$id", setOf("id", "user").plus(fields), 0, 0)

        return Story(this, storyData)
    }

    /**
     * Fetches a story part by its ID.
     *
     * @param id Story part id
     * @param fields Request fields to fetch (defaults to all fields)
     * @return `Part` instance
     */
    suspend fun fetchStoryPart(id: Int, fields: Set<String> = setOf()): Part {
        val storyPartData = fetchObjData<StoryPartData>("v4", "parts/$id", setOf("id", "text_url").plus(fields), 0, 0)

        return Part(this, storyPartData)
    }

    /**
     * Fetches a story part by its username.
     *
     * @param username Username
     * @param fields Request fields to fetch (defaults to all fields)
     * @return `User` instance
     */
    suspend fun fetchUser(username: String, fields: Set<String> = setOf()): User {
        val userData = fetchObjData<UserData>("v3", "users/$username", fields, 0, 0)

        return User(this, userData)
    }

    /**
     * Mark all the notifications as read
     *
     * @return `true` if the request succeeds, `false` otherwise, `null` if the client is not logged in
     */
    suspend fun markNotificationsAsRead(): Boolean? {
        if (!this.loggedIn)
            return null

        val res = this.simpleGet("https://www.wattpad.com/notifications")

        return res.status == HttpStatusCode.OK
    }

    /**
     * Searches for a story by a query
     *
     * @param query
     * @param offset Search request offset
     */
    suspend fun searchStory(query: String, offset: Int = 0): List<Story> {
        val params = mapOf(
            "query" to query,
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

    /**
     * Searches for a user by a query
     *
     * @param query
     * @param offset Search request offset
     */
    suspend fun searchUser(query: String, offset: Int = 0): List<User> {
        val params = mapOf(
            "query" to query,
            "offset" to offset.toString()
        )

        val result = fetch<List<UserData>>("v4", "search/users/", params)

        return result.map { data -> User(this, data)}
    }
}