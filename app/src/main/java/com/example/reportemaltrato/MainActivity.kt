package com.example.reportemaltrato

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.example.reportemaltrato.ui.MainScreen
import com.example.reportemaltrato.ui.RegisterScreen
import com.example.reportemaltrato.ui.ReportFormScreen
import com.example.reportemaltrato.ui.ReportListScreen
import com.example.reportemaltrato.ui.RegisterViewModel
import com.example.reportemaltrato.ui.theme.ReporteMaltratoTheme

/**
 * Actividad principal que inicializa el árbol Compose, configura el NavHost y aplica el tema de la app.
 * Actúa como punto de entrada único (single-activity architecture) para las pantallas declarativas.
 *
 * Comentarios didácticos añadidos:
 * - `FirebaseApp.initializeApp(this)`: inicializa el SDK de Firebase usando la configuración en
 *   `app/google-services.json`. Es necesario para usar Realtime Database (u otros servicios).
 * - `setContent { ... }`: monta la UI declarativa de Jetpack Compose. No se utiliza un layout XML
 *   en esta Activity; la app está migrada a Compose.
 * - `rememberNavController()` + `NavHost`: implementan la navegación entre pantallas de Compose.
 * - `val registerViewModel: RegisterViewModel = viewModel()`: crea un ViewModel a nivel de actividad
 *   que se comparte entre pantallas (`RegisterScreen` y `MainScreen`) para mantener el estado
 *   de preferencias del usuario (DataStore) sin perderlo al navegar.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inicializar Firebase explícitamente (usa google-services.json en /app)
        FirebaseApp.initializeApp(this)
        setContent {
            val navController = rememberNavController()
            // ViewModel compartido a nivel de actividad para mantener preferencias de usuario entre pantallas.
            val registerViewModel: RegisterViewModel = viewModel()

            ReporteMaltratoTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    // Gráfico de navegación: define las rutas principales de la app.
                    // Notas didácticas:
                    // - StartDestination está en "register" para forzar que el usuario configure
                    //   su identidad (DataStore) antes de usar la app.
                    // - Las rutas referencian composables que usan ViewModels y repositorios.
                    NavHost(navController = navController, startDestination = "register") {
                        composable("register") { RegisterScreen(navController, registerViewModel) }
                        composable("main") { MainScreen(navController, registerViewModel) }
                        composable("form") { ReportFormScreen(navController) }
                        composable("list") { ReportListScreen(navController) }
                    }
                }
            }
        }
    }
}