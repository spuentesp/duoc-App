package com.example.app.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.app.viewmodel.PokemonViewModel

@Composable
fun PokemonListScreen(
    onPokemonClick: (String) -> Unit,
    viewModel: PokemonViewModel = viewModel()
) {
    val state = viewModel.listState

    LaunchedEffect(Unit) {
        viewModel.loadList()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Lista de Pokémon", fontSize = 24.sp)

        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        state.error?.let { error ->
            Text(text = "Error: $error", color = Color.Red)
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(state.pokemons) { pokemon ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { onPokemonClick(pokemon.name) }
                ) {
                    Text(
                        text = pokemon.name.replaceFirstChar { it.uppercase() },
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PokemonDetailScreen(
    pokemonName: String,
    viewModel: PokemonViewModel = viewModel()
) {
    val state = viewModel.detailState

    LaunchedEffect(pokemonName) {
        viewModel.loadDetail(pokemonName)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (state.isLoading) {
            CircularProgressIndicator()
        }

        state.error?.let { error ->
            Text(text = "Error: $error", color = Color.Red)
        }

        state.detail?.let { detail ->
            Text(
                text = detail.name.replaceFirstChar { it.uppercase() },
                fontSize = 28.sp
            )

            detail.sprites.frontDefault?.let { url ->
                Image(
                    painter = rememberAsyncImagePainter(url),
                    contentDescription = detail.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .height(200.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Tipos: ${detail.types.joinToString { it.type.name }}")
        }
    }
}
