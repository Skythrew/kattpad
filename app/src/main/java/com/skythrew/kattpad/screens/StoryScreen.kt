package com.skythrew.kattpad.screens

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.skythrew.kattpad.R
import com.skythrew.kattpad.api.Story
import com.skythrew.kattpad.api.Wattpad
import com.skythrew.kattpad.screens.utils.NumberInfo
import kotlinx.serialization.Serializable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryScreen(padding: PaddingValues, navController: NavController, client: Wattpad, storyId: Int) {
        var isLoading by remember {
            mutableStateOf(true)
        }

        var searchResult: Story? by remember {
            mutableStateOf(null)
        }

        var showBottomSheet by remember {
            mutableStateOf(false)
        }

        LaunchedEffect (key1 = storyId) {
            isLoading = true
            searchResult = client.fetchStory(
                storyId,
                setOf(
                    "title",
                    "description",
                    "cover",
                    "numParts",
                    "readCount",
                    "voteCount",
                    "commentCount",
                    "createDate",
                    "modifyDate",
                    "parts(id,title)"
                )
            )
            isLoading = false
        }

        Box (modifier = Modifier.padding(padding)){
            if (isLoading) {
                Column (modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                }
            }
            else
                Column (horizontalAlignment = Alignment.CenterHorizontally) {
                    Column (verticalArrangement = Arrangement.spacedBy(15.dp), horizontalAlignment = Alignment.CenterHorizontally){
                        AsyncImage(model = searchResult?.data?.cover, contentDescription = stringResource(
                            id = R.string.cover
                        ))
                        Text(text = searchResult?.data?.title!!, fontSize = MaterialTheme.typography.headlineMedium.fontSize)
                        Row (verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.clickable {
                            navController.navigate(ProfileScreen(searchResult!!.data.user.username))
                        }) {
                            AsyncImage(model = searchResult?.data?.user?.avatar, modifier = Modifier.clip(CircleShape), contentDescription = stringResource(
                                id = R.string.profile_picture
                            ))
                            Text(searchResult?.data?.user?.username ?: "", fontWeight = FontWeight.Bold)
                        }
                        Row (horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                            Button(onClick = { /*TODO*/ }) {
                                Row (verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Icon(
                                        ImageVector.vectorResource(R.drawable.outline_menu_book_24),
                                        contentDescription = ""
                                    )
                                    Text(stringResource(id = R.string.read))
                                }
                            }
                            Button(shape = CircleShape, onClick = { showBottomSheet = true }) {
                                Row (verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)){
                                    Icon(Icons.AutoMirrored.Filled.List, contentDescription = "")
                                    Text("${searchResult?.data?.numParts} ${stringResource(id = R.string.chapters)}")
                                }

                                if (showBottomSheet)
                                    ModalBottomSheet(onDismissRequest = { showBottomSheet = false }) {
                                        LazyColumn {
                                            items(searchResult?.data?.parts!!) {part ->
                                                Box (modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        navController.navigate(
                                                            PartScreen(
                                                                partId = part.id!!,
                                                                storyId = storyId
                                                            )
                                                        )
                                                    }
                                                ) {
                                                    Text(part.title!!, modifier = Modifier.padding(10.dp), fontSize = MaterialTheme.typography.bodyMedium.fontSize, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                            }
                        }
                        Row (horizontalArrangement = Arrangement.spacedBy(10.dp)){
                            NumberInfo(icon = ImageVector.vectorResource(R.drawable.visibility), number = searchResult?.data?.readCount!!)
                            NumberInfo(icon = ImageVector.vectorResource(R.drawable.outline_comment_24), number = searchResult?.data?.commentCount!!)
                            NumberInfo(icon = ImageVector.vectorResource(R.drawable.outline_favorite_24), number = searchResult?.data?.voteCount!!)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider()
                    Column(modifier = Modifier.verticalScroll(ScrollState(0))) {
                        Text(searchResult?.data?.description!!, modifier = Modifier.padding(20.dp))
                    }
                }
        }
    }

@Serializable
data class StoryScreen(
    val storyId: Int
)