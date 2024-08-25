package com.skythrew.kattpad.api

import com.skythrew.kattpad.api.requests.ListData

/**
 * A class representing a user's reading list.
 *
 * @property client The Wattpad client to use for requests
 * @property data Reading list data
 */
class WattpadList (
    private val client: Wattpad,
    val data: ListData
)