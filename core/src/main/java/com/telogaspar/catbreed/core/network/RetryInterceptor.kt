package com.telogaspar.catbreed.core.network

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Retries transient network failures with a fixed backoff.
 *
 */
internal class RetryInterceptor(
    private val maxRetries: Int = 2,
    private val retryDelayMillis: Long = 500L,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var attempt = 0
        var lastError: IOException? = null

        while (attempt <= maxRetries) {
            try {
                val response = chain.proceed(chain.request())
                if (response.isRetryable() && attempt < maxRetries) {
                    response.close()
                    attempt++
                    Thread.sleep(retryDelayMillis * attempt)
                    continue
                }
                return response
            } catch (e: IOException) {
                lastError = e
                if (attempt >= maxRetries) break
                attempt++
                Thread.sleep(retryDelayMillis * attempt)
            }
        }
        throw lastError ?: IOException("Request failed after $maxRetries retries")
    }

    private fun Response.isRetryable(): Boolean = code == 429 || code in 500..599
}
