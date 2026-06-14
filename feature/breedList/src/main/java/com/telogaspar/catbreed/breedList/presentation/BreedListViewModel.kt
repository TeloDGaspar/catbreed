package com.telogaspar.catbreed.breedList.presentation

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

data class BreedListUiState(
    val allBreeds: List<Breed> = emptyList(),
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val isLastPage: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
) {
    val filteredBreeds: List<Breed>
        get() = if (searchQuery.isBlank()) allBreeds
        else allBreeds.filter {
            it.breedName.contains(searchQuery, ignoreCase = true) ||
                it.origin?.contains(searchQuery, ignoreCase = true) == true
        }
}

@HiltViewModel
class BreedListViewModel @Inject constructor(
    private val repository: BreedListRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(BreedListUiState())
    val uiState = _uiState.asStateFlow()

    private var currentPage = 0
    private val pageSize = 15

    init {
        loadBreeds()
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun loadNextPage() {
        val state = _uiState.value
        if (state.isLoadingMore || state.isLastPage || state.isLoading || state.searchQuery.isNotBlank()) return
        currentPage++
        loadBreeds(isLoadingMore = true)
    }

    fun retry() {
        currentPage = 0
        _uiState.update { it.copy(error = null, allBreeds = emptyList()) }
        loadBreeds()
    }

    private fun loadBreeds(isLoadingMore: Boolean = false) {
        viewModelScope.launch {
            _uiState.update {
                if (isLoadingMore) it.copy(isLoadingMore = true, error = null)
                else it.copy(isLoading = true, error = null)
            }
            repository.fetchBreedList(page = currentPage, limit = pageSize)
                .catch { e ->
                    _uiState.update {
                        it.copy(isLoading = false, isLoadingMore = false, error = e.message)
                    }
                }
                .collect { newBreeds ->
                    _uiState.update {
                        it.copy(
                            allBreeds = if (isLoadingMore) it.allBreeds + newBreeds else newBreeds,
                            isLoading = false,
                            isLoadingMore = false,
                            isLastPage = newBreeds.size < pageSize,
                            error = null,
                        )
                    }
                }
        }
    }
}
