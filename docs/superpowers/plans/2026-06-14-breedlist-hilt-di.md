# BreedList Hilt DI Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Wire Hilt dependency injection into the `feature/breedList` module so its classes are injected automatically from the app's DI graph.

**Architecture:** Add the Hilt + KSP plugins and runtime to `breedList/build.gradle.kts`, annotate the three implementation classes with `@Inject`, then create a `BreedListModule` that provides `BreedsEventApi` (from the shared `Retrofit`) and binds the two interfaces to their implementations.

**Tech Stack:** Hilt 2.59.2, KSP 2.2.10-2.0.2, Retrofit (provided by `:core`)

---

## File Map

| Action | Path |
|--------|------|
| Modify | `feature/breedList/build.gradle.kts` |
| Modify | `feature/breedList/src/main/java/com/telogaspar/catbreed/breedList/data/mapper/EventMapper.kt` |
| Modify | `feature/breedList/src/main/java/com/telogaspar/catbreed/breedList/data/remote/BreedEventListRemoteDataSourceImpl.kt` |
| Modify | `feature/breedList/src/main/java/com/telogaspar/catbreed/breedList/data/repository/BrededListRepositoryImpl.kt` |
| Create | `feature/breedList/src/main/java/com/telogaspar/catbreed/breedList/di/BreedListModule.kt` |

---

## Task 1: Add Hilt + KSP to the breedList build file

**Files:**
- Modify: `feature/breedList/build.gradle.kts`

- [ ] **Step 1: Add the plugins block entries**

Replace the current `plugins` block:

```kotlin
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.dagger.hilt)
    alias(libs.plugins.ksp)
}
```

- [ ] **Step 2: Add Hilt dependencies**

Add inside the `dependencies` block (after the existing entries):

```kotlin
    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
```

- [ ] **Step 3: Verify the project syncs**

Run:
```
./gradlew :feature:breedList:assembleDebug
```
Expected: BUILD SUCCESSFUL (no KSP or Hilt classpath errors).

- [ ] **Step 4: Commit**

```bash
git add feature/breedList/build.gradle.kts
git commit -m "build(breedList): add Hilt and KSP plugins and dependencies"
```

---

## Task 2: Add @Inject to EventMapper

**Files:**
- Modify: `feature/breedList/src/main/java/com/telogaspar/catbreed/breedList/data/mapper/EventMapper.kt`

- [ ] **Step 1: Verify the existing mapper test still compiles and passes**

Run:
```
./gradlew :feature:breedList:testDebugUnitTest --tests "com.telogaspar.catbreed.breedList.data.mapper.EventMapperTest"
```
Expected: all 5 tests pass (baseline before modification).

- [ ] **Step 2: Add @Inject constructor**

Replace the class declaration:

```kotlin
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
                imageUrl = item.image?.url ?: ""
            )
        }
    }
}
```

- [ ] **Step 3: Run the mapper tests again to confirm nothing broke**

Run:
```
./gradlew :feature:breedList:testDebugUnitTest --tests "com.telogaspar.catbreed.breedList.data.mapper.EventMapperTest"
```
Expected: all 5 tests still pass.

- [ ] **Step 4: Commit**

```bash
git add feature/breedList/src/main/java/com/telogaspar/catbreed/breedList/data/mapper/EventMapper.kt
git commit -m "feat(breedList): add @Inject constructor to EventMapper"
```

---

## Task 3: Add @Inject to BreedEventListRemoteDataSourceImpl

**Files:**
- Modify: `feature/breedList/src/main/java/com/telogaspar/catbreed/breedList/data/remote/BreedEventListRemoteDataSourceImpl.kt`

- [ ] **Step 1: Verify the existing remote data source test passes**

Run:
```
./gradlew :feature:breedList:testDebugUnitTest --tests "com.telogaspar.catbreed.breedList.data.remote.BreedEventListRemoteDataSourceImplTest"
```
Expected: all 3 tests pass.

- [ ] **Step 2: Add @Inject constructor**

```kotlin
package com.telogaspar.catbreed.breedList.data.remote

import com.telogaspar.catbreed.breedList.data.api.BreedsEventApi
import com.telogaspar.catbreed.breedList.data.model.BreedsResponse
import javax.inject.Inject

internal class BreedEventListRemoteDataSourceImpl @Inject constructor(
    private val breedsEventApi: BreedsEventApi
) : BreedEventListRemoteDataSource {
    override suspend fun fetchBreedList(): List<BreedsResponse> {
        return breedsEventApi.getBreeds()
    }
}
```

- [ ] **Step 3: Run the remote data source tests again**

Run:
```
./gradlew :feature:breedList:testDebugUnitTest --tests "com.telogaspar.catbreed.breedList.data.remote.BreedEventListRemoteDataSourceImplTest"
```
Expected: all 3 tests still pass.

- [ ] **Step 4: Commit**

```bash
git add feature/breedList/src/main/java/com/telogaspar/catbreed/breedList/data/remote/BreedEventListRemoteDataSourceImpl.kt
git commit -m "feat(breedList): add @Inject constructor to BreedEventListRemoteDataSourceImpl"
```

---

## Task 4: Add @Inject to BreedListRepositoryImpl

**Files:**
- Modify: `feature/breedList/src/main/java/com/telogaspar/catbreed/breedList/data/repository/BrededListRepositoryImpl.kt`

- [ ] **Step 1: Verify the existing repository test passes**

Run:
```
./gradlew :feature:breedList:testDebugUnitTest --tests "com.telogaspar.catbreed.breedList.data.repository.BreedListRepositoryImplTest"
```
Expected: all 3 tests pass.

- [ ] **Step 2: Add @Inject constructor**

```kotlin
package com.telogaspar.catbreed.breedList.data.repository

import com.telogaspar.catbreed.breedList.data.mapper.EventMapper
import com.telogaspar.catbreed.breedList.data.remote.BreedEventListRemoteDataSource
import com.telogaspar.catbreed.breedList.domain.Breed
import com.telogaspar.catbreed.breedList.domain.BreedListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class BreedListRepositoryImpl @Inject constructor(
    private val remoteDataSource: BreedEventListRemoteDataSource,
    private val mapper: EventMapper,
) : BreedListRepository {

    override fun fetchBreedList(): Flow<List<Breed>> = flow {
        try {
            val sportsResponse = remoteDataSource.fetchBreedList()
            if (sportsResponse.isNotEmpty()) {
                val mappedSports = mapper.map(sportsResponse)
                emit(mappedSports)
                return@flow
            }
        } catch (e: Exception) {

        }
    }
}
```

- [ ] **Step 3: Run the repository tests again**

Run:
```
./gradlew :feature:breedList:testDebugUnitTest --tests "com.telogaspar.catbreed.breedList.data.repository.BreedListRepositoryImplTest"
```
Expected: all 3 tests still pass.

- [ ] **Step 4: Commit**

```bash
git add feature/breedList/src/main/java/com/telogaspar/catbreed/breedList/data/repository/BrededListRepositoryImpl.kt
git commit -m "feat(breedList): add @Inject constructor to BreedListRepositoryImpl"
```

---

## Task 5: Create BreedListModule

**Files:**
- Create: `feature/breedList/src/main/java/com/telogaspar/catbreed/breedList/di/BreedListModule.kt`

- [ ] **Step 1: Create the Hilt module**

```kotlin
package com.telogaspar.catbreed.breedList.di

import com.telogaspar.catbreed.breedList.data.api.BreedsEventApi
import com.telogaspar.catbreed.breedList.data.remote.BreedEventListRemoteDataSource
import com.telogaspar.catbreed.breedList.data.remote.BreedEventListRemoteDataSourceImpl
import com.telogaspar.catbreed.breedList.data.repository.BreedListRepositoryImpl
import com.telogaspar.catbreed.breedList.domain.BreedListRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class BreedListModule {

    @Binds
    @Singleton
    internal abstract fun bindBreedEventListRemoteDataSource(
        impl: BreedEventListRemoteDataSourceImpl
    ): BreedEventListRemoteDataSource

    @Binds
    @Singleton
    internal abstract fun bindBreedListRepository(
        impl: BreedListRepositoryImpl
    ): BreedListRepository

    companion object {
        @Provides
        @Singleton
        internal fun provideBreedsEventApi(retrofit: Retrofit): BreedsEventApi =
            retrofit.create(BreedsEventApi::class.java)
    }
}
```

- [ ] **Step 2: Build the module to verify KSP generates the Hilt component correctly**

Run:
```
./gradlew :feature:breedList:assembleDebug
```
Expected: BUILD SUCCESSFUL with no Hilt or KSP errors.

- [ ] **Step 3: Run all breedList unit tests to confirm nothing regressed**

Run:
```
./gradlew :feature:breedList:testDebugUnitTest
```
Expected: all 11 tests pass.

- [ ] **Step 4: Commit**

```bash
git add feature/breedList/src/main/java/com/telogaspar/catbreed/breedList/di/BreedListModule.kt
git commit -m "feat(breedList): add BreedListModule to wire Hilt DI graph"
```
