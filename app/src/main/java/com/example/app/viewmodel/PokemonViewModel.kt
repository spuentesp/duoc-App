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

    fun loadList(limit: Int = 20) {
        listState = listState.copy(isLoading = true, error = null)
        viewModelScope.launch {
            try {
                val result = repository.getPokemonList(limit)
                listState = listState.copy(
                    pokemons = result.results,
                    isLoading = false
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
        detailState = detailState.copy(isLoading = true, error = null)
        viewModelScope.launch {
            try {
                val result = repository.getPokemonDetail(nameOrId)
                detailState = detailState.copy(
                    detail = result,
                    isLoading = false
                )
            } catch (e: Exception) {
                detailState = detailState.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
}
