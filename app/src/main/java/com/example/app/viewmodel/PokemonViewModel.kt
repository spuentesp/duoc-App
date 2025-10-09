package com.example.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.data.PokemonRepository
import kotlinx.coroutines.launch

/**
 * ViewModel que orquesta el flujo de datos entre el repositorio y la capa de UI.
 * Mantiene dos estados independientes: uno para la lista y otro para el detalle
 * (con su información complementaria de species).
 */
class PokemonViewModel(
    private val repository: PokemonRepository = PokemonRepository()
) : ViewModel() {

    /**
     * Estado observable por la pantalla de exploración. La UI se recompone cada vez
     * que asignamos un nuevo valor gracias a mutableStateOf.
     */
    var listState by mutableStateOf(PokemonListUiState())
        private set

    /**
     * Estado del panel de detalle que se muestra tanto en la pantalla principal
     * como en la pantalla dedicada de detalle.
     */
    var detailState by mutableStateOf(PokemonDetailUiState())
        private set

    /**
     * Carga la Pokédex consultando el repositorio. Se ejecuta en una corrutina
     * para no bloquear el hilo principal. limit y offset permiten paginar.
     */
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
                // No mostramos un stacktrace en la UI, sólo el mensaje amigable.
                listState = listState.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    /**
     * Carga el detalle base y, en paralelo, intenta obtener la información de species.
     * Si la consulta de species falla, seguimos mostrando el detalle principal.
     */
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

                // Intentamos obtener información adicional (flavor text, evolución, etc.).
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
