# Alternative: Offline-first breed list (single source of truth)

## Current implementation

`BreedListRepositoryImpl.fetchBreedList` is **network-first with a cache fallback**: it
fetches from the API, caches the result, and emits it. Only if the network call fails
(or returns empty) does it fall back to the cached page.

```kotlin
override fun fetchBreedList(page: Int, limit: Int): Flow<List<Breed>> = flow {
    val response = remoteDataSource.fetchBreedList(page = page, limit = limit)
    if (response.isEmpty()) throw BreedException.EmptyResultException()
    val breeds = mapper.map(response)
    localDataSource.upsertBreeds(breeds.map { it.toEntity() })
    emit(breeds)
}.catch { e ->
    val cached = localDataSource.getBreedsPage(limit = limit, offset = page * limit)
    if (cached.isNotEmpty()) {
        emit(cached.map { it.toDomain() })
    } else {
        throw e as? BreedException ?: BreedException.NetworkException(e)
    }
}.flowOn(Dispatchers.IO)
```

**Characteristics:** one emission per call. The user sees nothing until the network
responds (or fails). Cache is purely a fallback, never the primary display path.

---

## Alternative: offline-first (`NetworkBoundResource` pattern)

The room database becomes the **single source of truth**. The UI always observes the
database; the network is just a background job that refreshes it.

The flow emits **twice**:
1. Cached data immediately (instant UI, even offline).
2. Fresh data after the network refresh completes and writes to the DB.

```kotlin
override fun fetchBreedList(page: Int, limit: Int): Flow<List<Breed>> = flow {
    // 1. Emit cache immediately (if any) for instant UI
    val cached = localDataSource.getBreedsPage(limit = limit, offset = page * limit)
    if (cached.isNotEmpty()) {
        emit(cached.map { it.toDomain() })
    }

    // 2. Refresh from network, write through to cache, emit fresh
    try {
        val response = remoteDataSource.fetchBreedList(page = page, limit = limit)
        if (response.isEmpty() && cached.isEmpty()) throw BreedException.EmptyResultException()
        val breeds = mapper.map(response)
        localDataSource.upsertBreeds(breeds.map { it.toEntity() })
        emit(breeds)
    } catch (e: Exception) {
        // Only surface the error if we have nothing cached to show
        if (cached.isEmpty()) {
            throw e as? BreedException ?: BreedException.NetworkException(e)
        }
    }
}.flowOn(Dispatchers.IO)
```

A stricter variant uses Room's `Flow` return types so the DB **continuously** drives the
UI: the network writes to Room, and a `dao.observeBreedsPage(...): Flow<List<Entity>>`
re-emits automatically. The repository then never emits the network result directly — it
only triggers a refresh.

```kotlin
override fun fetchBreedList(page: Int, limit: Int): Flow<List<Breed>> =
    localDataSource.observeBreedsPage(limit = limit, offset = page * limit)
        .onStart { refreshBreedList(page, limit) }   // fire-and-forget network refresh
        .map { entities -> entities.map { it.toDomain() } }
        .flowOn(Dispatchers.IO)
```

---

## Benefits

- **Instant UI / offline support** — cached data shows immediately, before (or without)
  any network response.
- **Single source of truth** — the DB is the only thing the UI reads, so cache and screen
  can never disagree.
- **Resilient** — a network failure is silent when cache exists; the user keeps seeing data.

## Why this is NOT adopted here

The current `BreedListViewModel` pagination appends pages:

```kotlin
allBreeds = if (isLoadingMore) it.allBreeds + newBreeds else newBreeds
```

An offline-first flow emits **twice per page** (cache, then network). For `isLoadingMore`
that would **append the same page twice**, duplicating rows. Adopting offline-first
therefore requires reworking the ViewModel's collect logic — e.g.:

- Replace append-on-emit with a keyed merge / `distinctBy { breedId }`, or
- Track loaded pages as a `Map<Int, List<Breed>>` and recompute `allBreeds` from it, or
- Move pagination into the DB query and observe a single growing `Flow` of all loaded rows.

Given the current paging design, the **network-first + cache-fallback** approach (single
emission per page) is the simpler, correct fit. Offline-first becomes worthwhile if the
app needs strong offline support or a guaranteed single source of truth, and is paired with
the ViewModel changes above.
