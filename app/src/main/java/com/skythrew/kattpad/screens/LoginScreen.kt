package com.skythrew.kattpad.screens

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.navigation.NavController
import com.skythrew.kattpad.R
import com.skythrew.kattpad.api.Wattpad
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen (padding: PaddingValues, navController: NavController, client: Wattpad, store: SharedPreferences, loggedState: MutableState<Boolean>) {
    val coroutineScope = rememberCoroutineScope()

    var username by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var isLogging by remember {
        mutableStateOf(false)
    }

    var loggedIn by remember {
        mutableStateOf(client.loggedIn)
    }

    var incorrectCredentials by remember {
        mutableStateOf(false)
    }

    TopAppBar(title = {}, navigationIcon = {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.back))
        }
    })
    Column (
        modifier = Modifier
            .padding(padding)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            Text(stringResource(id = R.string.login_short_text), fontWeight = FontWeight.Bold, fontSize = MaterialTheme.typography.headlineMedium.fontSize)

            Column (
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                TextField(
                    value = username,
                    singleLine = true,
                    onValueChange = {username = it},
                    trailingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null)
                    },
                    label = {Text(stringResource(id = R.string.username))},
                    isError = incorrectCredentials,
                    supportingText = {
                        if (incorrectCredentials)
                            Text(stringResource(id = R.string.incorrect_credentials))
                    }
                )

                TextField(
                    value = password,
                    singleLine = true,
                    onValueChange = {password = it},
                    trailingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null)
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    label = {Text(stringResource(id = R.string.password))},
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    isError = incorrectCredentials,
                    supportingText = {
                        if (incorrectCredentials)
                            Text(stringResource(id = R.string.incorrect_credentials))
                    }
                )
            }


            TextButton(onClick = {
                coroutineScope.launch {
                    isLogging = true

                    client.login(
                        username,
                        password
                    )

                    loggedIn = client.loggedIn
                    Log.d("LOGIN", loggedIn.toString())
                    incorrectCredentials = client.loggedIn == false

                    if (client.loggedIn) {
                        loggedState.value = true

                        navController.popBackStack()

                        store.edit {
                            putString("username", username)
                            putString("password", password)

                            apply()
                        }
                    }

                    isLogging = false
                }
            }) {
                Text(stringResource(id = R.string.log_in))
            }
        }
    }
}

@Serializable
object LoginScreen