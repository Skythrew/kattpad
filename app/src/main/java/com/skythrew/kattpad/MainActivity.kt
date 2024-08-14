package com.skythrew.kattpad

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.skythrew.kattpad.api.Wattpad
import com.skythrew.kattpad.screens.HomeScreen
import com.skythrew.kattpad.screens.PartScreen
import com.skythrew.kattpad.screens.ProfileScreen
import com.skythrew.kattpad.screens.StoryScreen
import com.skythrew.kattpad.screens.utils.GenericNav
import com.skythrew.kattpad.ui.theme.KattpadTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val client = Wattpad()

        enableEdgeToEdge()
        setContent {
            KattpadTheme {
                val navController = rememberNavController()
                
                var showBottomBar by remember {
                    mutableStateOf(true)
                }

                Scaffold (
                    bottomBar = { 
                        if (showBottomBar)
                            GenericNav(navController = navController)
                    }
                ){ padding ->
                    NavHost(navController = navController, startDestination = HomeScreen) {
                        composable<HomeScreen> {
                            showBottomBar = true
                            HomeScreen(padding = padding, navController = navController)
                        }

                        composable<StoryScreen> {
                            showBottomBar = false

                            val args = it.toRoute<StoryScreen>()

                            StoryScreen(padding = padding, navController = navController, client = client, storyId = args.storyId)
                        }

                        composable<PartScreen> {
                            showBottomBar = false

                            val args = it.toRoute<PartScreen>()

                            PartScreen(navController = navController, client = client, storyId = args.storyId, id = args.partId)
                        }

                        composable<ProfileScreen> {
                            showBottomBar = false

                            val args = it.toRoute<ProfileScreen>()

                            ProfileScreen(navController = navController, padding = padding, client = client, username = args.username)
                        }
                    }
                }

            }
        }
    }
}