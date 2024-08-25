package com.skythrew.kattpad.api

import com.skythrew.kattpad.api.requests.UserData
import io.ktor.client.request.cookie
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.decodeURLQueryComponent
import io.ktor.http.parameters
import io.ktor.http.setCookie

/**
 * An internal class to manage the authentication process.
 */
open class Authentication : com.skythrew.kattpad.api.config.Request() {
    var username: String? = null

    var loggedIn = false

    /**
     * Tries to log the client by using the given credentials.
     *
     * @param username
     * @param password
     */
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

    /**
     * Tries to log the client by using the given cookie.
     *
     * @param cookie Must be the `token` cookie
     */
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