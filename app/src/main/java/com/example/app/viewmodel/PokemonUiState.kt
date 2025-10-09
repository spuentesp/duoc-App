package com.example.app.viewmodel

import com.example.app.model.BasicPokemon
import com.example.app.model.PokemonDetail

data class PokemonListUiState(
    val pokemons: List<BasicPokemon> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class PokemonDetailUiState(
    val detail: PokemonDetail? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
