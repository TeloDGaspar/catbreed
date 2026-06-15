# Alternative: Resolve breed image URL via `/images/{id}` endpoint

## Current implementation

`EventMapper` builds the image URL directly from `reference_image_id` when the `/breeds` response does not include an `image` object:

```kotlin
// feature/breedList/src/main/java/.../data/mapper/EventMapper.kt
private const val CAT_CDN_BASE_URL = "https://cdn2.thecatapi.com/images/"

private fun resolveImageUrl(item: BreedsResponse): String? =
    item.image?.url ?: item.reference_image_id?.let { CAT_CDN_BASE_URL + it + ".jpg" }
```

**Trade-off:** assumes `.jpg` extension. Images that are `.png` or `.gif` will fail to load and fall back to the initials avatar in `BreedRowCard`.

---

## Alternative: fetch the real URL from the images endpoint

For the **breed detail screen**, where image quality matters and only a single breed is shown, the real URL (with the correct format) can be fetched from:

```
GET https://api.thecatapi.com/v1/images/{image_id}
```

Response:
```json
{
  "id": "0XYvRd7oD",
  "url": "https://cdn2.thecatapi.com/images/0XYvRd7oD.jpg",
  "width": 1204,
  "height": 1445
}
```

### Where to add it

**API interface** — `feature/breedList/src/main/java/.../data/api/SportsEventApi.kt`

```kotlin
@GET("images/{imageId}")
suspend fun getImage(@Path("imageId") imageId: String): ImageResponse
```

**Model**

```kotlin
data class ImageResponse(
    val id: String,
    val url: String,
    val width: Int,
    val height: Int,
)
```

**Remote data source** — add a method to `BreedEventListRemoteDataSource` / `BreedEventListRemoteDataSourceImpl`:

```kotlin
suspend fun fetchImageUrl(imageId: String): String?
```

**Repository** — `BreedListRepository.fetchBreedById` already reads the entity from `BreedLocalDataSource`. Extend it to resolve the image URL when `imageUrl` is null or ends with `.jpg` but you want to confirm the format:

```kotlin
override fun fetchBreedById(id: String): Flow<Breed> = flow {
    val entity = localDataSource.getBreedById(id)
        ?: throw BreedException.NetworkException(NoSuchElementException("Breed $id not found"))
    val breed = entity.toDomain()

    // Resolve real image URL if only a reference_image_id is available
    val resolvedUrl = breed.imageUrl
        ?: entity.referenceImageId?.let { remoteDataSource.fetchImageUrl(it) }

    emit(breed.copy(imageUrl = resolvedUrl))
}
```

### Why only on the detail screen

Calling `/images/{id}` per breed on a 15-item list page creates an N+1 problem (up to 15 extra requests). On the detail screen the cost is a single request and the benefit — correct format, full resolution — is worth it.
