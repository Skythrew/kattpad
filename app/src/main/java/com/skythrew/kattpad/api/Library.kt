package com.skythrew.kattpad.api

import com.skythrew.kattpad.api.requests.LibraryData

class Library (
    private val client: Wattpad,
    val data: LibraryData
)