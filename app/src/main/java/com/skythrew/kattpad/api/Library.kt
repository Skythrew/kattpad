package com.skythrew.kattpad.api

import com.skythrew.kattpad.api.requests.LibraryData

/**
 * A class representing a user's library
 *
 * @property client The Wattpad client to use for requests
 * @property data Library data
 */
class Library (
    private val client: Wattpad,
    val data: LibraryData
)