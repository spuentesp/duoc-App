package com.example.app.data

import com.example.app.data.remote.NetworkModule
import com.example.app.data.remote.PokemonApiService
import com.example.app.model.PokemonDetail
import com.example.app.model.PokemonListResponse

class PokemonRepository(
    private val api: PokemonApiService = NetworkModule.api
) {

    suspend fun getPokemonList(limit: Int = 20): PokemonListResponse {
        return api.fetchPokemonList(limit)
    }

    suspend fun getPokemonDetail(nameOrId: String): PokemonDetail {
        return api.fetchPokemonDetail(nameOrId)
    }
}
