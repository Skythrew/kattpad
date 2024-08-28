package com.skythrew.kattpad.screens

import android.text.format.DateFormat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import androidx.navigation.NavController
import com.skythrew.kattpad.R
import com.skythrew.kattpad.api.Story
import com.skythrew.kattpad.api.User
import com.skythrew.kattpad.api.Wattpad
import com.skythrew.kattpad.api.WattpadList
import com.skythrew.kattpad.api.requests.ReadingListStoriesResponse
import com.skythrew.kattpad.api.requests.StoryData
import com.skythrew.kattpad.screens.utils.ProfilePicture
import com.skythrew.kattpad.screens.utils.StoryElement
import com.skythrew.kattpad.screens.utils.navigateOnce
import com.skythrew.kattpad.screens.utils.popBackOnce
import com.skythrew.kattpad.screens.utils.reachedLast
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlin.math.ceil
import kotlin.reflect.KSuspendFunction3

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, client: Wattpad, username: String) {
    val coroutineScope = rememberCoroutineScope()

    val scrollState = rememberScrollState()

    var isLoading by remember {
        mutableStateOf(true)
    }

    var user: User? by remember {
        mutableStateOf(null)
    }

    var userFollowed: Boolean? by remember {
        mutableStateOf(null)
    }

    var showInfoModal: Boolean by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = username) {
        isLoading = true
        user = client.fetchUser(username, setOf("username", "name", "avatar", "location", "numFollowers", "createDate", "numStoriesPublished", "numLists", "numFollowing", "following", "description", "location"))
        userFollowed = user!!.data.following
        isLoading = false
    }

    Scaffold (
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackOnce() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.back))
                    }
                },
                title = {
                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ProfilePicture(
                            url = when(isLoading) {true -> "" false -> user!!.data.avatar!!}
                        )

                        Text(
                            username,
                            fontSize = MaterialTheme.typography.labelLarge.fontSize,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    if (client.loggedIn && client.username != username)
                        IconButton(onClick = {
                            coroutineScope.launch {
                                val success: Boolean? = if (userFollowed!!)
                                    user!!.unfollow()
                                else
                                    user!!.follow()

                                if (success == true)
                                    userFollowed = !userFollowed!!
                            }
                        }) {
                            Icon(when(userFollowed) {true -> ImageVector.vectorResource(id = R.drawable.baseline_notifications_active_24) else -> Icons.Outlined.Notifications}, contentDescription = "")
                        }

                    IconButton(onClick = { showInfoModal = true }) {
                        Icon(Icons.Outlined.Info, contentDescription = stringResource(id = R.string.infos))
                    }
                }
            )
        }
    ) {padding ->
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
                    .padding(padding)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    if (user!!.data.numStoriesPublished!! > 0)
                        StoriesRow(navController = navController, user = user!!)

                    if (user!!.data.numFollowers!! > 0)
                        UsersRow(
                            navController = navController,
                            request = user!!::fetchFollowers,
                            title = stringResource(id = R.string.followers),
                            maxCount = user!!.data.numFollowers!!
                        )

                    if (user!!.data.numFollowing!! > 0)
                        UsersRow(
                            navController = navController,
                            request = user!!::fetchFollowing,
                            title = stringResource(id = R.string.following),
                            maxCount = user!!.data.numFollowing!!
                        )

                    ReadingListsSection(navController = navController, user = user!!)
                }
            }
        }
    }

    if (showInfoModal)
        ModalBottomSheet(onDismissRequest = { showInfoModal = false }) {
            if (!isLoading)
                Column (
                    modifier = Modifier
                        .padding(10.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(15.dp)
                ) {

                    InfoModalSection(
                        condition = user!!.data.description!!.isNotBlank(),
                        divider = true,
                        icon = {Icon(ImageVector.vectorResource(id = R.drawable.baseline_description_24), contentDescription = stringResource(id = R.string.description))},
                        label = stringResource(id = R.string.description)
                    ) {
                        Text(HtmlCompat.fromHtml(user!!.data.description!!, HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_HEADING).toString())
                    }

                    InfoModalSection(
                        condition = user!!.data.location!!.isNotBlank(),
                        divider = true,
                        icon = {Icon(Icons.Filled.LocationOn, contentDescription = stringResource(id = R.string.location))},
                        label = stringResource(id = R.string.location)
                    ) {
                        Text(user!!.data.location!!)
                    }

                    InfoModalSection(
                        divider = false,
                        icon = {Icon(Icons.Filled.CheckCircle, contentDescription = stringResource(id = R.string.registration))},
                        label = stringResource(id = R.string.registration)
                    ) {
                        Text("${stringResource(id = R.string.joined_on)} ${DateFormat.getDateFormat(LocalContext.current).format(user!!.data.createDate!!)}")
                    }
                }
        }
}

@Composable
fun InfoModalSection(condition: Boolean = true, divider: Boolean, icon: @Composable () -> Unit = {}, label: String, content: @Composable () -> Unit = {}) {
    if (condition) {
        Column (
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row (
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                icon()
                Text(
                    label,
                    fontWeight = FontWeight.Bold
                )
            }

            content()
        }

        if (divider)
            HorizontalDivider()
    }
}

@Composable
fun StoriesRow(navController: NavController, user: User) {
    val pagerState = rememberPagerState(pageCount = { ceil(user.data.numStoriesPublished!!.toDouble() / 3).toInt() })

    var cache: Map<Int, List<Story>> by remember {
        mutableStateOf(mapOf())
    }

    Text(
        stringResource(id = R.string.stories),
        fontSize = MaterialTheme.typography.headlineSmall.fontSize,
        modifier = Modifier.padding(horizontal = 10.dp)
    )

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
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (story in stories) {
                    StoryElement(
                        coverUrl = story.data.cover!!,
                        text = story.data.title!!,
                        onClick = { navController.navigateOnce(StoryScreen(story.data.id!!)) }
                    )
                }
            }
        }
    }
}

@Composable
fun ReadingListsSection(navController: NavController, user: User) {
    var isLoading by remember {
        mutableStateOf(true)
    }

    var readingLists: List<WattpadList> by remember {
        mutableStateOf(listOf())
    }

    LaunchedEffect(key1 = Unit) {
        isLoading = true
        readingLists = user.fetchLists(fields = setOf("name"))
        isLoading = false
    }

    if (!isLoading) {
        Column (
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            readingLists.forEach { readingList ->
                ReadingListElement(
                    list = readingList,
                    onStoryClick = { storyId ->
                        navController.navigateOnce(
                            StoryScreen(storyId = storyId)
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun ReadingListElement(
    list: WattpadList,
    onStoryClick: (storyId: Int) -> Unit = {}
) {
    val lazyRowState = rememberLazyListState()

    val reachedLast: Boolean by remember {
        derivedStateOf {
            lazyRowState.reachedLast(5)
        }
    }

    var storiesResponse: ReadingListStoriesResponse? by remember {
        mutableStateOf(null)
    }

    var stories: List<StoryData> by remember {
        mutableStateOf(listOf())
    }

    var offset by remember {
        mutableIntStateOf(0)
    }

    LaunchedEffect(key1 = offset) {
        storiesResponse = list.fetchStories(setOf("total", "stories(id,title,cover)"), offset = offset)
        stories = stories.plus(storiesResponse!!.stories)
    }

    LaunchedEffect(key1 = reachedLast) {
        if (reachedLast && storiesResponse != null)
            if (stories.isNotEmpty() && offset < storiesResponse!!.total!! - 1)
                offset += 10
    }

    if (storiesResponse != null) {
        if (stories.isNotEmpty())
            Column {
                Text(
                    list.data.name!!,
                    fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )

                Text(
                    stringResource(id = R.string.reading_list),
                    fontSize = MaterialTheme.typography.labelSmall.fontSize,
                    fontWeight = FontWeight.Thin,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp),
                    state = lazyRowState
                ) {
                    items(stories) { story ->
                        StoryElement(
                            coverUrl = story.cover!!,
                            text = story.title!!,
                            onClick = { onStoryClick(story.id!!) }
                        )
                    }
                }
            }
    }
    else
        Row (
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            CircularProgressIndicator()
        }
}

@Composable
fun UsersRow(
    navController: NavController,
    request: KSuspendFunction3<Set<String>, Int, Int, List<User>>,
    title: String,
    maxCount: Int
) {
    val lazyRowState = rememberLazyListState()

    var offset by remember {
        mutableIntStateOf(0)
    }

    var fetchedUsers: List<User> by remember {
        mutableStateOf(listOf())
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(key1 = lazyRowState.canScrollForward) {
        if (!lazyRowState.canScrollForward &&
            lazyRowState.canScrollBackward &&
            fetchedUsers.count() < maxCount
        )
            offset += 10
    }

    LaunchedEffect(key1 = offset) {
        isLoading = true
        fetchedUsers = fetchedUsers.plus(request(setOf("username", "avatar"), 0, offset))
        isLoading = false
    }

    Text(
        title,
        fontSize = MaterialTheme.typography.headlineSmall.fontSize,
        modifier = Modifier.padding(horizontal = 10.dp)
    )

    LazyRow (
        state = lazyRowState,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 10.dp)
    )
    {
        items(fetchedUsers) {
            Column (
                verticalArrangement = Arrangement.spacedBy(5.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                    navController.navigateOnce(ProfileScreen(it.data.username))
                }
            )
            {
                ProfilePicture(url = it.data.avatar!!)
                Text(
                    it.data.username,
                    fontSize = MaterialTheme.typography.labelMedium.fontSize
                )
            }
        }
    }
}

@Serializable
data class ProfileScreen (
    val username: String
)