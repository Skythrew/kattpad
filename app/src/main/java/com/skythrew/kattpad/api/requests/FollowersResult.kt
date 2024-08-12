package com.skythrew.kattpad.api.requests

import kotlinx.serialization.Serializable

@Serializable
data class FollowersResult(
    val users: List<UserData>,
    val total: Int
)
