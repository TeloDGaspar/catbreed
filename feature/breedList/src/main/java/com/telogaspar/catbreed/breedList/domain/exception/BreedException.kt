package com.telogaspar.catbreed.breedList.domain.exception

sealed class BreedException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class NetworkException(cause: Throwable) : BreedException("Failed to fetch breeds from network", cause)
    class EmptyResultException : BreedException("No breeds returned from the server")
    class NotFoundException(id: String) : BreedException("Breed $id not found")
}