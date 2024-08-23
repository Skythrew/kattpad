package com.skythrew.kattpad.screens

import androidx.annotation.Keep
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.skythrew.kattpad.R
import com.skythrew.kattpad.api.Library
import com.skythrew.kattpad.api.Wattpad
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlin.reflect.KSuspendFunction2

@Composable
fun HomeScreen(padding: PaddingValues, navController: NavController, client: Wattpad, isLogging: Boolean, isLogged: Boolean) {
        Column (
            modifier = Modifier
                .padding(bottom = padding.calculateBottomPadding()),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DiscoverySearchBar(navController)

            if (!isLogged) {
                Column (
                    modifier = Modifier
                        .weight(1F)
                        .padding(50.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (!isLogging) {
                        Text(
                            stringResource(id = R.string.login_text),
                            fontWeight = FontWeight.Thin,
                            textAlign = TextAlign.Center
                        )
                        TextButton(onClick = { navController.navigate(LoginScreen) }) {
                            Text(stringResource(id = R.string.login))
                        }
                    } else {
                        CircularProgressIndicator()
                    }
                }
            } else {
                Column (modifier = Modifier.padding(horizontal = 10.dp)) {
                    Library(navController = navController, client = client)
                }
            }
        }
}

@Composable
fun Library(navController: NavController, client: Wattpad) {
    var isLoading by remember {
        mutableStateOf(true)
    }

    var library: Library? by remember {
        mutableStateOf(null)
    }

    LaunchedEffect(Unit) {
        isLoading = true
        library = client.fetchLibrary()
        isLoading = false
    }

    Column (
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            stringResource(R.string.your_stories),
            fontSize = MaterialTheme.typography.headlineSmall.fontSize
        )
        if (isLoading)
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
            }
        else
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 80.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(library!!.data.stories) {story ->
                    val currentPartId = story.readingPosition!!.partId
                    val currentPartNumber = story.parts!!.indexOf(story.parts.find {data -> data.id == currentPartId}) + 1
                    val chaptersLeft = story.numParts!! - currentPartNumber

                    Column (
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage (
                            model = story.cover,
                            contentDescription = stringResource(R.string.cover),
                            modifier = Modifier
                                .width(80.dp)
                                .clickable {
                                    navController.navigate(
                                        PartScreen(partId = currentPartId, storyId = story.id)
                                    )
                                }
                        )

                        LinearProgressIndicator(
                            progress = {currentPartNumber.toFloat() / story.numParts},
                            modifier = Modifier
                                .width(75.dp)
                                .clip(CircleShape)
                        )

                        Text(
                            when (chaptersLeft) {
                                0 -> stringResource(id = R.string.Finished)
                                1 -> "1 ${stringResource(id = R.string.chapter_left)}"
                                else -> "$chaptersLeft ${stringResource(id = R.string.chapters_left)}"
                            },
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverySearchBar(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()

    var activeSearch by rememberSaveable {
        mutableStateOf(false)
    }

    var searchText by rememberSaveable {
        mutableStateOf("")
    }

    var query by rememberSaveable {
        mutableStateOf("")
    }

    val client = Wattpad()

    val tabs = listOf(
        SearchTab(stringResource(id = R.string.stories), client::searchStory) { data, offset ->
            val listState = remember {
                LazyListState()
            }

            LaunchedEffect(key1 = listState.canScrollForward) {
                if (!listState.canScrollForward) {
                    offset.intValue += 10
                }
            }

            if (data.isNotEmpty()) {
                LazyColumn(state = listState) {
                    items(data) {
                        Card(
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxWidth()
                                .clickable {
                                    navController.navigate(StoryScreen(it.data.id))
                                }
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                AsyncImage(model = it.data.cover, contentDescription = stringResource(
                                    id = R.string.cover
                                ))
                                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                    Text(
                                        text = it.data.title!!,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                                        overflow = TextOverflow.Ellipsis,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = it.data.description!!,
                                        overflow = TextOverflow.Ellipsis,
                                        minLines = 3,
                                        maxLines = 3
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        SearchTab(stringResource(id = R.string.profiles), client::searchUser) { data, _ ->
            LazyColumn (modifier = Modifier.fillMaxSize()){
                items(data) {
                    Box (modifier = Modifier.clickable {
                        navController.navigate(
                            ProfileScreen(it.data.username)
                        )
                    }) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                        ) {
                            AsyncImage(
                                model = it.data.avatar,
                                contentDescription = "${it.data.username} avatar",
                                modifier = Modifier.clip(
                                    CircleShape
                                )
                            )
                            Text(it.data.username)
                        }
                    }
                }
            }
        }
    )

    val pagerState = rememberPagerState(pageCount = {tabs.count()})

    LaunchedEffect(key1 = searchText) {
        if (searchText.isNotBlank()) {
            if (searchText != query) {
                delay(1000)
                query = searchText
            }
        } else {
            query = ""
        }
    }

    Row (
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        SearchBar(
            query = searchText,
            onQueryChange = {
                searchText = it
            },
            onSearch = {activeSearch = false},
            active = activeSearch,
            onActiveChange = {activeSearch = it},
            placeholder = {
                Text(
                    stringResource(id = R.string.discovery_placeholder)
                )
            },
            trailingIcon = { Icon(
                Icons.Default.Search,
                contentDescription = null
            )}
        ) {
            SecondaryTabRow(selectedTabIndex = pagerState.currentPage) {
                for (i in 0..<pagerState.pageCount) {
                    Tab(selected = pagerState.currentPage == i, onClick = { coroutineScope.launch { pagerState.animateScrollToPage(i) }}, text = {Text(tabs[i].title)})
                }
            }

            HorizontalPager(state = pagerState) {
                tabs[it].ShowContent(query = query)
            }
        }
    }
}

data class SearchTab<T>(
    val title: String,
    val request: KSuspendFunction2<String, Int, List<T>>,
    val content: @Composable (data: List<T>, offset: MutableIntState) -> Unit
) {
    @Composable
    fun ShowContent(query: String) {
        val offset = remember {
            mutableIntStateOf(0)
        }

        var searchResult: List<T> by remember {
            mutableStateOf(listOf())
        }

        var isLoading by remember {
            mutableStateOf(false)
        }

        LaunchedEffect(key1 = query) {
            if (query.isNotBlank()) {
                isLoading = true
                offset.intValue = 0
                searchResult = request(query, 0)
                isLoading = false
            } else {
                searchResult = listOf()
            }
        }

        LaunchedEffect(key1 = offset.intValue) {
            if (offset.intValue > 0)
                searchResult = searchResult.plus(request(query, offset.intValue))
        }

        if (!isLoading) {
            Column (
                modifier = Modifier.fillMaxSize()
            ) {
                if (searchResult.isEmpty()) {
                    Column (
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            stringResource(id = R.string.no_result),
                            fontWeight = FontWeight.Thin
                        )
                    }
                }
                else {
                    content(searchResult, offset)
                }
            }
        }
        else {
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(5.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Keep
@Serializable
object HomeScreen