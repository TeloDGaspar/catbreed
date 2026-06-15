package com.telogaspar.catbreed.breedList.data.mapper

import com.telogaspar.catbreed.breedList.data.model.BreedsResponse
import com.telogaspar.catbreed.breedList.data.model.Image
import com.telogaspar.catbreed.breedList.data.model.Weight
import com.telogaspar.catbreed.breedList.domain.model.Breed
import org.junit.Assert.assertEquals
import org.junit.Test

class EventMapperTest {

    private val mapper = EventMapper()

    @Test
    fun `GIVEN a valid response WHEN map is called THEN every field is converted to domain breed`() {
        val source = listOf(
            breedsResponse(
                id = "1",
                name = "Bengal",
                description = "Energetic cat",
                temperament = "Active",
                lifeSpan = "12 - 15",
                origin = "United States",
                imageUrl = "https://cat/1.png",
            ),
        )

        val result = mapper.map(source)

        assertEquals(1, result.size)
        val breed = result.first()
        assertEquals("1", breed.breedId)
        assertEquals("Bengal", breed.breedName)
        assertEquals("Energetic cat", breed.description)
        assertEquals("Active", breed.temperament)
        assertEquals("12 - 15", breed.lifeSpan)
        assertEquals("United States", breed.origin)
        assertEquals("https://cat/1.png", breed.imageUrl)
    }

    @Test
    fun `GIVEN multiple responses WHEN map is called THEN order is preserved and every item is mapped`() {
        val source = listOf(
            breedsResponse(id = "1", name = "Bengal"),
            breedsResponse(id = "2", name = "Persian"),
        )

        val result = mapper.map(source)

        assertEquals(listOf("1", "2"), result.map { it.breedId })
        assertEquals(listOf("Bengal", "Persian"), result.map { it.breedName })
    }

    @Test
    fun `GIVEN image and reference id are null WHEN map is called THEN image url is null`() {
        val source = listOf(breedsResponse(id = "1").copy(image = null, reference_image_id = null))

        val result = mapper.map(source)

        assertEquals(null, result.first().imageUrl)
    }

    @Test
    fun `GIVEN image url is null WHEN map is called THEN image url falls back to reference image cdn url`() {
        val source = listOf(
            breedsResponse(id = "1").copy(
                image = Image(height = 0, id = "img", url = null, width = 0),
                reference_image_id = "abc123",
            ),
        )

        val result = mapper.map(source)

        assertEquals("https://cdn2.thecatapi.com/images/abc123.jpg", result.first().imageUrl)
    }

    @Test
    fun `GIVEN image is null but reference id exists WHEN map is called THEN image url is built from reference id`() {
        val source = listOf(
            breedsResponse(id = "1").copy(image = null, reference_image_id = "xyz789"),
        )

        val result = mapper.map(source)

        assertEquals("https://cdn2.thecatapi.com/images/xyz789.jpg", result.first().imageUrl)
    }

    @Test
    fun `GIVEN empty source WHEN map is called THEN returns empty list`() {
        val result = mapper.map(emptyList())

        assertEquals(emptyList<Breed>(), result)
    }

    private fun breedsResponse(
        id: String,
        name: String = "Breed $id",
        description: String = "description-$id",
        temperament: String = "Active",
        lifeSpan: String = "10 - 15",
        origin: String = "Origin $id",
        imageUrl: String? = "https://cat/$id.png",
    ) = BreedsResponse(
        description = description,
        id = id,
        image = Image(height = 100, id = "img-$id", url = imageUrl, width = 100),
        life_span = lifeSpan,
        name = name,
        origin = origin,
        temperament = temperament,
        weight = Weight(imperial = "7 - 10", metric = "3 - 5"),
    )
}
