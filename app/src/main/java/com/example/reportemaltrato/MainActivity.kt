package com.example.reportemaltrato

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.reportemaltrato.ui.MainScreen
import com.example.reportemaltrato.ui.RegisterScreen
import com.example.reportemaltrato.ui.ReportFormScreen
import com.example.reportemaltrato.ui.ReportListScreen
import com.example.reportemaltrato.ui.RegisterViewModel
import com.example.reportemaltrato.ui.theme.ReporteMaltratoTheme

/**
 * Actividad principal que inicializa el árbol Compose, configura el NavHost y aplica el tema de la app.
 * Actúa como punto de entrada único (single-activity architecture) para las pantallas declarativas.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            // ViewModel compartido a nivel de actividad para mantener preferencias de usuario entre pantallas.
            val registerViewModel: RegisterViewModel = viewModel()

            ReporteMaltratoTheme {
                Surface(color = MaterialTheme.colors.background) {
                    // Gráfico de navegación: define las rutas principales de la app.
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