package com.telogaspar.catbreed.breedList.domain.model

data class Breed(
    val breedId: String,
    val breedName: String,
    val imageUrl: String?,
    val lifeSpan: String?,
    val origin: String?,
    val temperament: String?,
    val description: String?,
    val weightMetric: String?,
)