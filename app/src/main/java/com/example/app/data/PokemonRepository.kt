package com.example.app.data

import com.example.app.data.remote.NetworkModule
import com.example.app.data.remote.PokemonApiService
import com.example.app.model.PokemonDetail
import com.example.app.model.PokemonListResponse
import com.example.app.model.PokemonSpecies

/**
 * Encapsula el acceso a la red para que el resto de capas desconozcan
 * si los datos provienen de la web, de un caché o de una base local.
 * Aquí es donde podríamos combinar múltiples fuentes sin cambiar la UI.
 */
class PokemonRepository(
    private val api: PokemonApiService = NetworkModule.api
) {

    /**
     * Devuelve la lista paginada de Pokémon. limit y offset se exponen
     * hacia arriba para que la UI pueda recrear la experiencia de la Pokédex.
     */
    suspend fun getPokemonList(limit: Int = 20, offset: Int = 0): PokemonListResponse {
        return api.fetchPokemonList(limit, offset)
    }

    /**
     * Consulta el detalle base de un Pokémon (stats, tipos, sprite frontal).
     */
    suspend fun getPokemonDetail(nameOrId: String): PokemonDetail {
        return api.fetchPokemonDetail(nameOrId)
    }

    /**
     * Recupera la información narrativa (flavor text) y datos adicionales
     * como captura, evolución previa y especie textual.
     */
    suspend fun getPokemonSpecies(nameOrId: String): PokemonSpecies {
        return api.fetchPokemonSpecies(nameOrId)
    }
}
