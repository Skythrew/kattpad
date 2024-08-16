package com.skythrew.kattpad.api.requests

import kotlinx.serialization.Serializable

@Serializable
data class VotePostData(
    val votes: Int
)