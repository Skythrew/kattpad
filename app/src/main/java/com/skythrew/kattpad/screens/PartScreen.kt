package com.skythrew.kattpad.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import androidx.navigation.NavController
import com.skythrew.kattpad.R
import com.skythrew.kattpad.api.Part
import com.skythrew.kattpad.api.Wattpad
import com.skythrew.kattpad.screens.utils.getFormattedNumber
import kotlinx.serialization.Serializable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartScreen(navController: NavController, client: Wattpad, storyId: Int, id: Int) {
    BackHandler {
        navController.popBackStack(StoryScreen(storyId), inclusive = false)
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var part: Part? by remember {
        mutableStateOf(null)
    }

    var partText: String? by remember {
        mutableStateOf(null)
    }

    var prevPartId: Int? by remember {
        mutableStateOf(null)
    }

    var nextPartId: Int? by remember {
        mutableStateOf(null)
    }

    LaunchedEffect(key1 = id) {
        isLoading = true

        part = client.fetchStoryPart(id, setOf("title", "group", "readCount", "voteCount", "commentCount"))
        partText = part!!.fetchText()

        part!!.data.story!!.parts!!.forEachIndexed { index, storyPart ->
            if (storyPart.id == id) {
                if (index > 0)
                    prevPartId = part!!.data.story!!.parts!![index - 1].id

                if (index + 1 < part!!.data.story!!.numParts!!)
                    nextPartId = part!!.data.story!!.parts!![index + 1].id
            }
        }

        isLoading = false
    }

    if (isLoading) {
        Column (modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
        }
    }
    else {
        val scrollBehaviour = BottomAppBarDefaults.exitAlwaysScrollBehavior()

        Scaffold (
            bottomBar = {
                BottomAppBar (
                    actions = {
                        Row (
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            NumberIconButton(icon = ImageVector.vectorResource(id = R.drawable.visibility), number = part?.data?.readCount)
                            NumberIconButton(icon = ImageVector.vectorResource(R.drawable.outline_favorite_24), number = part?.data?.voteCount)
                            NumberIconButton(icon = ImageVector.vectorResource(R.drawable.outline_comment_24), number = part?.data?.commentCount)
                        }
                    },
                    scrollBehavior = scrollBehaviour
                )
            }
        ) {padding ->
            Column (modifier = Modifier.padding(padding)) {
                Row (verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    IconButton(enabled = prevPartId != null, onClick = {
                        navController.navigate(PartScreen(partId = prevPartId!!, storyId = storyId))
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.prev_chapter)
                        )
                    }

                    Text(
                        text = part?.data?.title!!,
                        fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(enabled = nextPartId != null, onClick = {
                        navController.navigate(PartScreen(partId = nextPartId!!, storyId = storyId))
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = stringResource(id = R.string.next_chapter)
                        )
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(top = 10.dp))
                LazyColumn (
                    modifier = Modifier
                        .nestedScroll(scrollBehaviour.nestedScrollConnection)
                        .background(Color.White)
                        .fillMaxSize()
                ) {
                    item {
                        Text(
                            HtmlCompat.fromHtml(partText!!, HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_HEADING).toString(),
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NumberIconButton(icon: ImageVector, number: Int?, contentDescription: String = "", onClick: () -> Unit = {}) {
    val iconBtnColors = IconButtonDefaults.iconButtonColors()

    val colors = ButtonColors(
        containerColor = iconBtnColors.containerColor,
        contentColor = iconBtnColors.contentColor,
        disabledContentColor = iconBtnColors.disabledContentColor,
        disabledContainerColor = iconBtnColors.disabledContainerColor
    )

    TextButton(colors = colors, onClick = onClick) {
        Column (verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = contentDescription)
            Text(getFormattedNumber(number))
        }
    }
}

@Serializable
data class PartScreen (
    val partId: Int,
    val storyId: Int
)