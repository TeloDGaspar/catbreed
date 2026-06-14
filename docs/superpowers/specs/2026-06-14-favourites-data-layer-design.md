# Favourites — Data Layer Design

**Date:** 2026-06-14
**Scope:** Data layer only — `FavouriteEntity`, `FavouriteDao`, `FavouriteRepository` interface + impl. No UI.

---

## Goal

Persist favourite breeds locally so the user's selections survive app restarts and cache wipes. Provide a clean repository interface inside `:feature:favourites` that future UI layers (favourites screen, breed list heart toggle) can depend on.

---

## Module Responsibility Split

| Layer | Module | What lives there |
|---|---|---|
| DB entity | `:core` | `FavouriteEntity` |
| DAO | `:core` | `FavouriteDao` |
| DB registration + DAO provision | `:core` | `CatBreedsDatabase`, `DatabaseModule` |
| Repository interface + impl | `:feature:favourites` | `FavouriteRepository`, `FavouriteRepositoryImpl`, `FavouritesModule` |

`FavouriteRepository` lives in `:feature:favourites` (not `:core`) because only the favourites feature needs it for now. When sibling features (breedList, breedDetail) need the toggle, the interface will be promoted to `:core` at that point — no premature promotion.

---

## `:core` changes

### `FavouriteEntity`

```kotlin
@Entity(tableName = "favourites")
data class FavouriteEntity(@PrimaryKey val breedId: String)
```

Separate table from `cat_breeds` — no Room `ForeignKey` constraint — so a favourite persists even when the breed cache is wiped.

### `FavouriteDao`

```kotlin
@Dao
interface FavouriteDao {
    @Upsert
    suspend fun insertFavourite(favourite: FavouriteEntity)

    @Query("DELETE FROM favourites WHERE breedId = :breedId")
    suspend fun deleteFavourite(breedId: String)

    @Query("SELECT breedId FROM favourites")
    fun getFavouriteIds(): Flow<List<String>>

    @Query("""
        SELECT b.* FROM cat_breeds b
        INNER JOIN favourites f ON b.id = f.breedId
        ORDER BY b.name
    """)
    fun getFavouriteBreeds(): Flow<List<CatBreedEntity>>
}
```

`getFavouriteIds` and `getFavouriteBreeds` return `Flow` so the UI layer can observe changes reactively.

### `CatBreedsDatabase`

- Add `FavouriteEntity::class` to `entities`
- Bump `version` from `2` → `3`
- `fallbackToDestructiveMigration(true)` is already set — no migration code needed

### `DatabaseModule`

Add a `@Provides @Singleton fun provideFavouriteDao(db: CatBreedsDatabase): FavouriteDao` binding.

---

## `:feature:favourites` additions

### `build.gradle.kts`

Add:
```kotlin
implementation(project(":core"))
implementation(libs.hilt.android)
ksp(libs.hilt.compiler)
implementation(libs.kotlinx.coroutines.android)
testImplementation(libs.mockk)
testImplementation(kotlin("test-junit"))
testImplementation(libs.kotlinx.coroutines.test)
```

### `FavouriteRepository` interface

```kotlin
interface FavouriteRepository {
    fun getFavouriteBreeds(): Flow<List<CatBreedEntity>>
    fun getFavouriteIds(): Flow<Set<String>>
    suspend fun addFavourite(breedId: String)
    suspend fun removeFavourite(breedId: String)
}
```

`getFavouriteIds` returns `Set<String>` (not the DAO's `List<String>`) — O(1) membership checks by callers.

### `FavouriteRepositoryImpl`

- Wraps `FavouriteDao`
- `getFavouriteIds()`: maps `Flow<List<String>>` → `Flow<Set<String>>` via `.map { it.toSet() }`
- `addFavourite(breedId)`: calls `dao.insertFavourite(FavouriteEntity(breedId))`
- `removeFavourite(breedId)`: calls `dao.deleteFavourite(breedId)`

### `FavouritesModule` (Hilt)

```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class FavouritesModule {
    @Binds @Singleton
    abstract fun bindFavouriteRepository(impl: FavouriteRepositoryImpl): FavouriteRepository
}
```

---

## Testing

### `FavouriteRepositoryImplTest`

Unit tests with a mockk `FavouriteDao`. Covers:

| Test | What it verifies |
|---|---|
| `addFavourite` delegates to DAO insert | `insertFavourite(FavouriteEntity(breedId))` called exactly once |
| `removeFavourite` delegates to DAO delete | `deleteFavourite(breedId)` called exactly once |
| `getFavouriteIds` maps list to set | `Flow<List<"a","a","b">>` → `Flow<Set<"a","b">>` (deduplication) |
| `getFavouriteBreeds` passes DAO flow through | emitted list matches DAO emission |

---

## What is explicitly out of scope

- No `toggleFavourite` convenience method — callers explicitly call `add` or `remove`. Toggle logic belongs in the ViewModel when UI is added.
- No UI (`FavouritesViewModel`, `FavouritesScreen`) — separate task.
- No promotion of `FavouriteRepository` to `:core` — deferred until sibling features need it.
