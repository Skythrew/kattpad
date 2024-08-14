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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.skythrew.kattpad.api.Wattpad
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlin.reflect.KSuspendFunction2

@Composable
fun DiscoveryScreen(padding: PaddingValues, navController: NavController) {

        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(bottom = padding.calculateBottomPadding())
                .fillMaxSize()
        ) {
            DiscoverySearchBar(navController)
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
        SearchTab("Histoires", client::searchStory) { data, offset ->
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
                                AsyncImage(model = it.data.cover, contentDescription = "Couverture")
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
        SearchTab("Profils", client::searchUser) { data, _ ->
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
                "Qu'allez-vous découvrir aujourd'hui ?"
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
                            "Aucun résultat",
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
object DiscoveryScreen