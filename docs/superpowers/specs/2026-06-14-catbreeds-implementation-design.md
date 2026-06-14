# CatBreeds Android — Implementation Design

**Date:** 2026-06-14  
**Challenge:** TeamViewer Android Code Challenge  
**Project:** `/Users/CTW02097/Documents/MyProjects/CatBreeds`

---

## Context

This is a multi-module Android project that partially exists. The networking stack, Hilt wiring, domain models, repository interfaces, and unit/integration tests for `:feature:breedList` are already in place. Everything UI, persistence, and pagination is missing.

**Tech decisions (locked):**
- UI: Jetpack Compose + Material3
- Architecture: MVVM + Clean Architecture (already established)
- DI: Hilt (already set up)
- Network: Retrofit + OkHttp (already in `:core`)
- Concurrency: Kotlin Coroutines + Flow
- Pagination: Paging 3
- Persistence: Room (network-first, Room as offline fallback)

---

## Module Structure

```
:app                      — Application, MainActivity, NavHost, bottom nav
:core                     — NetworkModule (Retrofit), Mapper interface, Room DB + DAOs, DatabaseModule
:feature:breedList        — Breed list screen, ViewModel, pagination, API, repository (already partially built)
:feature:favourites       — Favourites screen, ViewModel, repository
:feature:breedDetail      — Breed detail screen, ViewModel (reads from Room cache)
```

`breedDetail` is a separate module. It depends only on `:core` (for `CatBreedDao` and `FavouriteRepository`) and has no dependency on `:feature:breedList`.

---

## Room Schema (in `:core`)

### `CatBreedEntity`
```
id          String  PK
name        String
origin      String
temperament String
description String
lifeSpan    String
imageUrl    String?
```

### `FavouriteEntity`
```
breedId     String  PK  (FK → CatBreedEntity.id)
```
Kept as a separate table so favourite status survives a cache wipe.

### DAOs
- `CatBreedDao`
  - `upsertBreeds(breeds: List<CatBreedEntity>)`
  - `getBreedsPage(limit: Int, offset: Int): List<CatBreedEntity>` — `SELECT * FROM cat_breeds ORDER BY name LIMIT :limit OFFSET :offset`
  - `getBreedById(id: String): CatBreedEntity?`
- `FavouriteDao`
  - `insertFavourite(breedId: String)`
  - `deleteFavourite(breedId: String)`
  - `getFavouriteIds(): Flow<Set<String>>`
  - `getFavouriteBreeds(): Flow<List<CatBreedEntity>>`

### Hilt
`DatabaseModule` in `:core` — `@Singleton` `CatBreedsDatabase`, provides both DAOs.

---

## Pagination Strategy (`:feature:breedList`)

### API change
Add `@Query("page") page: Int = 0` to `BreedsEventApi.getBreeds()`. The Cat API accepts `limit` + `page`.

### `BreedPagingSource : PagingSource<Int, Breed>`
- `loadKey` starts at 0, increments by 1 per page
- Per page load:
  1. Try network → on success: upsert to Room, return network data
  2. On network failure → read Room via `getBreedsPage(limit, offset = page * limit)` → return if non-empty
  3. If Room also empty → propagate `LoadResult.Error`
- `nextKey = null` when network returns fewer items than `pageSize`

### Repository
`BreedListRepository` gains:
```kotlin
fun getPagedBreeds(): Flow<PagingData<Breed>>
```
Returns `Pager(PagingConfig(pageSize = 15)) { BreedPagingSource(...) }.flow`.

Existing `fetchBreedList(): Flow<List<Breed>>` is removed (superseded by paging).

---

## `:feature:breedList` UI

### `BreedListViewModel` (`@HiltViewModel`)
- Holds `pagedBreeds: Flow<PagingData<Breed>>` from repository
- Holds `favouriteIds: StateFlow<Set<String>>` from `FavouriteRepository`
- `query: StateFlow<String>` — local search filter applied via `PagingData.filter`
- `toggleFavourite(breedId: String)` — delegates to `FavouriteRepository`

### `BreedListScreen`
- `TopAppBar` with search `OutlinedTextField`
- `LazyColumn` + `collectAsLazyPagingItems()`
- Each `BreedCard`: thumbnail image (Coil), breed name, origin, heart icon toggle (filled/outlined based on `favouriteIds`)
- Load states: `LoadingState` composable for initial load, inline footer spinner for append, `ErrorState` composable with retry on failure

---

## `:feature:favourites`

### `FavouriteRepository` (lives in `:core`, not `:feature:favourites`)
```kotlin
fun getFavouriteBreeds(): Flow<List<CatBreedEntity>>
fun getFavouriteIds(): Flow<Set<String>>
suspend fun toggleFavourite(breedId: String)
```
Lives in `:core` because it is injected by `breedList`, `breedDetail`, and `favourites` — putting it in any one feature module would create a sibling feature dependency. Implementation wraps `FavouriteDao` directly. All three feature modules already depend on `:core`.

### `FavouritesViewModel` (`@HiltViewModel`)
- `uiState: StateFlow<FavouritesUiState>`
- `FavouritesUiState(breeds: List<Breed>, averageLifespan: Double?)`
- Average lifespan: parse the lower bound of each `lifeSpan` string (e.g. `"10 - 15"` → `10`), average across all favourites.

### `FavouritesScreen`
- Summary banner at top: "Average lifespan: X years" (hidden when list is empty)
- `LazyColumn` of favourite breed cards with remove toggle
- Empty state when no favourites

---

## `:feature:breedDetail`

### `BreedDetailViewModel` (`@HiltViewModel`)
- Takes `breedId: String` via `SavedStateHandle`
- Loads breed from `CatBreedDao.getBreedById(id)` (always available — breed was cached when list was loaded)
- Exposes `uiState: StateFlow<BreedDetailUiState>`
- `toggleFavourite(breedId)` via `FavouriteRepository`

### `BreedDetailScreen`
Displays: breed name (headline), origin, temperament (chips or comma list), description, hero image. FAB or icon button for favourite toggle (filled/outlined).

---

## Navigation (`:app`)

```
NavHost
├── BottomNav tab: "Breeds"     → BreedListScreen
│                                  └── navigate("detail/{breedId}") → BreedDetailScreen
└── BottomNav tab: "Favourites" → FavouritesScreen
                                   └── navigate("detail/{breedId}") → BreedDetailScreen
```

`BreedDetailScreen` is a shared destination reachable from both tabs. Each bottom tab owns its own back stack via `rememberNavController` per tab or `saveState`/`restoreState`.

---

## API Key Security

API key stored in `local.properties` (git-ignored), injected via `BuildConfig` field in `:app`'s `build.gradle.kts`. Passed to `NetworkModule` via a `@Named("apiKey")` Hilt binding. Never hardcoded in source.

---

## Error & State Handling

Every screen handles four states: **Loading**, **Success**, **Empty**, **Error**.  
Repository errors surface as `Flow` emissions or `LoadResult.Error` (paging). ViewModels never swallow exceptions silently (fix existing silent `catch (e: Exception) {}` in `BreedListRepositoryImpl`).

---

## Testing

### Unit tests (existing — keep and extend)
- `BreedListRepositoryImplTest` — add offline fallback case
- `EventMapperTest` — already exists
- `BreedEventListRemoteDataSourceImplTest` — already exists

### New unit tests
- `BreedListViewModelTest` — paging + search filter + favourite toggle
- `FavouritesViewModelTest` — average lifespan calculation, empty state
- `BreedDetailViewModelTest` — load breed, toggle favourite

### Integration test (existing — keep)
- `BreedApiIntegrationTest` — already hits live API

---

## What Would Come Next (README outline)
- Instrumentation / E2E tests with Espresso or Compose test APIs
- Image caching strategy (Coil disk cache config)
- Proguard / R8 rules for release build
- CI pipeline (GitHub Actions)
