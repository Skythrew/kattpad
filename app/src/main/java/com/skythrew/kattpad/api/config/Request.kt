package com.skythrew.kattpad.api.config

import io.ktor.client.HttpClient
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.ConstantCookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.cookies.cookies
import io.ktor.client.plugins.cookies.get
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.delete
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Cookie
import io.ktor.http.HttpMethod
import io.ktor.http.Parameters
import io.ktor.http.decodeURLQueryComponent
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.util.Locale

open class Request {
    private var client = HttpClient {
        install(ContentNegotiation) {
            json()
        }
        install(ContentEncoding) {
            deflate(1.0F)
            gzip(0.9F)
        }
        defaultRequest {
            header("Accept-Language", Locale.getDefault().toLanguageTag())
        }
    }

    private val apis = mapOf(
        "v2" to "https://www.wattpad.com/apiv2/",
        "v3" to "https://www.wattpad.com/api/v3/",
        "v4" to "https://api.wattpad.com/v4/",
        "v5" to "https://api.wattpad.com/v5/"
    )

    val jsonDecoder = Json {
        ignoreUnknownKeys = true
    }

    suspend fun getToken(): String? {
        return client.cookies("https://www.wattpad.com")["token"]?.value?.decodeURLQueryComponent()
    }

    fun updateHttpClient(cookie: String? = null) {
        client = client.config {
            install(HttpCookies) {
                storage =
                    if (cookie != null)
                        ConstantCookiesStorage(
                            Cookie(
                                name = "token",
                                value = cookie,
                                domain = ".wattpad.com"
                            )
                        )
                    else
                        ConstantCookiesStorage()
            }
        }
    }

    suspend fun getAPI(api: String, path: String, options: HttpRequestBuilder.() -> Unit): HttpResponse {
        return client.get("${apis[api]}$path", options)
    }

    suspend fun postAPI(api: String, path: String, options: HttpRequestBuilder.() -> Unit): HttpResponse {
        return client.post("${apis[api]}$path", options)
    }

    suspend fun putAPI(api: String, path: String, options: HttpRequestBuilder.() -> Unit): HttpResponse {
        return client.put("${apis[api]}$path", options)
    }

    suspend fun deleteAPI(api: String, path: String, options: HttpRequestBuilder.() -> Unit): HttpResponse {
        return client.delete("${apis[api]}$path", options)
    }

    suspend fun simplePost(url: String, data: Parameters): HttpResponse {
        return client.submitForm(
            url,
            formParameters = data
        )
    }

    suspend fun simpleGet(url: String): HttpResponse {
        return client.get(url)
    }

    suspend fun makeRequest(api: String, path: String, options: HttpRequestBuilder.() -> Unit): HttpResponse {
        return client.request("${apis[api]}$path", options)
    }

    suspend inline fun <reified T> fetch(api: String, path: String, params: Map<String, String>): T {
        val res = this.makeRequest(api, path) {
            method = HttpMethod.Get
            url {
                params.forEach {entry -> parameters.append(entry.key, entry.value)}
            }
        }

        return jsonDecoder.decodeFromString<T>(res.bodyAsText())
    }

    suspend inline fun <reified T> fetchObjData(api: String, path: String, fields: Set<String>, limit: Int = 0, offset: Int = 0): T {
        val params: Map<String, String> = mapOf(
            "offset" to offset.toString(),
            when { fields.isNotEmpty() -> "fields" to fields.joinToString(",") else -> "" to ""},
            "limit" to limit.toString(),
        )

        return fetch(api, path, params)
    }
}