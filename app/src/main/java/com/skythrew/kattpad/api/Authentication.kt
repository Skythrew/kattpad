package com.skythrew.kattpad.api

import io.ktor.http.parameters
import io.ktor.http.setCookie

open class Authentication : com.skythrew.kattpad.api.config.Request() {
    var loggedIn = false

    suspend fun login(username: String, password: String) {
        val loginResponse = this.simplePost("https://www.wattpad.com/login", parameters {
            append("username", username)
            append("password", password)
        })

        val cookies = loginResponse.setCookie()

        for (cookie in cookies) {
            if (cookie.name == "token") {
                loggedIn = true
                break
            }
        }
    }
}