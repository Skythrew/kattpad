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
import androidx.compose.foundation.layout.width
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
import coil.request.ImageRequest
import com.skythrew.kattpad.R
import com.skythrew.kattpad.api.Wattpad
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
                    val notificationData = notification.data
                    val dateFooter = @Composable { NotificationDateText(date = notification.createDate!!) }

                    when(notification.type) {
                        "upload" -> {
                            NotificationRow(
                                left = {
                                    ProfilePicture(url = notificationData.story!!.user!!.avatar!!)
                                },
                                content = {
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
                                },
                                right = {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(notificationData.story!!.cover)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = stringResource(
                                            id = R.string.cover
                                        ),
                                        modifier = Modifier.height(50.dp)
                                    )
                                },
                                footer = dateFooter,
                                onClick = {
                                    navController.navigateOnce(
                                        PartScreen(
                                            partId = notificationData.story!!.part!!.id!!,
                                            storyId = notificationData.story.id!!
                                        )
                                    )
                                }
                            )
                        }
                        "comment" -> {
                            NotificationRow(
                                left = {
                                    ProfilePicture(url = notificationData.comment!!.user!!.avatar!!)
                                },
                                content = {
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
                                },
                                footer = dateFooter,
                                onClick = {
                                    navController.navigateOnce(
                                        PartScreen(
                                            partId = notificationData.story!!.part!!.id!!,
                                            storyId = notificationData.story.id!!
                                        )
                                    )
                                }
                            )
                        }
                        "follow" -> {
                            NotificationRow(
                                left = {
                                    ProfilePicture(url = notificationData.follower!!.avatar!!)
                                },
                                content = {
                                    Text(
                                        buildAnnotatedString {
                                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                append(notificationData.follower!!.username)
                                            }

                                            append(" ${stringResource(id = R.string.just_followed_you)}")
                                        }
                                    )
                                },
                                footer = dateFooter,
                                onClick = {
                                    navController.navigateOnce(
                                        ProfileScreen(
                                            username = notificationData.follower!!.username
                                        )
                                    )
                                }
                            )
                        }
                        "message" -> {
                            NotificationRow(
                                left = {
                                    ProfilePicture(url = notificationData.message!!.from!!.avatar!!)
                                },
                                content = {
                                    Text(
                                        buildAnnotatedString {
                                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                append(notificationData.message!!.from!!.username)
                                            }

                                            append(" ${stringResource(id = R.string.posted_new_announcement)}")
                                        }
                                    )

                                    Text(notificationData.message!!.body!!)
                                },
                                footer = dateFooter,
                                onClick = { /* TODO */ }
                            )
                        }
                    }
                }
            }
    }
}

@Composable
fun ProfilePicture(url: String) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .crossfade(true)
            .build(),
        contentDescription = stringResource(
            id = R.string.profile_picture
        ),
        modifier = Modifier
            .clip(CircleShape)
            .width(50.dp)
            .height(50.dp)
    )
}

@Composable
fun NotificationRow(
    left: @Composable () -> Unit = {},
    right: @Composable () -> Unit = {},
    content: @Composable () -> Unit = {},
    footer: @Composable () -> Unit = {},
    onClick: () -> Unit = {}
) {
    Box (
        modifier = Modifier.clickable { onClick() }
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
                left()
            }
            Column (
                modifier = Modifier.weight(1F)
            ) {
                content()
                footer()
            }
            Column (
                verticalArrangement = Arrangement.Center
            ) {
                right()
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