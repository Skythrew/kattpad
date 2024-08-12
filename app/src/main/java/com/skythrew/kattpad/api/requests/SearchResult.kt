package com.skythrew.kattpad.api.requests

import com.skythrew.kattpad.api.Story
import com.skythrew.kattpad.api.User

data class SearchResult(
    val users: List<User>,
    val stories: List<Story>
)
