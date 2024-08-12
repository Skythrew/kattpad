package com.skythrew.kattpad.api

import com.skythrew.kattpad.api.requests.StoryData


data class Story (
    private val client: Wattpad = Wattpad(),
    val data: StoryData
) {
    val parts: MutableList<Part> = mutableListOf()

    init {
        data.parts?.forEach {data -> parts.add(Part(client, data))}
    }

    suspend fun fetchUser(fields: Set<String> = setOf()): User {
        return client.fetchUser(data.user.username, fields)
    }
}