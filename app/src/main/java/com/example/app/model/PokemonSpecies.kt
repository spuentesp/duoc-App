package com.example.app.model

import com.google.gson.annotations.SerializedName

/**
 * Modelo que complementa la información de [PokemonDetail] con datos narrativos
 * y biológicos. Esta respuesta proviene del endpoint /pokemon-species/{nameOrId}.
 */
data class PokemonSpecies(
    @SerializedName("base_happiness")
    val baseHappiness: Int?, // valor entre 0-140 que indica la afinidad base del Pokémon
    @SerializedName("capture_rate")
    val captureRate: Int?, // número que aproxima qué tan fácil es capturarlo (cuanto mayor, mejor)
    val color: NamedApiResource?, // color asignado dentro de la Pokédex (no siempre coincide con la ilustración)
    @SerializedName("evolves_from_species")
    val evolvesFromSpecies: NamedApiResource?, // especie previa en la cadena evolutiva (puede ser null)
    @SerializedName("flavor_text_entries")
    val flavorTextEntries: List<FlavorTextEntry>, // textos narrativos por idioma y versión del juego
    val genera: List<GenusEntry>, // descripciones cortas del tipo "Royal Sword Pokémon"
    @SerializedName("growth_rate")
    val growthRate: NamedApiResource?, // curva de experiencia, útil para estadísticas avanzadas
    val habitat: NamedApiResource?, // hábitat donde habita el Pokémon (antiguos sí lo tienen)
    val shape: NamedApiResource? // forma general (blob, humanoide, etc.)
)

/**
 * Texto narrativo que describe al Pokémon en distintos idiomas/versiones.
 */
data class FlavorTextEntry(
    @SerializedName("flavor_text")
    val flavorText: String,
    val language: NamedApiResource,
    val version: NamedApiResource?
)

/**
 * Entrada que describe la "especie" textual (ej. Seed Pokémon).
 */
data class GenusEntry(
    val genus: String,
    val language: NamedApiResource
)

/**
 * Recurso genérico reutilizado por la API para enlazar otras entidades.
 */
data class NamedApiResource(
    val name: String,
    val url: String
)
