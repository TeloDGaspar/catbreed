# Favourites Data Layer Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add persistent favourites storage — `FavouriteEntity` + `FavouriteDao` in `:core`, and `FavouriteRepository` interface + impl + Hilt wiring in `:feature:favourites`.

**Architecture:** DB infrastructure (entity, DAO, DB registration, DAO provision) lives in `:core` alongside the existing `CatBreedEntity`/`CatBreedDao`. The repository interface and implementation live in `:feature:favourites`, keeping feature logic out of core. Hilt binds the interface to the impl via an abstract module inside the feature.

**Tech Stack:** Room (entity, DAO, `@Upsert`/`@Query`), Hilt (`@Binds`, `@Singleton`, `SingletonComponent`), Kotlin Coroutines + Flow, MockK (unit tests).

---

## File Map

| Action | Path |
|---|---|
| **Create** | `core/src/main/java/com/telogaspar/catbreed/core/database/entity/FavouriteEntity.kt` |
| **Create** | `core/src/main/java/com/telogaspar/catbreed/core/database/dao/FavouriteDao.kt` |
| **Modify** | `core/src/main/java/com/telogaspar/catbreed/core/database/CatBreedsDatabase.kt` |
| **Modify** | `core/src/main/java/com/telogaspar/catbreed/core/database/DatabaseModule.kt` |
| **Modify** | `feature/favourites/build.gradle.kts` |
| **Create** | `feature/favourites/src/main/java/com/telogaspar/catbreed/feature/favourites/domain/FavouriteRepository.kt` |
| **Create** | `feature/favourites/src/main/java/com/telogaspar/catbreed/feature/favourites/data/FavouriteRepositoryImpl.kt` |
| **Create** | `feature/favourites/src/main/java/com/telogaspar/catbreed/feature/favourites/di/FavouritesModule.kt` |
| **Create** | `feature/favourites/src/test/java/com/telogaspar/catbreed/feature/favourites/data/FavouriteRepositoryImplTest.kt` |

---

## Task 1: `FavouriteEntity` and `FavouriteDao` in `:core`

**Files:**
- Create: `core/src/main/java/com/telogaspar/catbreed/core/database/entity/FavouriteEntity.kt`
- Create: `core/src/main/java/com/telogaspar/catbreed/core/database/dao/FavouriteDao.kt`

- [ ] **Step 1: Create `FavouriteEntity`**

```kotlin
// core/src/main/java/com/telogaspar/catbreed/core/database/entity/FavouriteEntity.kt
package com.telogaspar.catbreed.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourites")
data class FavouriteEntity(@PrimaryKey val breedId: String)
```

- [ ] **Step 2: Create `FavouriteDao`**

```kotlin
// core/src/main/java/com/telogaspar/catbreed/core/database/dao/FavouriteDao.kt
package com.telogaspar.catbreed.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.telogaspar.catbreed.core.database.entity.CatBreedEntity
import com.telogaspar.catbreed.core.database.entity.FavouriteEntity
import kotlinx.coroutines.flow.Flow

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

- [ ] **Step 3: Register `FavouriteEntity` in `CatBreedsDatabase` and bump version**

Replace the current content of `CatBreedsDatabase.kt` with:

```kotlin
// core/src/main/java/com/telogaspar/catbreed/core/database/CatBreedsDatabase.kt
package com.telogaspar.catbreed.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.telogaspar.catbreed.core.database.dao.CatBreedDao
import com.telogaspar.catbreed.core.database.dao.FavouriteDao
import com.telogaspar.catbreed.core.database.entity.CatBreedEntity
import com.telogaspar.catbreed.core.database.entity.FavouriteEntity

@Database(
    entities = [CatBreedEntity::class, FavouriteEntity::class],
    version = 3,
    exportSchema = false,
)
abstract class CatBreedsDatabase : RoomDatabase() {
    abstract fun catBreedDao(): CatBreedDao
    abstract fun favouriteDao(): FavouriteDao
}
```

- [ ] **Step 4: Provide `FavouriteDao` in `DatabaseModule`**

Add one `@Provides` function to `DatabaseModule.kt`. The full file after the change:

```kotlin
// core/src/main/java/com/telogaspar/catbreed/core/database/DatabaseModule.kt
package com.telogaspar.catbreed.core.database

import android.content.Context
import androidx.room.Room
import com.telogaspar.catbreed.core.database.dao.CatBreedDao
import com.telogaspar.catbreed.core.database.dao.FavouriteDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideCatBreedsDatabase(@ApplicationContext context: Context): CatBreedsDatabase =
        Room.databaseBuilder(
            context,
            CatBreedsDatabase::class.java,
            "cat_breeds.db",
        ).fallbackToDestructiveMigration(true).build()

    @Provides
    @Singleton
    fun provideCatBreedDao(database: CatBreedsDatabase): CatBreedDao =
        database.catBreedDao()

    @Provides
    @Singleton
    fun provideFavouriteDao(database: CatBreedsDatabase): FavouriteDao =
        database.favouriteDao()
}
```

- [ ] **Step 5: Verify `:core` compiles**

```bash
./gradlew :core:compileDebugKotlin
```

Expected: `BUILD SUCCESSFUL`

- [ ] **Step 6: Commit**

```bash
git add core/src/main/java/com/telogaspar/catbreed/core/database/entity/FavouriteEntity.kt \
        core/src/main/java/com/telogaspar/catbreed/core/database/dao/FavouriteDao.kt \
        core/src/main/java/com/telogaspar/catbreed/core/database/CatBreedsDatabase.kt \
        core/src/main/java/com/telogaspar/catbreed/core/database/DatabaseModule.kt
git commit -m "Add FavouriteEntity, FavouriteDao, and DB wiring to :core"
```

---

## Task 2: Gradle wiring for `:feature:favourites`

**Files:**
- Modify: `feature/favourites/build.gradle.kts`

- [ ] **Step 1: Replace `build.gradle.kts` with full dependency set**

```kotlin
// feature/favourites/build.gradle.kts
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.dagger.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.telogaspar.catbreed.feature.favourites"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        minSdk = 29

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(project(":core"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.android)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    testImplementation(libs.junit)
    testImplementation(kotlin("test-junit"))
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
```

- [ ] **Step 2: Sync and verify module resolves**

```bash
./gradlew :feature:favourites:dependencies --configuration debugRuntimeClasspath | grep -E "core|hilt|coroutines" | head -10
```

Expected: lines showing `:core`, `hilt-android`, `kotlinx-coroutines-android` in the dependency tree.

- [ ] **Step 3: Commit**

```bash
git add feature/favourites/build.gradle.kts
git commit -m "Wire :core, Hilt, and coroutines deps into :feature:favourites"
```

---

## Task 3: `FavouriteRepository` interface and impl

**Files:**
- Create: `feature/favourites/src/main/java/com/telogaspar/catbreed/feature/favourites/domain/FavouriteRepository.kt`
- Create: `feature/favourites/src/main/java/com/telogaspar/catbreed/feature/favourites/data/FavouriteRepositoryImpl.kt`

- [ ] **Step 1: Write the failing tests first**

Create the test file — it won't compile yet because `FavouriteRepository` and `FavouriteRepositoryImpl` don't exist:

```kotlin
// feature/favourites/src/test/java/com/telogaspar/catbreed/feature/favourites/data/FavouriteRepositoryImplTest.kt
package com.telogaspar.catbreed.feature.favourites.data

import com.telogaspar.catbreed.core.database.dao.FavouriteDao
import com.telogaspar.catbreed.core.database.entity.CatBreedEntity
import com.telogaspar.catbreed.core.database.entity.FavouriteEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals

class FavouriteRepositoryImplTest {

    private val dao: FavouriteDao = mockk(relaxed = true)
    private val repository = FavouriteRepositoryImpl(dao)

    @Test
    fun `GIVEN a breedId WHEN addFavourite is called THEN delegates to dao insertFavourite`() = runTest {
        repository.addFavourite("abys")

        coVerify(exactly = 1) { dao.insertFavourite(FavouriteEntity("abys")) }
    }

    @Test
    fun `GIVEN a breedId WHEN removeFavourite is called THEN delegates to dao deleteFavourite`() = runTest {
        repository.removeFavourite("abys")

        coVerify(exactly = 1) { dao.deleteFavourite("abys") }
    }

    @Test
    fun `GIVEN dao emits a list of ids WHEN getFavouriteIds is called THEN returns a set`() = runTest {
        every { dao.getFavouriteIds() } returns flowOf(listOf("abys", "aege", "abys"))

        val result = repository.getFavouriteIds().first()

        assertEquals(setOf("abys", "aege"), result)
    }

    @Test
    fun `GIVEN dao emits favourite breeds WHEN getFavouriteBreeds is called THEN passes them through`() = runTest {
        val entity = CatBreedEntity(
            id = "abys",
            name = "Abyssinian",
            origin = "Egypt",
            temperament = "Active",
            description = "Energetic cat",
            lifeSpan = "14 - 15",
            imageUrl = null,
            weightMetric = "3 - 5",
        )
        every { dao.getFavouriteBreeds() } returns flowOf(listOf(entity))

        val result = repository.getFavouriteBreeds().first()

        assertEquals(listOf(entity), result)
    }
}
```

- [ ] **Step 2: Run tests — expect compile failure**

```bash
./gradlew :feature:favourites:compileDebugUnitTestKotlin 2>&1 | tail -10
```

Expected: error — `FavouriteRepositoryImpl` unresolved reference.

- [ ] **Step 3: Create `FavouriteRepository` interface**

```kotlin
// feature/favourites/src/main/java/com/telogaspar/catbreed/feature/favourites/domain/FavouriteRepository.kt
package com.telogaspar.catbreed.feature.favourites.domain

import com.telogaspar.catbreed.core.database.entity.CatBreedEntity
import kotlinx.coroutines.flow.Flow

interface FavouriteRepository {
    fun getFavouriteBreeds(): Flow<List<CatBreedEntity>>
    fun getFavouriteIds(): Flow<Set<String>>
    suspend fun addFavourite(breedId: String)
    suspend fun removeFavourite(breedId: String)
}
```

- [ ] **Step 4: Create `FavouriteRepositoryImpl`**

```kotlin
// feature/favourites/src/main/java/com/telogaspar/catbreed/feature/favourites/data/FavouriteRepositoryImpl.kt
package com.telogaspar.catbreed.feature.favourites.data

import com.telogaspar.catbreed.core.database.dao.FavouriteDao
import com.telogaspar.catbreed.core.database.entity.CatBreedEntity
import com.telogaspar.catbreed.core.database.entity.FavouriteEntity
import com.telogaspar.catbreed.feature.favourites.domain.FavouriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FavouriteRepositoryImpl @Inject constructor(
    private val dao: FavouriteDao,
) : FavouriteRepository {

    override fun getFavouriteBreeds(): Flow<List<CatBreedEntity>> =
        dao.getFavouriteBreeds()

    override fun getFavouriteIds(): Flow<Set<String>> =
        dao.getFavouriteIds().map { it.toSet() }

    override suspend fun addFavourite(breedId: String) {
        dao.insertFavourite(FavouriteEntity(breedId))
    }

    override suspend fun removeFavourite(breedId: String) {
        dao.deleteFavourite(breedId)
    }
}
```

- [ ] **Step 5: Run tests — expect all pass**

```bash
./gradlew :feature:favourites:testDebugUnitTest
```

Expected: `4 tests completed, 0 failures`

- [ ] **Step 6: Commit**

```bash
git add feature/favourites/src/main/java/com/telogaspar/catbreed/feature/favourites/domain/FavouriteRepository.kt \
        feature/favourites/src/main/java/com/telogaspar/catbreed/feature/favourites/data/FavouriteRepositoryImpl.kt \
        feature/favourites/src/test/java/com/telogaspar/catbreed/feature/favourites/data/FavouriteRepositoryImplTest.kt
git commit -m "Add FavouriteRepository interface and impl with unit tests"
```

---

## Task 4: Hilt module to bind `FavouriteRepository`

**Files:**
- Create: `feature/favourites/src/main/java/com/telogaspar/catbreed/feature/favourites/di/FavouritesModule.kt`

- [ ] **Step 1: Create `FavouritesModule`**

```kotlin
// feature/favourites/src/main/java/com/telogaspar/catbreed/feature/favourites/di/FavouritesModule.kt
package com.telogaspar.catbreed.feature.favourites.di

import com.telogaspar.catbreed.feature.favourites.data.FavouriteRepositoryImpl
import com.telogaspar.catbreed.feature.favourites.domain.FavouriteRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FavouritesModule {

    @Binds
    @Singleton
    abstract fun bindFavouriteRepository(impl: FavouriteRepositoryImpl): FavouriteRepository
}
```

- [ ] **Step 2: Verify full module compiles**

```bash
./gradlew :feature:favourites:compileDebugKotlin
```

Expected: `BUILD SUCCESSFUL`

- [ ] **Step 3: Verify full project compiles**

```bash
./gradlew compileDebugKotlin
```

Expected: `BUILD SUCCESSFUL`

- [ ] **Step 4: Commit**

```bash
git add feature/favourites/src/main/java/com/telogaspar/catbreed/feature/favourites/di/FavouritesModule.kt
git commit -m "Add FavouritesModule Hilt binding for FavouriteRepository"
```
