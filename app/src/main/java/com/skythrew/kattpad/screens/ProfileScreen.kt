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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.skythrew.kattpad.R
import com.skythrew.kattpad.api.Story
import com.skythrew.kattpad.api.User
import com.skythrew.kattpad.api.Wattpad
import com.skythrew.kattpad.screens.utils.navigateOnce
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlin.math.ceil

@Composable
fun ProfileScreen(padding: PaddingValues, navController: NavController, client: Wattpad, username: String) {
    val coroutineScope = rememberCoroutineScope()

    var isLoading by remember {
        mutableStateOf(true)
    }

    var user: User? by remember {
        mutableStateOf(null)
    }

    var userFollowed: Boolean? by remember {
        mutableStateOf(null)
    }

    LaunchedEffect(key1 = username) {
        isLoading = true
        user = client.fetchUser(username, setOf("username", "avatar", "numFollowers", "createDate", "numStoriesPublished", "following"))
        userFollowed = user!!.data.following
        isLoading = false
    }

    if (isLoading) {
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
        }
    } else {
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1F),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AsyncImage(
                        model = user!!.data.avatar,
                        contentDescription = stringResource(id = R.string.profile_picture),
                        modifier = Modifier.clip(CircleShape)
                    )
                    Text(
                        user?.data?.username!!,
                        fontSize = MaterialTheme.typography.labelLarge.fontSize,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    "${stringResource(id = R.string.joined_on)} ${
                        DateFormat.getDateFormat(
                            LocalContext.current
                        ).format(user?.data?.createDate!!)
                    }",
                    fontWeight = FontWeight.Thin
                )

                if (client.loggedIn)
                    Button(onClick = {
                        coroutineScope.launch {
                            val success: Boolean? = if (userFollowed!!)
                                user!!.unfollow()
                            else
                                user!!.follow()

                            if (success == true)
                                userFollowed = !userFollowed!!
                        }

                    }) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                when (userFollowed!!) {
                                    true -> Icons.Default.CheckCircle
                                    false -> Icons.Default.AddCircle
                                                              },
                                contentDescription = null
                            )
                            Text(
                                when (userFollowed!!) {
                                    true -> stringResource(id = R.string.unfollow)
                                    false -> stringResource(id = R.string.follow)
                                }
                            )
                        }
                    }
            }
            HorizontalDivider()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .weight(4F),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                StoriesRow(navController = navController, user = user!!)
                FollowersRow(navController = navController, user = user!!)
                FollowingRow(navController = navController, user = user!!)
            }
        }
    }
}

@Composable
fun StoriesRow(navController: NavController, user: User) {
    val pagerState = rememberPagerState(pageCount = { ceil(user.data.numStoriesPublished!!.toDouble() / 3).toInt() })

    var cache: Map<Int, List<Story>> by remember {
        mutableStateOf(mapOf())
    }

    Text(stringResource(id = R.string.stories), fontSize = MaterialTheme.typography.headlineSmall.fontSize, fontWeight = FontWeight.Bold)

    HorizontalPager(state = pagerState) {page ->
        var stories: List<Story> by remember {
            mutableStateOf(listOf())
        }

        var isLoading by remember {
            mutableStateOf(false)
        }

        LaunchedEffect(key1 = page) {
            if (page !in cache) {
                isLoading = true
                stories = user.fetchStories(setOf("title", "cover"), offset = page * 3, limit = 3)
                cache = cache.plus(mapOf(page to stories))
                isLoading = false
            } else {
                stories = cache[page]!!
            }
        }

        if (isLoading) {
            Row (
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
            }
        }
        else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                for (story in stories) {
                    Column (
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1F)
                    ) {
                        AsyncImage(
                            model = story.data.cover,
                            contentDescription = stringResource(id = R.string.cover),
                            modifier = Modifier.width(80.dp).height(125.dp).clickable { navController.navigateOnce(StoryScreen(story.data.id)) }
                        )
                        Text(
                            story.data.title!!,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = MaterialTheme.typography.labelSmall.fontSize,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                    }
                }
                for (i in 1..(3 - stories.count())) {
                    Box (modifier = Modifier.weight(1F))
                }
            }
        }
    }
}

@Composable
fun FollowersRow(navController: NavController, user: User) {
    val lazyRowState = rememberLazyListState()

    var offset by remember {
        mutableIntStateOf(0)
    }

    var followers: List<User> by remember {
        mutableStateOf(listOf())
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(key1 = lazyRowState.canScrollForward) {
        if (!lazyRowState.canScrollForward &&
            lazyRowState.canScrollBackward &&
            followers.count() < user.data.numFollowers!!
        )
            offset += 10
    }

    LaunchedEffect(key1 = offset) {
        isLoading = true
        followers = followers.plus(user.fetchFollowers(setOf("username", "avatar"), offset=offset))
        isLoading = false
    }

    Text(stringResource(id = R.string.followers), fontSize = MaterialTheme.typography.headlineSmall.fontSize, fontWeight = FontWeight.Bold)

    LazyRow (
        state = lazyRowState,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    )
    {
        items(followers) {
            Column (
                verticalArrangement = Arrangement.spacedBy(5.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                    navController.navigateOnce(ProfileScreen(it.data.username))
                }
            )
            {
                AsyncImage(
                    model = it.data.avatar,
                    contentDescription = stringResource(id = R.string.profile_picture),
                    modifier = Modifier.clip(CircleShape)
                )
                Text(
                    it.data.username,
                    fontSize = MaterialTheme.typography.labelSmall.fontSize
                )
            }
        }
    }
}

@Composable
fun FollowingRow(navController: NavController, user: User) {
    val lazyRowState = rememberLazyListState()

    var offset by remember {
        mutableIntStateOf(0)
    }

    var following: List<User> by remember {
        mutableStateOf(listOf())
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(key1 = lazyRowState.canScrollForward) {
        if (!lazyRowState.canScrollForward &&
            lazyRowState.canScrollBackward &&
            following.count() < user.data.numFollowers!!
        )
            offset += 10
    }

    LaunchedEffect(key1 = offset) {
        isLoading = true
        following = following.plus(user.fetchFollowing(setOf("username", "avatar"), offset=offset))
        isLoading = false
    }

    Text(stringResource(id = R.string.following), fontSize = MaterialTheme.typography.headlineSmall.fontSize, fontWeight = FontWeight.Bold)

    LazyRow (
        state = lazyRowState,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    )
    {
        items(following) {
            Column (
                verticalArrangement = Arrangement.spacedBy(5.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                    navController.navigateOnce(ProfileScreen(it.data.username))
                }
            )
            {
                AsyncImage(
                    model = it.data.avatar,
                    contentDescription = stringResource(id = R.string.profile_picture),
                    modifier = Modifier.clip(CircleShape)
                )
                Text(
                    it.data.username,
                    fontSize = MaterialTheme.typography.labelSmall.fontSize
                )
            }
        }
    }
}

@Serializable
data class ProfileScreen (
    val username: String
)