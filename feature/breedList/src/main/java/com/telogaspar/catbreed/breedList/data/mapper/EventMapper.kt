package com.telogaspar.catbreed.breedList.data.mapper

import com.telogaspar.catbreed.breedList.data.model.BreedsResponse
import com.telogaspar.catbreed.breedList.domain.model.Breed
import com.telogaspar.catbreed.core.mapper.Mapper
import javax.inject.Inject

private const val CAT_CDN_BASE_URL = "https://cdn2.thecatapi.com/images/"

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
                imageUrl = resolveImageUrl(item),
                weightMetric = item.weight.metric,
            )
        }
    }

    /**
     *
     * KNOWN LIMITATION: this assumes a `.jpg` extension. Some breed images are `.png`/`.gif`
     * and will 404 (Coil then falls back to the initials avatar). The accurate implementation is
     * to call `GET /images/{id}` per breed, but it is one more network call
     */
    private fun resolveImageUrl(item: BreedsResponse): String? =
        item.image?.url ?: item.reference_image_id?.let { CAT_CDN_BASE_URL + it + ".jpg" }
}
