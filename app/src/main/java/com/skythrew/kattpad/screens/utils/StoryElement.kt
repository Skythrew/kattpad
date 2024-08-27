package com.skythrew.kattpad.screens.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun StoryElement(
    coverUrl: String,
    text: String,
    modifier: Modifier = Modifier,
    textMinLines: Int = 1,
    textMaxLines: Int = 2,
    progress: (() -> Float)? = null,
    onClick: () -> Unit = {}
) {
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier.width(80.dp)
    ) {
        StoryPicture(
            url = coverUrl,
            modifier = modifier.clickable { onClick() }
        )

        if (progress != null)
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier.width(75.dp).clip(CircleShape)
            )

        Text(
            text,
            fontSize = MaterialTheme.typography.labelMedium.fontSize,
            textAlign = TextAlign.Center,
            minLines = textMinLines,
            maxLines = textMaxLines,
            overflow = TextOverflow.Ellipsis
        )
    }
}