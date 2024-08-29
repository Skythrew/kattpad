package com.skythrew.kattpad.api.requests

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import java.util.Date

@Serializable
@Parcelize
data class ActionUrl(
    val deeplink: String? = null,
    val standard: String? = null
) : Parcelable

@Serializable
@Parcelize
data class NotificationImage(
    val url: String? = null,
    val callToActionUrl: ActionUrl? = null
) : Parcelable

@Serializable
@Parcelize
data class NotificationImages(
    val left: NotificationImage? = null
) : Parcelable

@Serializable
@Parcelize
data class NotificationStoryPartData(
    val id: Int? = null,
    val title: String? = null,
    val index: Int? = null,
    val url: String? = null
) : Parcelable

@Serializable
@Parcelize
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
) : Parcelable

@Serializable
@Parcelize
data class NotificationCommentData(
    val id: String? = null,
    val body: String? = null,
    val user: MinUserData? = null,
    val parentId: String? = null,
    @SerialName("notification_instance_id") val notificationInstanceId: String? = null
) : Parcelable

@Serializable
@Parcelize
data class NotificationUserMessageData(
    val id: Long? = null,
    val body: String? = null,
    @Serializable(with = DateSerializer::class) val createDate: Date? = null,
    val from: MinUserData? = null,
    val to: MinUserData? = null,
    val numReplies: Int? = null,
    val isReply: Boolean? = null,
    val isOffensive: Boolean? = null,
    val latestReplies: List<NotificationUserMessageData>? = null,
    val parentId: Long? = null,
    val wasBroadcast: Boolean? = null
) : Parcelable

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@Parcelize
data class NotificationData(
    val comment: NotificationCommentData? = null,
    val story: NotificationStoryData? = null,
    @SerialName("highlight_colour") val highlightColor: String? = null,
    val icon: String? = null,
    @JsonNames("sub_type") val subtype: String? = null,
    val body: String? = null,
    val images: NotificationImages? = null,
    val callToActionUrl: ActionUrl? = null,
    @SerialName("notification_instance_id") val notificationInstanceId: String? = null,
    val followed: MinUserData? = null,
    val follower: MinUserData? = null,
    val message: NotificationUserMessageData? = null,
    val voter: MinUserData? = null
) : Parcelable

@Serializable
@Parcelize
data class NotificationItem(
    val id: Long? = null,
    val type: String? = null,
    @Serializable(with = DateSerializer::class) val createDate: Date? = null,
    val data: NotificationData,
    val isRead: Boolean? = null
) : Parcelable

@Serializable
@Parcelize
data class NotificationResponse(
    val feed: List<NotificationItem>? = null,
    val total: Int? = null,
    val hasMore: Boolean? = null,
    val unreadTotal: Int? = null,
    val nextUrl: String? = null
) : Parcelable
