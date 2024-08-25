package com.skythrew.kattpad.api

import com.skythrew.kattpad.api.requests.FollowersResult
import com.skythrew.kattpad.api.requests.FollowingResult
import com.skythrew.kattpad.api.requests.LibraryData
import com.skythrew.kattpad.api.requests.ListsResult
import com.skythrew.kattpad.api.requests.UserData
import com.skythrew.kattpad.api.requests.UserMessagesResponse
import com.skythrew.kattpad.api.requests.UserStoriesResult
import io.ktor.http.HttpStatusCode

/**
 * A class representing a Wattpad user.
 *
 * @property client The Wattpad client to use for requests
 * @property data The user's data
 */
class User (
    private val client: Wattpad = Wattpad(),
    val data: UserData
) {
    private val userRequest = "users/${data.username}"

    /**
     * Fetches the followers of the user
     *
     * @param fields Request fields to fetch (defaults to all fields)
     * @param limit The maximum number of followers to fetch
     * @param offset Request offset
     *
     * @return A list of users
     */
    suspend fun fetchFollowers(fields: Set<String> = setOf(), limit: Int = 0, offset: Int = 0): List<User> {
        val followersResult = client.fetchObjData<FollowersResult>("v3", "$userRequest/followers", setOf("total", "users(username,${fields.joinToString(",")})"), limit, offset)

        return followersResult.users.map {data -> User(client, data) }
    }

    /**
     * Fetches the users followed by the user represented by the instance of this class
     *
     * @param fields Request fields to fetch (defaults to all fields)
     * @param limit The maximum number of followed users to fetch
     * @param offset Request offset
     *
     * @return A list of users
     */
    suspend fun fetchFollowing(fields: Set<String> = setOf(), limit: Int = 0, offset: Int = 0): List<User> {
        val followingResult = client.fetchObjData<FollowingResult>("v3", "$userRequest/following", setOf("total", "users(username,${fields.joinToString(",")})"), limit, offset)

        return followingResult.users.map {data -> User(client, data) }
    }

    /**
     * Fetches the reading lists of the user
     *
     * @param fields Request fields to fetch (defaults to all fields)
     * @param limit The maximum number of reading lists to fetch
     *
     * @return A list of reading lists
     */
    suspend fun fetchLists(fields: Set<String> = setOf(), limit: Int = 0): List<WattpadList> {
        val listsResult = client.fetchObjData<ListsResult>("v3", "$userRequest/lists", setOf("total", "lists(id,${fields.joinToString(",")})"), limit)

        return listsResult.lists.map {data -> WattpadList(client, data) }
    }

    /**
     * Fetches stories written by the user
     *
     * @param fields Request fields to fetch (defaults the "title" field)
     * @param limit The maximum number of followed users to fetch
     * @param offset Request offset
     *
     * @return A list of stories
     */
    suspend fun fetchStories(fields: Set<String> = setOf("title"), limit: Int = 0, offset: Int = 0): List<Story> {
        val storiesResult = client.fetchObjData<UserStoriesResult>("v4", "$userRequest/stories/published", setOf("total", "stories(id,user,${fields.joinToString(",")})"), limit, offset)

        return storiesResult.stories.map {data -> Story(client, data) }
    }

    /**
     * Fetches the library of the user
     *
     * @param fields Request fields to fetch (defaults the "title" field)
     *
     * @return A list of stories
     */
    suspend fun fetchLibrary(fields: Set<String> = setOf()): LibraryData {
        return client.fetchObjData<LibraryData>("v3", "$userRequest/library", fields)
    }

    /**
     * Follows the user
     *
     * @return `true` if the request succeeds, `false` otherwise, `null` if the client is not logged in
     */
    suspend fun follow(): Boolean? {
        if (!client.loggedIn)
            return null

        val res = client.postAPI("v3", "users/${client.username}/following") {
            url {
                parameters.append("users", data.username)
            }
        }

        if (res.status == HttpStatusCode.OK)
            data.following = true

        return res.status == HttpStatusCode.OK
    }

    /**
     * Unfollows the user
     *
     * @return `true` if the request succeeds, `false` otherwise, `null` if the client is not logged in
     */
    suspend fun unfollow(): Boolean? {
        if (!client.loggedIn)
            return null

        val res = client.deleteAPI("v3", "users/${client.username}/following/${data.username}") {}

        if (res.status == HttpStatusCode.OK)
            data.following = false

        return res.status == HttpStatusCode.OK
    }
}