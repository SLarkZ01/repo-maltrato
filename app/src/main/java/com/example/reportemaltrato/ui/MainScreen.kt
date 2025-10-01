package com.example.reportemaltrato.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.reportemaltrato.datastore.UserPreferences
import com.example.reportemaltrato.ui.theme.ElevatedCard
import com.example.reportemaltrato.ui.theme.GradientBackground

/**
 * Pantalla principal tras el registro. Muestra un panel de bienvenida y navegación
 * hacia las acciones clave: cambiar usuario, crear reporte y ver listados.
 * Depende de [RegisterViewModel] para obtener el alias vigente.
 */
@Composable
fun MainScreen(navController: NavController, registerViewModel: RegisterViewModel) {
    val prefs by registerViewModel.userPreferences.collectAsState(initial = UserPreferences())
    val displayName = if (prefs.anonymous || prefs.nickname.isBlank()) "anónimo" else prefs.nickname

    Scaffold(topBar = {
        TopAppBar(title = { Text("Inicio", style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold)) })
    }) { padding ->
        GradientBackground(modifier = Modifier
            .padding(padding)
            .fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(text = "Bienvenido", style = MaterialTheme.typography.h5)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Usuario actual: $displayName", style = MaterialTheme.typography.body1)
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = { navController.navigate("register") { popUpTo("main") { inclusive = true } } },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.padding(end = 6.dp))
                    Text("Cambiar usuario")
                }
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = { navController.navigate("form") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.padding(end = 6.dp))
                    Text("Reportar maltrato")
                }
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = { navController.navigate("list") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.ListAlt, contentDescription = null, modifier = Modifier.padding(end = 6.dp))
                    Text("Ver reportes")
                }
                Spacer(modifier = Modifier.weight(1f))
                Surface(elevation = 0.dp, color = MaterialTheme.colors.background.copy(alpha = 0f)) {
                    Text(
                        text = "Aplicación de reporte anónimo",
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.onBackground.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}
