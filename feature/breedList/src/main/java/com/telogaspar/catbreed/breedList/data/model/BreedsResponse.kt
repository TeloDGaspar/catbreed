package com.telogaspar.catbreed.breedList.data.model

data class BreedsResponse(
    val description: String,
    val id: String,
    val image: Image?,
    val life_span: String,
    val name: String,
    val origin: String,
    val temperament: String,
    val weight: Weight,
)