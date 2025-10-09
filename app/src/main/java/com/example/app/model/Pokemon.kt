package com.example.app.model

import com.google.gson.annotations.SerializedName

data class BasicPokemon(
    val name: String,
    val url: String
)

data class PokemonListResponse(
    val results: List<BasicPokemon>
)

data class PokemonTypeEntry(
    val slot: Int,
    val type: TypeInfo
)

data class TypeInfo(
    val name: String
)

data class PokemonDetail(
    val id: Int,
    val name: String,
    val types: List<PokemonTypeEntry>,
    val sprites: Sprites
)

data class Sprites(
    @SerializedName("front_default")
    val frontDefault: String?
)



