package com.example.app.viewmodel

import com.example.app.model.BasicPokemon
import com.example.app.model.PokemonDetail
import com.example.app.model.PokemonSpecies

data class PokemonListUiState(
    val pokemons: List<BasicPokemon> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val limit: Int = 20,
    val offset: Int = 0
)

data class PokemonDetailUiState(
    val detail: PokemonDetail? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val species: PokemonSpecies? = null,
    val speciesError: String? = null
)
