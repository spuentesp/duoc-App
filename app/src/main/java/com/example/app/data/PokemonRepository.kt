package com.example.app.data

import com.example.app.data.remote.NetworkModule
import com.example.app.data.remote.PokemonApiService
import com.example.app.model.PokemonDetail
import com.example.app.model.PokemonListResponse
import com.example.app.model.PokemonSpecies

class PokemonRepository(
    private val api: PokemonApiService = NetworkModule.api
) {

    suspend fun getPokemonList(limit: Int = 20, offset: Int = 0): PokemonListResponse {
        return api.fetchPokemonList(limit, offset)
    }

    suspend fun getPokemonDetail(nameOrId: String): PokemonDetail {
        return api.fetchPokemonDetail(nameOrId)
    }

    suspend fun getPokemonSpecies(nameOrId: String): PokemonSpecies {
        return api.fetchPokemonSpecies(nameOrId)
    }
}
