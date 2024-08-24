package com.skythrew.kattpad.api.requests

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import java.util.Date

@Serializable
data class ActionUrl(
    val deeplink: String? = null,
    val standard: String? = null
)

@Serializable
data class NotificationImage(
    val url: String? = null,
    val callToActionUrl: ActionUrl? = null
)

@Serializable
data class NotificationImages(
    val left: NotificationImage? = null
)

@Serializable
data class NotificationStoryPartData(
    val id: Int? = null,
    val title: String? = null,
    val index: Int? = null,
    val url: String? = null
)

@Serializable
data class NotificationStoryData(
    val id: Int? = null,
    val title: String? = null,
    val description: String? = null,
    val cover: String? = null,
    val voteCount: Int? = null,
    val readCount: Int? = null,
    val url: String? = null,
    val part: NotificationStoryPartData? = null,
    val numParts: Int? = null,
    val user: MinUserData? = null,
    val category1: String? = null,
    val category2: String? = null,
    val tag: String? = null
)

@Serializable
data class NotificationCommentData(
    val id: String? = null,
    val body: String? = null,
    val user: MinUserData? = null,
    val parentId: String? = null,
    @SerialName("notification_instance_id") val notificationInstanceId: String? = null
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class NotificationData(
    val comment: NotificationCommentData? = null,
    val story: NotificationStoryData? = null,
    @SerialName("highlight_colour") val highlightColor: String? = null,
    val icon: String? = null,
    @JsonNames("sub_type") val subtype: String? = null,
    val body: String? = null,
    val images: NotificationImages? = null,
    val callToActionUrl: ActionUrl? = null,
    @SerialName("notification_instance_id") val notificationInstanceId: String? = null
)

@Serializable
data class NotificationItem(
    val id: Long? = null,
    val type: String? = null,
    @Serializable(with = DateSerializer::class) val createDate: Date? = null,
    val data: NotificationData,
    val isRead: Boolean? = null
)

@Serializable
data class NotificationResponse(
    val feed: List<NotificationItem>? = null,
    val total: Int? = null,
    val hasMore: Boolean? = null,
    val unreadTotal: Int? = null,
    val nextUrl: String? = null
)
