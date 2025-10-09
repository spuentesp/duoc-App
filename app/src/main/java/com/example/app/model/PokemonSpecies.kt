package com.example.app.model

import com.google.gson.annotations.SerializedName

data class PokemonSpecies(
    @SerializedName("base_happiness")
    val baseHappiness: Int?,
    @SerializedName("capture_rate")
    val captureRate: Int?,
    val color: NamedApiResource?,
    @SerializedName("evolves_from_species")
    val evolvesFromSpecies: NamedApiResource?,
    @SerializedName("flavor_text_entries")
    val flavorTextEntries: List<FlavorTextEntry>,
    val genera: List<GenusEntry>,
    @SerializedName("growth_rate")
    val growthRate: NamedApiResource?,
    val habitat: NamedApiResource?,
    val shape: NamedApiResource?
)

data class FlavorTextEntry(
    @SerializedName("flavor_text")
    val flavorText: String,
    val language: NamedApiResource,
    val version: NamedApiResource?
)

data class GenusEntry(
    val genus: String,
    val language: NamedApiResource
)

data class NamedApiResource(
    val name: String,
    val url: String
)
