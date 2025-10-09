package com.example.app.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.PaddingValues
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.app.viewmodel.PokemonViewModel

/**
 * Pantalla principal de la Pokédex. Exhibe la lista paginada y un panel de preview
 * que se actualiza cada vez que el usuario busca o selecciona un Pokémon.
 * También ofrece accesos directos al perfil a través del AppBar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonExplorerScreen(
    onPokemonSelected: (String) -> Unit,
    onProfileClick: () -> Unit,
    viewModel: PokemonViewModel = viewModel()
) {
    val listState = viewModel.listState
    val detailState = viewModel.detailState
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    var limitInput by rememberSaveable { mutableStateOf(listState.limit.toString()) }
    var offsetInput by rememberSaveable { mutableStateOf(listState.offset.toString()) }

    // Carga inicial de la Pokédex cuando la pantalla entra en composición.
    LaunchedEffect(Unit) {
        viewModel.loadList()
    }

    // Si la lista se recarga con distintos parámetros, actualizamos los TextField visibles.
    LaunchedEffect(listState.limit, listState.offset) {
        limitInput = listState.limit.toString()
        offsetInput = listState.offset.toString()
    }

    fun performSearch() {
        val query = searchQuery.trim()
        if (query.isEmpty()) return
        val normalized = if (query.any { it.isLetter() }) {
            query.lowercase()
        } else {
            query
        }
        if (query.isNotEmpty()) {
            focusManager.clearFocus()
            viewModel.loadDetail(normalized)
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
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Text(
                    text = "Explora Pokémon y utiliza el icono del AppBar para volver a tu perfil.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Buscar por nombre o número") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { performSearch() })
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = limitInput,
                        onValueChange = { limitInput = it },
                        label = { Text("Limit") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        )
                    )
                    OutlinedTextField(
                        value = offsetInput,
                        onValueChange = { offsetInput = it },
                        label = { Text("Offset") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        )
                    )
                    Button(
                        onClick = {
                            val parsedLimit = limitInput.toIntOrNull()?.takeIf { it > 0 } ?: 20
                            val parsedOffset = offsetInput.toIntOrNull()?.coerceAtLeast(0) ?: 0
                            focusManager.clearFocus()
                            viewModel.loadList(parsedLimit, parsedOffset)
                        }
                    ) {
                        Text("Actualizar")
                    }
                }
            }

            item {
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
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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

                                val species = detailState.species
                                species?.let { info ->
                                    val flavor = info.flavorTextEntries.firstOrNull { entry ->
                                        entry.language.name == "es"
                                    } ?: info.flavorTextEntries.firstOrNull { entry ->
                                        entry.language.name == "en"
                                    }

                                    flavor?.flavorText
                                        ?.replace("\n", " ")
                                        ?.replace("\u000c", " ")
                                        ?.takeIf { it.isNotBlank() }
                                        ?.let { flavorText ->
                                            Text(
                                                text = flavorText,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                        }

                                    val genus = info.genera.firstOrNull { entry ->
                                        entry.language.name == "es"
                                    } ?: info.genera.firstOrNull { entry ->
                                        entry.language.name == "en"
                                    }

                                    genus?.let { entry ->
                                        Text(
                                            text = "Especie: ${entry.genus}",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                    }

                                    Text(
                                        text = "Felicidad base: ${info.baseHappiness ?: "—"}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "Ratio de captura: ${info.captureRate ?: "—"}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )

                                    info.evolvesFromSpecies?.name?.let { origin ->
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Evoluciona de: ${origin.replaceFirstChar { it.uppercase() }}",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }

                                detailState.speciesError?.let { speciesError ->
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "No fue posible cargar información adicional: $speciesError",
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }

                                TextButton(
                                    onClick = { onPokemonSelected(detail.name) }
                                ) {
                                    Text("Ver detalle completo")
                                }
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Pokémon disponibles",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            if (listState.isLoading) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            listState.error?.let { error ->
                item {
                    Text(
                        text = "Error al cargar la lista: $error",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            items(listState.pokemons) { pokemon ->
                val idFromUrl = pokemon.url.trimEnd('/').substringAfterLast('/').toIntOrNull()
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
                        text = buildString {
                            idFromUrl?.let {
                                append("#$it - ")
                            }
                            append(pokemon.name.replaceFirstChar { char -> char.uppercase() })
                        },
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}
/**
 * Lista reutilizable de Pokémon. Mantiene su propio llamado a loadList para que
 * pueda usarse en nav graphs secundarios sin depender del explorer.
 */
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
/**
 * Pantalla dedicada de detalle. Muestra navegación hacia atrás y toda la información
 * disponible para el Pokémon, incluyendo species si estuvo disponible.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonDetailScreen(
    pokemonName: String,
    onBack: () -> Unit,
    viewModel: PokemonViewModel = viewModel()
) {
    val state = viewModel.detailState

    LaunchedEffect(pokemonName) {
        viewModel.loadDetail(pokemonName)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(pokemonName.replaceFirstChar { it.uppercase() }) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (state.isLoading) {
                item {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }

            state.error?.let { error ->
                item {
                    Text(text = "Error: $error", color = MaterialTheme.colorScheme.error)
                }
            }

            state.detail?.let { detail ->
                item {
                    Card {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = detail.name.replaceFirstChar { it.uppercase() },
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "ID: #${detail.id}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            detail.sprites.frontDefault?.let { url ->
                                Image(
                                    painter = rememberAsyncImagePainter(url),
                                    contentDescription = detail.name,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(220.dp)
                                )
                            }
                            Text(
                                text = "Tipos: ${detail.types.joinToString { it.type.name }}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            state.species?.let { info ->
                item {
                    Card { // Información complementaria proveniente de /pokemon-species
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val flavor = info.flavorTextEntries.firstOrNull { entry ->
                                entry.language.name == "es"
                            } ?: info.flavorTextEntries.firstOrNull { entry ->
                                entry.language.name == "en"
                            }

                            flavor?.flavorText
                                ?.replace("\n", " ")
                                ?.replace("\u000c", " ")
                                ?.takeIf { it.isNotBlank() }
                                ?.let { flavorText ->
                                    Text(
                                        text = flavorText,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }

                            val genus = info.genera.firstOrNull { entry ->
                                entry.language.name == "es"
                            } ?: info.genera.firstOrNull { entry ->
                                entry.language.name == "en"
                            }

                            genus?.let { entry ->
                                Text(
                                    text = "Especie: ${entry.genus}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            Text(
                                text = "Felicidad base: ${info.baseHappiness ?: "—"}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Ratio de captura: ${info.captureRate ?: "—"}",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            info.evolvesFromSpecies?.name?.let { origin ->
                                Text(
                                    text = "Evoluciona de: ${origin.replaceFirstChar { it.uppercase() }}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }

            state.speciesError?.let { speciesError ->
                item { // Avisamos a la persona usuaria sin bloquear el resto del detalle
                    Text(
                        text = "No fue posible cargar datos adicionales: $speciesError",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
