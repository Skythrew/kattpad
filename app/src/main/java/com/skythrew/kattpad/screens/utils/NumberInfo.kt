package com.skythrew.kattpad.screens.utils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

fun getFormattedNumber(number: Int?): String = when {
    number == null -> ""
    number < 1000 -> number.toString()
    number >= 1_000_000 -> (number / 1_000_000).toString() + "M"
    number >= 1000 -> (number / 1000).toString() + "K"
    else -> ""
}

@Composable
fun NumberInfo(icon: ImageVector, number: Int, altText: String = "") {
    Row (verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)){
        Icon(icon, contentDescription = altText)
        Text(getFormattedNumber(number))
    }
}