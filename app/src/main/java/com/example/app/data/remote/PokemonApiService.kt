package com.example.app.data.remote

import com.example.app.model.PokemonDetail
import com.example.app.model.PokemonListResponse
import com.example.app.model.PokemonSpecies
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Declaración de los endpoints que usamos de PokeAPI.
 * Retrofit genera la implementación concreta en base a esta interfaz.
 */
interface PokemonApiService {

    /**
     * Obtiene la lista paginada de Pokémon. limit controla cuántos elementos trae
     * la respuesta y offset permite saltar resultados (similar a una página).
     */
    @GET("pokemon")
    suspend fun fetchPokemonList(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): PokemonListResponse

    /**
     * Recupera el detalle principal. PokeAPI acepta tanto el nombre ("pikachu")
     * como el identificador numérico ("25") en el mismo endpoint.
     */
    @GET("pokemon/{nameOrId}")
    suspend fun fetchPokemonDetail(
        @Path("nameOrId") nameOrId: String
    ): PokemonDetail

    /**
     * Endpoint complementario que provee flavor text, evolución y datos de especie.
     * Lo llamamos en paralelo después de obtener el detalle principal.
     */
    @GET("pokemon-species/{nameOrId}")
    suspend fun fetchPokemonSpecies(
        @Path("nameOrId") nameOrId: String
    ): PokemonSpecies
}
