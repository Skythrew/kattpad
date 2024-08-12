package com.skythrew.kattpad.api

import com.skythrew.kattpad.api.requests.ListData

class WattpadList (
    private val client: Wattpad,
    val data: ListData
)