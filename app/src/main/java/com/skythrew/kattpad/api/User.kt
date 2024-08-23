package com.skythrew.kattpad.api

import com.skythrew.kattpad.api.requests.FollowersResult
import com.skythrew.kattpad.api.requests.FollowingResult
import com.skythrew.kattpad.api.requests.LibraryData
import com.skythrew.kattpad.api.requests.ListsResult
import com.skythrew.kattpad.api.requests.UserData
import com.skythrew.kattpad.api.requests.UserStoriesResult

class User (
    private val client: Wattpad = Wattpad(),
    val data: UserData
) {
    private val userRequest = "users/${data.username}"

    suspend fun fetchFollowers(fields: Set<String> = setOf(), limit: Int = 0, offset: Int = 0): List<User> {
        val followersResult = client.fetchObjData<FollowersResult>("v3", "$userRequest/followers", setOf("total", "users(username,${fields.joinToString(",")})"), limit, offset)

        return followersResult.users.map {data -> User(client, data) }
    }

    suspend fun fetchFollowing(fields: Set<String> = setOf(), limit: Int = 0, offset: Int = 0): List<User> {
        val followingResult = client.fetchObjData<FollowingResult>("v3", "$userRequest/following", setOf("total", "users(username,${fields.joinToString(",")})"), limit, offset)

        return followingResult.users.map {data -> User(client, data) }
    }

    suspend fun fetchLists(fields: Set<String> = setOf(), limit: Int = 0): List<WattpadList> {
        val listsResult = client.fetchObjData<ListsResult>("v3", "$userRequest/lists", setOf("total", "lists(id,${fields.joinToString(",")})"), limit)

        return listsResult.lists.map {data -> WattpadList(client, data) }
    }

    suspend fun fetchStories(fields: Set<String> = setOf("title"), limit: Int = 0, offset: Int = 0): List<Story> {
        val storiesResult = client.fetchObjData<UserStoriesResult>("v4", "$userRequest/stories/published", setOf("total", "stories(id,user,${fields.joinToString(",")})"), limit, offset)

        return storiesResult.stories.map {data -> Story(client, data) }
    }

    suspend fun fetchLibrary(fields: Set<String> = setOf()): LibraryData {
        return client.fetchObjData<LibraryData>("v3", "$userRequest/library", fields)
    }
}