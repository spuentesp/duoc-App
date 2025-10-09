package com.example.app.data.remote

import com.example.app.model.PokemonDetail
import com.example.app.model.PokemonListResponse
import com.example.app.model.PokemonSpecies
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokemonApiService {

    @GET("pokemon")
    suspend fun fetchPokemonList(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): PokemonListResponse

    @GET("pokemon/{nameOrId}")
    suspend fun fetchPokemonDetail(
        @Path("nameOrId") nameOrId: String
    ): PokemonDetail

    @GET("pokemon-species/{nameOrId}")
    suspend fun fetchPokemonSpecies(
        @Path("nameOrId") nameOrId: String
    ): PokemonSpecies
}
