# Cat Breeds

A small Android app that browses cat breeds from [TheCatAPI](https://thecatapi.com/),
lets you favourite them, and shows breed details — built with Kotlin, Jetpack Compose,
and a modular Clean Architecture setup.

This document explains the **architectural decisions, trade-offs, and strategies**
applied during development.

---

## Module structure

```
:app                  Composition root — navigation, theme wiring, Hilt entry point
:core                 Shared infra: networking, Room DB, theme, cross-feature contracts
:feature:breedList    Breed list + detail (data / domain / presentation)
:feature:favourites   Favourites (data / domain / presentation)
```

**Why modular:** features are independently buildable and testable, `:core` holds only
what is genuinely shared, and feature code is kept `internal`.
Each feature follows a `data  domain presentation` split.

**Cross-feature decoupling:** `breedList` needs favourite state but must *not* depend on
the `favourites` module. The contract (`FavouriteInteractor`) lives in `:core`; `favourites`
provides the implementation. Features collaborate through a shared abstraction, not a
direct dependency.

## Strategies

- **Offline-first list:** `BreedListRepositoryImpl` fetches from the network, caches into
  Room, and emits. On failure it falls back to the cached page; only an empty cache surfaces
  an error. Domain models (not Room entities) cross the repository boundary.
- **Resilient networking:** a `RetryInterceptor` retries transient failures with
  backoff, and an `ApiKeyInterceptor` injects the key — redacted from logs, and omitted
  entirely when no key is configured so the app still runs.
- **API key handling:** read from `local.properties` a `CAT_API_KEY`
- **Typed errors:** a sealed `BreedException` (`Network` / `EmptyResult` / `NotFound`) gives
  the UI meaningful, distinguishable failure states.

---

## Trade-offs (deliberate, given the scope)

- **Destructive DB migration** (`fallbackToDestructiveMigration`) — acceptable for a cache;
  there is no user-authored data to preserve.
- **Detail screen reads from cache only** — avoids an extra network round-trip since the user
  always arrives from the (cached) list. Throws `NotFound` if the cache is cold.
- **Client-side search** over already-loaded pages, and **pagination paused while searching** —
  simpler UX for a bounded dataset; a server-side search endpoint would be the next step.
- **Image URLs assume `.jpg`** when only a reference id is available — avoids one network call
  per breed; Coil falls back to an initials avatar on a 404.

---

## Testing

- **Unit tests** (JUnit + MockK + `coroutines-test`): repositories (incl. cache-fallback
  branches), ViewModels, mappers, data sources.
- **Room integration tests** (in-memory DB) for the favourites DAO/repository, covering the
  breeds↔favourites JOIN.
- **Compose UI tests** for the list, error, and empty states.
- **Live-API tests** exist but are `@Ignore`d so CI stays fast and offline-friendly; run them
  manually to verify the real contract.

Naming follows a `GIVEN / WHEN / THEN` convention throughout.

---

## Running

Add your TheCatAPI key to `local.properties` (the app also runs without one):

```properties
CAT_API_KEY=your_key_here
```

Then build and run the `app` module from Android Studio, or `./gradlew :app:installDebug`.
