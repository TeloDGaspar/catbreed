package com.telogaspar.catbreed.breedList.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telogaspar.catbreed.breedList.domain.Breed
import com.telogaspar.catbreed.breedList.domain.BreedListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BreedDetailUiState(
    val breed: Breed? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
)

@HiltViewModel
class BreedDetailViewModel @Inject constructor(
    private val repository: BreedListRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val breedId: String = checkNotNull(savedStateHandle["breedId"])

    private val _uiState = MutableStateFlow(BreedDetailUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadBreed()
    }

    private fun loadBreed() {
        viewModelScope.launch {
            repository.fetchBreedById(breedId)
                .catch { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
                .collect { breed -> _uiState.update { it.copy(breed = breed, isLoading = false) } }
        }
    }
}
