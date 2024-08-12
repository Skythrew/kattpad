package com.skythrew.kattpad.api.requests

import kotlinx.serialization.Serializable

@Serializable
data class FollowingResult(
    val users: List<UserData>,
    val total: Int
)
