package com.example.app.view

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.app.viewmodel.AuthViewModel

/**
 * Declaramos cada pantalla y su ruta como un sealed class para evitar errores de escritura.
 */
sealed class AppScreen(val route: String){
    object Welcome : AppScreen("welcome")
    object Register : AppScreen("register")
    object Login : AppScreen("login")
    object QuickLogin : AppScreen("quick_login")
    object Profile : AppScreen("profile")
    object HomeScreen : AppScreen("home_screen")
    object MainScreen : AppScreen("main_screen")
    object SettingsScreen : AppScreen("settings_screen")
    object PokemonExplorer : AppScreen("pokemon_explorer")
    object PokemonList : AppScreen("pokemon_list")
    object PokemonDetail : AppScreen("pokemon_detail/{pokemonName}") {
        fun createRoute(pokemonName: String) = "pokemon_detail/$pokemonName"
    }
}

/**
 * Punto central de la navegación de Compose. Elegimos la pantalla inicial dependiendo
 * de si existe un usuario almacenado. Todas las rutas relacionadas con Pokémon pasan
 * por aquí para mantener el flujo coherente.
 */
@Composable
fun AppNavigation(authViewModel: AuthViewModel = viewModel()) {
    val navController = rememberNavController()
    val hasUser = authViewModel.hasExistingUser()
    val startDestination = if (hasUser) AppScreen.QuickLogin.route else AppScreen.Login.route

    NavHost(navController = navController, startDestination = startDestination) {
        composable(route = AppScreen.Welcome.route) {
            WelcomeScreen(navController = navController, authViewModel = authViewModel)
        }

        composable(route = AppScreen.Register.route) {
            RegisterScreen(navController = navController, authViewModel = authViewModel)
        }

        composable(route = AppScreen.Login.route) {
            LoginScreen(navController = navController, authViewModel = authViewModel)
        }

        composable(route = AppScreen.QuickLogin.route) {
            QuickLoginScreen(navController = navController, authViewModel = authViewModel)
        }

        composable(route = AppScreen.Profile.route) {
            ProfileScreen(navController = navController, authViewModel = authViewModel)
        }

        composable(route = AppScreen.HomeScreen.route) {
            homeScreen(navController)
        }

        composable(route = AppScreen.PokemonExplorer.route) {
            PokemonExplorerScreen(
                onPokemonSelected = { name ->
                    navController.navigate(AppScreen.PokemonDetail.createRoute(name))
                },
                onProfileClick = {
                    navController.navigate(AppScreen.Profile.route)
                }
            )
        }

        composable(route = AppScreen.PokemonList.route) {
            PokemonListScreen(
                onPokemonClick = { name ->
                    navController.navigate(AppScreen.PokemonDetail.createRoute(name))
                }
            )
        }

        composable(
            route = AppScreen.PokemonDetail.route,
            arguments = listOf(
                navArgument("pokemonName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val pokemonName = backStackEntry.arguments?.getString("pokemonName") ?: return@composable
            PokemonDetailScreen(
                pokemonName = pokemonName,
                onBack = { navController.navigateUp() } // activa la flecha del AppBar
            )
        }

        composable(route = AppScreen.MainScreen.route) {
            MainScreen(navController = navController)
        }

        composable(route = AppScreen.SettingsScreen.route) {
            SettingsScreen(navController = navController)
        }
    }
}
