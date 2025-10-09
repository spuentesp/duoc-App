# 04 · Conectando PokeAPI a nuestra app

En este módulo recorremos, paso a paso, cómo el proyecto conecta una fuente de datos **real** (PokeAPI) con la UI basada en Jetpack Compose. Todo el flujo sigue la arquitectura **Repository → ViewModel → UiState → Compose**, exactamente como lo implementamos en el código del repositorio.

---

## 1. PokeAPI en contexto

[PokeAPI](https://pokeapi.co/) es una API REST pública con información de Pokémon. Utilizamos dos endpoints:

- `GET /api/v2/pokemon?limit=20` para obtener una lista básica (nombre y URL).
- `GET /api/v2/pokemon/{name}` para traer el detalle completo de un Pokémon.

---

## 2. Modelos de datos (DTO)

Archivo: `app/src/main/java/com/example/app/model/Pokemon.kt`

```kotlin
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
```

> Estas clases reflejan la estructura exacta del JSON que entrega PokeAPI. No contienen lógica, solamente datos.

---

## 3. Servicio remoto con Retrofit

Archivo: `app/src/main/java/com/example/app/data/remote/PokemonApiService.kt`

```kotlin
interface PokemonApiService {

    @GET("pokemon")
    suspend fun fetchPokemonList(
        @Query("limit") limit: Int = 20
    ): PokemonListResponse

    @GET("pokemon/{nameOrId}")
    suspend fun fetchPokemonDetail(
        @Path("nameOrId") nameOrId: String
    ): PokemonDetail
}
```

Archivo: `app/src/main/java/com/example/app/data/remote/NetworkModule.kt`

```kotlin
object NetworkModule {
    private const val BASE_URL = "https://pokeapi.co/api/v2/"

    val api: PokemonApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PokemonApiService::class.java)
    }
}
```

> `NetworkModule` centraliza la configuración de Retrofit y nos entrega una instancia lista para usar del servicio.

---

## 4. Repository: puerta de entrada a los datos

Archivo: `app/src/main/java/com/example/app/data/PokemonRepository.kt`

```kotlin
class PokemonRepository(
    private val api: PokemonApiService = NetworkModule.api
) {
    suspend fun getPokemonList(limit: Int = 20): PokemonListResponse {
        return api.fetchPokemonList(limit)
    }

    suspend fun getPokemonDetail(nameOrId: String): PokemonDetail {
        return api.fetchPokemonDetail(nameOrId)
    }
}
```

> La UI nunca habla directamente con Retrofit. Siempre pasa por el `Repository`, lo que facilita reemplazar la fuente de datos en el futuro (cache local, base de datos, etc.).

---

## 5. UiState: el estado inmutable de la UI

Archivo: `app/src/main/java/com/example/app/viewmodel/PokemonUiState.kt`

```kotlin
data class PokemonListUiState(
    val pokemons: List<BasicPokemon> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class PokemonDetailUiState(
    val detail: PokemonDetail? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
```

> Los estados son inmutables. Solo el ViewModel los modifica mediante copias (`copy`).

---

## 6. ViewModel: orquestando el flujo

Archivo: `app/src/main/java/com/example/app/viewmodel/PokemonViewModel.kt`

```kotlin
class PokemonViewModel(
    private val repository: PokemonRepository = PokemonRepository()
) : ViewModel() {

    var listState by mutableStateOf(PokemonListUiState())
        private set

    var detailState by mutableStateOf(PokemonDetailUiState())
        private set

    fun loadList(limit: Int = 20) {
        listState = listState.copy(isLoading = true, error = null)
        viewModelScope.launch {
            try {
                val result = repository.getPokemonList(limit)
                listState = listState.copy(
                    pokemons = result.results,
                    isLoading = false
                )
            } catch (e: Exception) {
                listState = listState.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun loadDetail(nameOrId: String) {
        detailState = detailState.copy(isLoading = true, error = null)
        viewModelScope.launch {
            try {
                val result = repository.getPokemonDetail(nameOrId)
                detailState = detailState.copy(detail = result, isLoading = false)
            } catch (e: Exception) {
                detailState = detailState.copy(isLoading = false, error = e.message)
            }
        }
    }
}
```

---

## 7. UI con Jetpack Compose

Archivo: `app/src/main/java/com/example/app/view/PokemonScreens.kt`

### `PokemonExplorerScreen`: hub principal tras el login

```kotlin
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

    fun performSearch() { /* dispara viewModel.loadDetail(...) */ }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pokédex") },
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Icon(Icons.Default.Person, contentDescription = "Ir al perfil")
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
            OutlinedTextField(/* búsqueda con IME Search */)
            Button(onClick = { performSearch() }, enabled = searchQuery.isNotBlank()) {
                Text("Consultar")
            }
            if (detailState.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            detailState.detail?.let { detail ->
                Card {
                    /* Nombre, imagen, tipos y botón "Ver detalle completo" */
                }
            }
            Text("Pokémon disponibles")
            if (listState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
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
```

> Además del icono del AppBar, se muestra un `Snackbar` inicial que recuerda cómo volver al perfil sin quitar protagonismo a la Pokédex.

### `PokemonListScreen`: componente reutilizable

```kotlin
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
```

### `PokemonDetailScreen`: detalle standalone

```kotlin
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
```

> Para la imagen usamos `rememberAsyncImagePainter` de Coil. Asegúrate de agregar `implementation("io.coil-kt:coil-compose:<versión>")` en `app/build.gradle.kts`.

---

## 8. Flujo unidireccional (UDF)

```
Usuario → Composable (UI)
          ↓
     PokemonViewModel
          ↓
     PokemonRepository
          ↓
     PokeAPI
          ↓
     UiState se actualiza → Compose redibuja
```

Este flujo mantiene la lógica encapsulada y la UI declarativa.

---

## 9. Checklist para replicar el módulo

1. Confirmar que los modelos en `model/Pokemon.kt` coinciden con el JSON de PokeAPI.
2. Revisar `NetworkModule` y `PokemonApiService` para entender cómo se configura Retrofit.
3. Explorar `PokemonRepository` para ver cómo se encapsulan las llamadas.
4. Analizar `PokemonUiState` y `PokemonViewModel` para comprender el manejo de estado.
5. Navegar a `PokemonScreens.kt` y probar la UI desde un `NavHost`.
6. Agregar la dependencia de Coil si aún no se encuentra en Gradle.
7. (Opcional) Integrar las pantallas en la navegación principal (`AppNavigation.kt`).

---

## 10. Relación con Evaluación 2

Este flujo refleja lo que se espera en la evaluación: separar responsabilidades por capas, mantener el estado en el ViewModel y conectar la UI con datos reales de manera reactiva.

---

### Próximo módulo → **StateFlow, Repository Pattern formal y flujo UDF avanzado**.
