package com.skythrew.kattpad.api

import com.skythrew.kattpad.api.requests.MinUserData
import com.skythrew.kattpad.api.requests.UserData
import io.ktor.client.request.cookie
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.decodeURLQueryComponent
import io.ktor.http.parameters
import io.ktor.http.setCookie

open class Authentication : com.skythrew.kattpad.api.config.Request() {
    var username: String? = null

    var loggedIn = false

    suspend fun login(username: String, password: String) {
        val loginResponse = this.simplePost("https://www.wattpad.com/login", parameters {
            append("username", username)
            append("password", password)
        })

        val cookies = loginResponse.setCookie()

        for (cookie in cookies) {
            if (cookie.name == "token") {
                this.updateHttpClient(cookie.value.decodeURLQueryComponent())

                loggedIn = true
                this.username = username

                break
            }
        }
    }

    suspend fun loginByCookie(cookie: String) {
        val loginResponse = this.getAPI("v3", "internal/current_user") {
            cookie(name = "token", value = cookie)
            url {
                parameters {
                    append("fields", "username")
                }
            }
        }

        if (loginResponse.status == HttpStatusCode.OK) {
            val userData = this.jsonDecoder.decodeFromString<UserData>(loginResponse.bodyAsText())

            this.updateHttpClient(cookie)

            loggedIn = true
            username = userData.username
        }
    }
}