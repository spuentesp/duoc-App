package com.example.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.data.PokemonRepository
import kotlinx.coroutines.launch

class PokemonViewModel(
    private val repository: PokemonRepository = PokemonRepository()
) : ViewModel() {

    var listState by mutableStateOf(PokemonListUiState())
        private set

    var detailState by mutableStateOf(PokemonDetailUiState())
        private set

    fun loadList(limit: Int = 20, offset: Int = 0) {
        listState = listState.copy(
            isLoading = true,
            error = null,
            limit = limit,
            offset = offset
        )
        viewModelScope.launch {
            try {
                val result = repository.getPokemonList(limit, offset)
                listState = listState.copy(
                    pokemons = result.results,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                listState = listState.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun loadDetail(nameOrId: String) {
        detailState = detailState.copy(
            isLoading = true,
            error = null,
            species = null,
            speciesError = null
        )
        viewModelScope.launch {
            try {
                val result = repository.getPokemonDetail(nameOrId)
                detailState = detailState.copy(detail = result)
                try {
                    val speciesResult = repository.getPokemonSpecies(nameOrId)
                    detailState = detailState.copy(species = speciesResult)
                } catch (speciesError: Exception) {
                    detailState = detailState.copy(speciesError = speciesError.message)
                }
                detailState = detailState.copy(isLoading = false)
            } catch (e: Exception) {
                detailState = detailState.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
}
