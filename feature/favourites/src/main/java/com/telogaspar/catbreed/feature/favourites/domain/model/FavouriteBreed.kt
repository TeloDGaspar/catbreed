package com.telogaspar.catbreed.feature.favourites.domain.model

data class FavouriteBreed(
    val id: String,
    val name: String,
    val origin: String,
    val imageUrl: String?,
    val lifeSpan: String,
)
