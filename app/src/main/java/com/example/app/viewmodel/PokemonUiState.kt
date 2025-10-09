package com.example.app.viewmodel

import com.example.app.model.BasicPokemon
import com.example.app.model.PokemonDetail
import com.example.app.model.PokemonSpecies

/**
 * Estado expuesto a la UI para la pantalla de exploración/lista.
 * Incluye la colección de Pokémon, indicadores de carga/errores y
 * los parámetros usados en la última consulta para poder mostrarlos o reutilizarlos.
 */
data class PokemonListUiState(
    val pokemons: List<BasicPokemon> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val limit: Int = 20,
    val offset: Int = 0
)

/**
 * Estado específico del panel de detalle. Separamos la información principal
 * del resultado de /pokemon y la información narrativa de /pokemon-species
 * para manejar errores de manera independiente.
 */
data class PokemonDetailUiState(
    val detail: PokemonDetail? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val species: PokemonSpecies? = null,
    val speciesError: String? = null
)
