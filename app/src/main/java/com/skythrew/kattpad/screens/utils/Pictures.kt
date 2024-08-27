package com.skythrew.kattpad.screens.utils

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.skythrew.kattpad.R

@Composable
fun StoryPicture(
    url: String,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .crossfade(true)
            .build(),
        contentDescription = stringResource(id = R.string.cover),
        modifier = modifier
            .width(80.dp)
            .height(125.dp)
    )
}

@Composable
fun ProfilePicture(url: String, modifier: Modifier = Modifier) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .crossfade(true)
            .build(),
        contentDescription = stringResource(
            id = R.string.profile_picture
        ),
        modifier = modifier
            .clip(CircleShape)
            .width(50.dp)
            .height(50.dp)
    )
}
