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
    /**
     * Fetches the stories of the list represented by this class.
     *
     * @param fields Request fields to fetch (defaults to all fields)
     * @param limit The maximum number of stories to fetch
     * @param offset Request offset
     *
     * @return `ReadingListStoriesResponse` -> raw API response
     */
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