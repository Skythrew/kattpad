package com.skythrew.kattpad.screens.utils

import androidx.compose.foundation.lazy.LazyListState

internal fun LazyListState.reachedLast(buffer: Int = 1): Boolean {
    val lastVisibleItem = this.layoutInfo.visibleItemsInfo.lastOrNull()

    return if (lastVisibleItem != null)
        lastVisibleItem.index != 0 && lastVisibleItem.index >= this.layoutInfo.totalItemsCount - buffer
    else
        false
}