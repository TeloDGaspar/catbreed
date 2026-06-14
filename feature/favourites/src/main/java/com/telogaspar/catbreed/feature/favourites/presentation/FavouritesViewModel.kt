package com.telogaspar.catbreed.feature.favourites.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telogaspar.catbreed.core.database.entity.CatBreedEntity
import com.telogaspar.catbreed.feature.favourites.domain.FavouriteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FavouritesUiState(
    val breeds: List<CatBreedEntity> = emptyList(),
    val averageLifespan: Double? = null,
)

@HiltViewModel
class FavouritesViewModel @Inject constructor(
    private val repository: FavouriteRepository,
) : ViewModel() {

    val uiState = repository.getFavouriteBreeds()
        .map { breeds ->
            FavouritesUiState(
                breeds = breeds,
                averageLifespan = breeds.averageLifespan(),
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = FavouritesUiState(),
        )

    fun removeFavourite(breedId: String) {
        viewModelScope.launch { repository.removeFavourite(breedId) }
    }

    private fun List<CatBreedEntity>.averageLifespan(): Double? {
        if (isEmpty()) return null
        val parsed = mapNotNull { entity ->
            entity.lifeSpan
                .substringBefore("-")
                .trim()
                .toDoubleOrNull()
        }
        return if (parsed.isEmpty()) null else parsed.average()
    }
}
