package com.skythrew.kattpad.api

import com.skythrew.kattpad.api.requests.ListData
import com.skythrew.kattpad.api.requests.ReadingListStoriesResponse

/**
 * A class representing a user's reading list.
 *
 * @property client The Wattpad client to use for requests
 * @property data Reading list data
 */
class WattpadList (
    private val client: Wattpad,
    val data: ListData
) {
    suspend fun fetchStories(fields: Set<String> = setOf(), limit: Int = 0, offset: Int = 0): ReadingListStoriesResponse {
        return client.fetchObjData<ReadingListStoriesResponse>(
            api = "v3",
            path = "lists/${data.id}/stories",
            fields = fields,
            limit = limit,
            offset = offset
        )
    }
}