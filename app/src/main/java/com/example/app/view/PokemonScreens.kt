package com.example.app.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.app.viewmodel.PokemonViewModel

@Composable
fun PokemonExplorerScreen(
    onPokemonSelected: (String) -> Unit,
    onProfileClick: () -> Unit,
    viewModel: PokemonViewModel = viewModel()
) {
    val listState = viewModel.listState
    val detailState = viewModel.detailState
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        viewModel.loadList()
        snackbarHostState.showSnackbar(
            message = "Explora Pokémon y usa el icono para abrir tu perfil."
        )
    }

    fun performSearch() {
        val query = searchQuery.trim().lowercase()
        if (query.isNotEmpty()) {
            focusManager.clearFocus()
            viewModel.loadDetail(query)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pokédex") },
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Ir al perfil"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar Pokémon por nombre") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = { performSearch() }
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = { performSearch() },
                    enabled = searchQuery.isNotBlank()
                ) {
                    Text("Consultar")
                }
            }

            if (detailState.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            detailState.error?.let { error ->
                Text(
                    text = "Error al consultar: $error",
                    color = MaterialTheme.colorScheme.error
                )
            }

            detailState.detail?.let { detail ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = detail.name.replaceFirstChar { it.uppercase() },
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )

                        detail.sprites.frontDefault?.let { url ->
                            Image(
                                painter = rememberAsyncImagePainter(url),
                                contentDescription = detail.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp)
                                    .height(160.dp)
                            )
                        }

                        Text(
                            text = "Tipos: ${detail.types.joinToString { it.type.name }}",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        TextButton(
                            onClick = { onPokemonSelected(detail.name) }
                        ) {
                            Text("Ver detalle completo")
                        }
                    }
                }
            }

            Text(
                text = "Pokémon disponibles",
                style = MaterialTheme.typography.titleMedium
            )

            if (listState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                listState.error?.let { error ->
                    Text(
                        text = "Error al cargar la lista: $error",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = true)
            ) {
                items(listState.pokemons) { pokemon ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                searchQuery = pokemon.name
                                viewModel.loadDetail(pokemon.name)
                            }
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
}

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
            Text(text = "Error: $error", color = MaterialTheme.colorScheme.error)
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
            Text(text = "Error: $error", color = MaterialTheme.colorScheme.error)
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
