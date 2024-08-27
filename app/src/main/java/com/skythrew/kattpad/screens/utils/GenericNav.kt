package com.skythrew.kattpad.screens.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.currentBackStackEntryAsState
import com.skythrew.kattpad.R
import com.skythrew.kattpad.screens.HomeScreen
import com.skythrew.kattpad.screens.NotificationScreen

@Composable
fun GenericNav(navController: NavController, notificationCount: Int? = null) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()

        NavigationBarItem(
            selected = navBackStackEntry?.destination?.hasRoute<HomeScreen>() ?: false,
            onClick = { navController.navigateOnce(HomeScreen) { popUpTo(0) } },
            icon = {
                Icon(
                Icons.Default.Home,
                contentDescription = ""
            )
            },
            label = { Text(stringResource(id = R.string.home)) }
        )

        if (notificationCount != null)
            NavigationBarItem(
                selected = navBackStackEntry?.destination?.hasRoute<NotificationScreen>() ?: false,
                onClick = { navController.navigateOnce(NotificationScreen) { popUpTo(0) } },
                icon = {
                    BadgedBox(badge = {
                        if (notificationCount > 0)
                            Badge {
                                Text(notificationCount.toString())
                            }
                    }) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = ""
                        )
                    }
                },
                label = { Text("Notifications") }
            )
    }
}

fun NavController.navigateOnce(route: Any, builder: NavOptionsBuilder.() -> Unit = {}) {
    if (this.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED)
        this.navigate(route, builder)
}

fun NavController.popBackOnce() {
    if (this.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED)
        this.popBackStack()
}

fun NavController.navReplace(route: Any, builder: NavOptionsBuilder.() -> Unit = {}) {
    if (this.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
        this.popBackStack()
        this.navigate(route, builder)
    }
}