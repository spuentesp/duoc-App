package com.example.app.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Objeto que expone una única instancia de Retrofit configurada para consumir PokeAPI.
 * De esta forma todos los colaboradores (repositorios, pruebas) reutilizan la misma
 * configuración base sin duplicar código.
 */
object NetworkModule {

    private const val BASE_URL = "https://pokeapi.co/api/v2/"

    /**
     * Retrofit se crea de forma lazy para no inicializar la red hasta que realmente
     * se necesite. GsonConverterFactory se encarga de mapear JSON ↔ data class.
     */
    val api: PokemonApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PokemonApiService::class.java)
    }
}
