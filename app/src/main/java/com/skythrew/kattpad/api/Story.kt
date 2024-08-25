package com.skythrew.kattpad.api

import com.skythrew.kattpad.api.requests.StoryData

/**
 * A class representing a story.
 *
 * @property client The Wattpad client to use for requests
 * @property data Story data
 */
data class Story (
    private val client: Wattpad = Wattpad(),
    val data: StoryData
)