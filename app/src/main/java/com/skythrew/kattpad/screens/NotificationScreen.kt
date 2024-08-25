package com.skythrew.kattpad.screens

import android.text.format.DateFormat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.skythrew.kattpad.R
import com.skythrew.kattpad.api.Wattpad
import com.skythrew.kattpad.api.requests.NotificationItem
import com.skythrew.kattpad.api.requests.NotificationResponse
import com.skythrew.kattpad.screens.utils.navigateOnce
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.util.Date

@Composable
fun NotificationScreen(padding: PaddingValues, navController: NavController, client: Wattpad) {
    val lazyColState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var notificationResponse: NotificationResponse? by rememberSaveable {
        mutableStateOf(null)
    }

    val notifications = notificationResponse?.feed?.filter { item -> item.type != "generic" }

    LaunchedEffect(lazyColState.canScrollForward) {
        coroutineScope.launch {
            client.markNotificationsAsRead()
        }

        if(!lazyColState.canScrollForward) {
            if (notificationResponse == null)
                notificationResponse = client.fetchNotifications(limit = 15)
            else if (notificationResponse!!.hasMore!!) {
                val res = client.fetchNotifications(limit = 15, newestId = notificationResponse!!.feed!!.last().id)

                if (res != null)
                    notificationResponse = notificationResponse!!.copy(feed = notificationResponse!!.feed!!.plus(res.feed!!), hasMore = res.hasMore)
            }
        }
    }

    Column (
        modifier = Modifier
            .padding(padding)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (notifications == null)
            CircularProgressIndicator()
        else
            LazyColumn (
                modifier = Modifier.fillMaxSize(),
                state = lazyColState,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(notifications) { notification ->
                    when(notification.type) {
                        "upload" -> NotificationUpload(navController = navController, notification = notification)
                        "comment" -> NotificationComment(navController = navController, notification = notification)
                        "follow" -> NotificationFollow(navController = navController, notification = notification)
                        "message" -> NotificationMessage(navController = navController, notification = notification)
                    }
                }
            }
    }
}

@Composable
fun NotificationMessage(navController: NavController, notification: NotificationItem) {
    val notificationData = notification.data

    Box (
        modifier = Modifier.clickable { /* TODO */ }
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column (
                verticalArrangement = Arrangement.Center
            ) {
                AsyncImage(
                    model = notificationData.message!!.from!!.avatar,
                    contentDescription = stringResource(
                        id = R.string.profile_picture
                    ),
                    modifier = Modifier.clip(CircleShape)
                )
            }
            Column (
                modifier = Modifier.weight(1F)
            ) {
                Text(
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(notificationData.message!!.from!!.username)
                        }

                        append(" ${stringResource(id = R.string.posted_new_announcement)}")
                    }
                )

                Text(notificationData.message!!.body!!)

                NotificationDateText(date = notification.createDate!!)
            }
        }
    }
}

@Composable
fun NotificationUpload(navController: NavController, notification: NotificationItem) {
    val notificationData = notification.data

    Box (
        modifier = Modifier.clickable { navController.navigateOnce(PartScreen(partId = notificationData.story!!.part!!.id!!, storyId = notificationData.story.id!!)) }
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column (
                verticalArrangement = Arrangement.Center
            ) {
                AsyncImage(
                    model = notificationData.story!!.user!!.avatar,
                    contentDescription = stringResource(
                        id = R.string.profile_picture
                    ),
                    modifier = Modifier.clip(CircleShape)
                )
            }
            Column (
                modifier = Modifier.weight(1F)
            ) {
                Text(
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(notificationData.story!!.user!!.username)
                        }

                        append(" ${stringResource(id = R.string.updated_story)} ")

                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(notificationData.story!!.title!!)
                            append(" - ")
                            append(notificationData.story.part!!.title!!)
                        }
                    }
                )
                NotificationDateText(date = notification.createDate!!)
            }
            Column {
                AsyncImage(
                    model = notificationData.story!!.cover,
                    contentDescription = stringResource(
                        id = R.string.cover
                    ),
                    modifier = Modifier.height(50.dp)
                )
            }
        }
    }

}

@Composable
fun NotificationComment(navController: NavController, notification: NotificationItem) {
    val notificationData = notification.data

    Box (
        modifier = Modifier.clickable { navController.navigateOnce(PartScreen(partId = notificationData.story!!.part!!.id!!, storyId = notificationData.story.id!!)) }
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column (
                verticalArrangement = Arrangement.Center
            ) {
                AsyncImage(
                    model = notificationData.comment!!.user!!.avatar,
                    contentDescription = stringResource(
                        id = R.string.profile_picture
                    ),
                    modifier = Modifier.clip(CircleShape)
                )
            }
            Column (
                modifier = Modifier.weight(1F)
            ) {
                Text(
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(notificationData.comment!!.user!!.username)
                        }

                        append(" ${stringResource(id = when(notificationData.comment!!.parentId == null) {true -> R.string.commented_on false -> R.string.replied_to_comment_on})} ")

                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(notificationData.story!!.title!!)
                            append(" - ")
                            append(notificationData.story.part!!.title!!)
                        }
                    }
                )
                Text(notificationData.comment!!.body!!)
                NotificationDateText(date = notification.createDate!!)
            }
        }
    }
}

@Composable
fun NotificationFollow(navController: NavController, notification: NotificationItem) {
    val notificationData = notification.data

    Box (
        modifier = Modifier.clickable { navController.navigateOnce(ProfileScreen(username = notificationData.follower!!.username)) }
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column (
                verticalArrangement = Arrangement.Center
            ) {
                AsyncImage(
                    model = notificationData.follower!!.avatar,
                    contentDescription = stringResource(
                        id = R.string.profile_picture
                    ),
                    modifier = Modifier.clip(CircleShape)
                )
            }
            Column (
                modifier = Modifier.weight(1F)
            ) {
                Text(
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(notificationData.follower!!.username)
                        }

                        append(" ${stringResource(id = R.string.just_followed_you)}")
                    }
                )
                NotificationDateText(date = notification.createDate!!)
            }
        }
    }
}

@Composable
fun NotificationDateText(date: Date) {
    Text(
        DateFormat.getDateFormat(LocalContext.current).format(date),
        fontWeight = FontWeight.Thin
    )
}

@Serializable
object NotificationScreen