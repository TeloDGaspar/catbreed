package com.telogaspar.catbreed.breedList.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telogaspar.catbreed.breedList.domain.Breed
import com.telogaspar.catbreed.breedList.domain.BreedListRepository
import com.telogaspar.catbreed.core.repository.FavouriteInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BreedDetailUiState(
    val breed: Breed? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val isFavourite: Boolean = false,
)

@HiltViewModel
class BreedDetailViewModel @Inject constructor(
    private val repository: BreedListRepository,
    private val favouriteInteractor: FavouriteInteractor,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val breedId: String = checkNotNull(savedStateHandle["breedId"])

    private val _uiState = MutableStateFlow(BreedDetailUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadBreed()
        observeFavouriteState()
    }

    fun toggleFavourite() {
        viewModelScope.launch {
            if (_uiState.value.isFavourite) {
                favouriteInteractor.removeFavourite(breedId)
            } else {
                favouriteInteractor.addFavourite(breedId)
            }
        }
    }

    private fun loadBreed() {
        viewModelScope.launch {
            repository.fetchBreedById(breedId)
                .catch { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
                .collect { breed -> _uiState.update { it.copy(breed = breed, isLoading = false) } }
        }
    }

    private fun observeFavouriteState() {
        viewModelScope.launch {
            favouriteInteractor.getFavouriteIds()
                .map { ids -> breedId in ids }
                .collect { isFavourite -> _uiState.update { it.copy(isFavourite = isFavourite) } }
        }
    }
}
