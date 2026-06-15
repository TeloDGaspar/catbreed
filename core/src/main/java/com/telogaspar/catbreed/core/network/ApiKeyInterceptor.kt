package com.telogaspar.catbreed.core.network

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Adds TheCatAPI authentication header to every request.
 *
 * The key is injected from `BuildConfig.CAT_API_KEY` (sourced from local.properties or a
 * CI env var). When no key is configured the header is omitted so the app still works for
 * the API's keyless endpoints.
 */
internal class ApiKeyInterceptor(
    private val apiKey: String,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (apiKey.isBlank()) {
            return chain.proceed(request)
        }
        val authenticatedRequest = request.newBuilder()
            .addHeader(HEADER_API_KEY, apiKey)
            .build()
        return chain.proceed(authenticatedRequest)
    }

    private companion object {
        const val HEADER_API_KEY = "x-api-key"
    }
}
