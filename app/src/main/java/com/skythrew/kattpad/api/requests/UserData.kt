package com.skythrew.kattpad.api.requests

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import java.util.Date

@Serializable
data class MinUserData (
    @SerialName("name") val username: String,
    val avatar: String? = null,
    val fullname: String? = null
)

@Serializable
data class UserData (
    val username: String,
    val name: String? = null,
    val avatar: String? = null,
    val description: String? = null,
    val numFollowers: Int? = null,
    val numStoriesPublished: Int? = null,
    val numLists: Int? = null,
    val isPrivate: Boolean? = null,
    val backgroundUrl: String? = null,
    val status: String? = null,
    val gender: String? = null,
    val genderCode: String? = null,
    val language: Int? = null,
    val locale: String? = null,
    @Serializable(with = DateSerializer::class) val createDate: Date? = null,
    @Serializable(with = DateSerializer::class) val modifyDate: Date? = null,
    val location: String? = null,
    val verified: Boolean? = null,
    val ambassador: Boolean? = null,
    val facebook: String? = null,
    val website: String? = null,
    val lulu: String? = null,
    val smashwords: String? = null,
    val bubok: String? = null,
    val votesReceived: Int? = null,
    val numFollowing: Int? = null,
    val numMessages: Int? = null,
    @SerialName("verified_email") val verifiedEmail: Boolean? = null,
    val allowCrawler: Boolean? = null,
    val deeplink: String? = null,
    val isMuted: Boolean? = null
)