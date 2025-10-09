package com.example.app.model

import com.google.gson.annotations.SerializedName;



data class Pokemon(val id: Int,
                   val name: String,
                   val sprites: PokemonSprites,
                   val types: List<PokemonTypeEntry>,
                   val stats: List<PokemonStatEntry>)


data class PokemonSprites(
    // @serializedname se usa si el nombre del campo en json es diferente al de la variable
    @SerializedName("front_default")
    val frontDefault: String
)

data class PokemonTypeEntry(
    var slot: Int,
    val Type: PokemonType
)

data class PokemonType(
    val name: String
)

data class PokemonStatEntry(
    @SerializedName("base_stat")
    val baseStat: Int,
    val stat: PokemonStat
)

data class PokemonStat(
    val name:String
)




