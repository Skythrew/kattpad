package com.skythrew.kattpad.screens.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.currentBackStackEntryAsState
import com.skythrew.kattpad.screens.HomeScreen
import com.skythrew.kattpad.R

@Composable
fun GenericNav(navController: NavController) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()

        NavigationBarItem(
            selected = navBackStackEntry?.destination?.route == HomeScreen.javaClass.name,
            onClick = { navController.navigateOnce(HomeScreen) { popUpTo(0) } },
            icon = {
                Icon(
                Icons.Default.Home,
                contentDescription = ""
            )
            },
            label = { Text(stringResource(id = R.string.home)) }
        )
    }
}

inline fun <reified T: Any> NavController.navigateOnce(route: T, noinline builder: NavOptionsBuilder.() -> Unit = {}) {
    if (this.currentDestination!!.route?.startsWith(route.javaClass.name) == false)
        this.navigate(route, builder = builder)
}