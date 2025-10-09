package com.example.app.model

import com.google.gson.annotations.SerializedName

/**
 * Entrada mínima que entrega la API cuando consultamos /pokemon.
 * Incluye solo el nombre y una URL con más información del Pokémon.
 */
data class BasicPokemon(
    val name: String,
    val url: String // Ej.: https://pokeapi.co/api/v2/pokemon/25/ → usamos el segmento final para obtener el ID
)

/**
 * Contenedor de la respuesta para la lista de Pokémon.
 * PokeAPI coloca el arreglo bajo la clave "results", por eso lo modelamos explícitamente.
 */
data class PokemonListResponse(
    val results: List<BasicPokemon>
)

/**
 * Describe un tipo asociado al Pokémon dentro del detalle general.
 * slot indica la posición (tipo primario, secundario, etc.).
 */
data class PokemonTypeEntry(
    val slot: Int,
    val type: TypeInfo
)

/**
 * Objeto interno que solo necesitamos para acceder al nombre del tipo.
 */
data class TypeInfo(
    val name: String
)

/**
 * Representa la respuesta principal de /pokemon/{nameOrId}.
 * Aquí no vienen descripciones ni evolución; eso existe en /pokemon-species.
 */
data class PokemonDetail(
    val id: Int,
    val name: String,
    val types: List<PokemonTypeEntry>,
    val sprites: Sprites
)

/**
 * Estructura de sprites que entrega la API.
 * Usamos SerializedName porque el JSON usa snake_case y no coincide con la convención de Kotlin.
 */
data class Sprites(
    @SerializedName("front_default")
    val frontDefault: String? // Algunos Pokémon especiales no tienen sprite frontal, por eso lo marcamos nullable.
)
