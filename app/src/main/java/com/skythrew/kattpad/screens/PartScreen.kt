package com.skythrew.kattpad.screens

import android.text.format.DateFormat
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.skythrew.kattpad.R
import com.skythrew.kattpad.api.Comment
import com.skythrew.kattpad.api.Part
import com.skythrew.kattpad.api.Wattpad
import com.skythrew.kattpad.screens.utils.getFormattedNumber
import com.skythrew.kattpad.screens.utils.navigateOnce
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartScreen(navController: NavController, client: Wattpad, storyId: Int, id: Int) {
    BackHandler {
        if(!navController.popBackStack(StoryScreen(storyId), inclusive = false))
            navController.popBackStack()
    }

    val coroutineScope = rememberCoroutineScope()

    var isLoading by remember {
        mutableStateOf(true)
    }

    var part: Part? by remember {
        mutableStateOf(null)
    }

    var voted: Boolean? by remember {
        mutableStateOf(null)
    }

    var voteCount: Int? by remember {
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

    val showModal = remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = id) {
        isLoading = true

        part = client.fetchStoryPart(id, setOf("title", "group", "readCount", "voteCount", "commentCount", "voted"))
        partText = part!!.fetchText()

        part!!.data.story!!.parts!!.forEachIndexed { index, storyPart ->
            if (storyPart.id == id) {
                if (index > 0)
                    prevPartId = part!!.data.story!!.parts!![index - 1].id

                if (index + 1 < part!!.data.story!!.numParts!!)
                    nextPartId = part!!.data.story!!.parts!![index + 1].id
            }
        }

        voted = part!!.data.voted
        voteCount = part!!.data.voteCount

        if (client.loggedIn)
            coroutineScope.launch { part!!.syncReadingPosition() }

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
                            NumberIconButton(
                                icon = when (voted) {
                                    null, false -> ImageVector.vectorResource(R.drawable.outline_favorite_24)
                                    true -> ImageVector.vectorResource(R.drawable.baseline_favorite_24)
                                },
                                onClick = {
                                    if (voted != null)
                                        coroutineScope.launch {
                                            val req = part!!.toggleVote()
                                            if (req != null) {
                                                voted = part!!.data.voted
                                                voteCount = req.votes
                                            }
                                        }
                                },
                                number = voteCount
                            )
                            NumberIconButton(icon = ImageVector.vectorResource(R.drawable.outline_comment_24), number = part?.data?.commentCount, onClick = {
                                showModal.value = true
                            })
                        }
                    },
                    scrollBehavior = scrollBehaviour
                )
            }
        ) {padding ->
            Column (modifier = Modifier.padding(padding)) {
                Row (verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    IconButton(enabled = prevPartId != null, onClick = {
                        navController.popBackStack()
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
                        navController.popBackStack()
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
                            modifier = Modifier.padding(10.dp),
                            color = Color.Black
                        )
                    }
                }
            }

            if (showModal.value)
                CommentsModal(showModal = showModal, part = part!!, client = client, navController = navController)
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
            if (number != null)
                Text(getFormattedNumber(number))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CommentsModal(showModal: MutableState<Boolean>, part: Part, client: Wattpad, navController: NavController) {
    val modalState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()

    val focusManager = LocalFocusManager.current

    val lazyColState = rememberLazyListState()

    var commentText by remember {
        mutableStateOf("")
    }

    var commentSent by remember {
        mutableStateOf(true)
    }

    val commentsState: MutableState<List<Comment>> = remember {
        mutableStateOf(listOf())
    }

    val comments = commentsState.value

    var commentsLeft by remember {
        mutableStateOf(false)
    }

    var after: String? by remember {
        mutableStateOf(null)
    }

    var commentsLoading by remember {
        mutableStateOf(false)
    }

    ModalBottomSheet(sheetState = modalState, onDismissRequest = { showModal.value = false }) {
        LaunchedEffect(after) {
            if (after == null)
                commentsLoading = true

            val (commentsAfter, fetchedComments) = part.fetchComments(after = after)
            commentsLeft = commentsAfter
            commentsState.value = comments.plus(fetchedComments)
            commentsLoading = false
        }

        LaunchedEffect(lazyColState.canScrollForward) {
            if(comments.isNotEmpty() && !lazyColState.canScrollForward && commentsLeft) {
                after = comments.last().data.commentId.resourceId
            }
        }

        if (commentsLoading)
            Row (
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                CircularProgressIndicator()
            }
        else
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth(),
                state = lazyColState
            ) {
                stickyHeader {
                    TextField(
                        value = commentText,
                        enabled = client.loggedIn,
                        onValueChange = { commentText = it },
                        singleLine = false,
                        modifier = Modifier
                            .fillMaxWidth(),
                        label = {
                            Text(stringResource(id = R.string.write_comment))
                        },
                        keyboardActions = KeyboardActions(onSend = {}),
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        val sentComment = part.comment(commentText)

                                        if (sentComment != null) {
                                            commentSent = true
                                            commentsState.value = listOf(sentComment).plus(comments)
                                            commentText = ""
                                        } else {
                                            commentSent = false
                                        }
                                    }

                                    focusManager.clearFocus(force = true)
                                }
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
                            }
                        },
                        supportingText = {
                            if (!commentSent)
                                Text(stringResource(id = R.string.comment_error))
                        },
                        isError = !commentSent
                    )
                }
                items(comments) {
                    CommentRow(navController = navController, comment = it, client = client, commentsState = commentsState)
                }
            }
    }
}

@Composable
fun CommentRow(navController: NavController, comment: Comment, commentsState: MutableState<List<Comment>>, modifier: Modifier = Modifier, client: Wattpad) {
    val coroutineScope = rememberCoroutineScope()

    var showReplies by remember {
        mutableStateOf(false)
    }

    var repliesLoading by remember {
        mutableStateOf(true)
    }

    var replies: List<Comment> by remember {
        mutableStateOf(listOf())
    }

    val showDeleteDialogState = remember {
        mutableStateOf(false)
    }

    val deletionErrState = remember {
        mutableStateOf(false)
    }

    var commentLiked by remember {
        mutableStateOf(comment.data.sentiments.like?.interaction != null)
    }

    var likeCount by remember {
        mutableIntStateOf(comment.data.sentiments.like?.count ?: 0)
    }

    LaunchedEffect(key1 = showReplies) {
        if (showReplies && replies.isEmpty()) {
            repliesLoading = true
            replies = comment.fetchReplies()
            repliesLoading = false
        }
    }

    Row (
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Column {
            AsyncImage(
                model = comment.data.user.avatar,
                contentDescription = stringResource(id = R.string.profile_picture),
                modifier = Modifier
                    .clip(CircleShape)
                    .height(40.dp)
                    .clickable { navController.navigateOnce(ProfileScreen(comment.data.user.username)) }
            )
        }
        Column (modifier = Modifier.weight(1F)){
            Row  (modifier = Modifier.fillMaxWidth()){
                Column (modifier = Modifier.weight(1F)) {
                    Text(
                        comment.data.user.username,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        comment.data.text
                    )
                    Text(
                        DateFormat.getDateFormat(LocalContext.current).format(comment.data.created),
                        fontWeight = FontWeight.Thin
                    )

                    if (comment.data.replyCount > 0)
                        TextButton(
                            onClick = { showReplies = !showReplies }
                        ) {
                            Text("${when(showReplies) { true -> stringResource(id = R.string.hide) false -> stringResource(id = R.string.show)}} ${comment.data.replyCount} ${stringResource(id = R.string.replies)}")
                        }
                }
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    NumberIconButton(icon = ImageVector.vectorResource(id = when (commentLiked) {true -> R.drawable.baseline_favorite_24 false -> R.drawable.outline_favorite_24}), number = likeCount) {
                        if (client.loggedIn)
                            coroutineScope.launch {
                                if (commentLiked) {
                                    if(comment.unlike() == true) {
                                        commentLiked = false
                                        likeCount -= 1
                                    }
                                } else {
                                    if(comment.like() == true) {
                                        commentLiked = true
                                        likeCount += 1
                                    }
                                }
                            }
                    }
                    if (comment.data.user.username == client.username)
                        IconButton(onClick = {
                            showDeleteDialogState.value = true
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete_icon))
                        }
                }
            }
            if (deletionErrState.value)
                Text(stringResource(id = R.string.comment_delete_err), color = MaterialTheme.colorScheme.error)
            if (showReplies) {
                if (repliesLoading)
                    CircularProgressIndicator()
                else {
                    for (reply in replies) {
                        CommentRow(navController = navController, comment = reply, client = client, commentsState = commentsState)
                    }
                }
            }
        }
    }

    if (showDeleteDialogState.value)
        DeleteCommentDialog(showDialogState = showDeleteDialogState, commentToDel = comment, errState = deletionErrState, commentsState = commentsState, coroutineScope)
}

@Composable
fun DeleteCommentDialog(showDialogState: MutableState<Boolean>, commentToDel: Comment, errState: MutableState<Boolean>, commentsState: MutableState<List<Comment>>, coroutineScope: CoroutineScope) {
    AlertDialog(onDismissRequest = { showDialogState.value = false },
        confirmButton = {
            TextButton(onClick = {
                coroutineScope.launch {
                    showDialogState.value = false
                    val res = commentToDel.delete()

                    if (res != true) {
                        errState.value = true
                    }
                    else {
                        errState.value = false
                        commentsState.value = commentsState.value.minus(commentToDel)
                    }
                }
            }) {
                Text(stringResource(id = R.string.delete))
            }
        },
        text = {Text(stringResource(id = R.string.comment_deletion_confirm))}
    )
}

@Serializable
data class PartScreen (
    val partId: Int,
    val storyId: Int
)