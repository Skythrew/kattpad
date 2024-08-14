package com.skythrew.kattpad.screens.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.skythrew.kattpad.screens.DiscoveryScreen
import com.skythrew.kattpad.R

@Composable
fun GenericNav(navController: NavController) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()

        NavigationBarItem(
            selected = navBackStackEntry?.destination?.route == DiscoveryScreen.javaClass.name,
            onClick = { navController.navigate(DiscoveryScreen) },
            icon = {
                Icon(
                Icons.Default.Search,
                contentDescription = ""
            )
            },
            label = { Text(stringResource(id = R.string.discovery_tab_name)) }
        )
    }
}