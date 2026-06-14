package com.telogaspar.catbreed.breedList.data.mapper

import com.telogaspar.catbreed.breedList.data.model.BreedsResponse
import com.telogaspar.catbreed.breedList.domain.Breed
import com.telogaspar.catbreed.core.mapper.Mapper
import javax.inject.Inject

internal class EventMapper @Inject constructor() : Mapper<List<BreedsResponse>, List<Breed>> {

    override fun map(source: List<BreedsResponse>): List<Breed> {
        return source.map { item ->
            Breed(
                breedId = item.id,
                breedName = item.name,
                description = item.description,
                temperament = item.temperament,
                lifeSpan = item.life_span,
                origin = item.origin,
                imageUrl = item.image?.url ?: "",
                weightMetric = item.weight.metric,
            )
        }
    }
}