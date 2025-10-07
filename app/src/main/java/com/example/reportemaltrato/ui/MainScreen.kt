package com.example.reportemaltrato.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Report
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.reportemaltrato.datastore.UserPreferences
import com.example.reportemaltrato.ui.theme.ElevatedCard
import com.example.reportemaltrato.ui.theme.GradientBackground

/**
 * Pantalla principal tras el registro con diseño Material3 simplificado.
 * Los botones principales ahora se colocan en el bottomBar del Scaffold para quedarse pegados al fondo.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController, registerViewModel: RegisterViewModel) {
    val prefs by registerViewModel.userPreferences.collectAsState(initial = UserPreferences())
    val displayName = if (prefs.anonymous || prefs.nickname.isBlank()) "anónimo" else prefs.nickname

    fun initialsOf(name: String): String {
        val parts = name.trim().split(" ").filter { it.isNotBlank() }
        return when {
            parts.isEmpty() -> "?"
            parts.size == 1 -> parts[0].take(1).uppercase()
            else -> (parts[0].take(1) + parts[1].take(1)).uppercase()
        }
    }

    val initials = initialsOf(displayName)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Inicio", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) })
        },
        bottomBar = {
            Column(modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                FilledTonalButton(
                    onClick = { navController.navigate("register") { popUpTo("main") { inclusive = true } } },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.AccountBox, contentDescription = "Cambiar usuario", modifier = Modifier.padding(end = 6.dp))
                    Text("Cambiar usuario")
                }

                FilledTonalButton(
                    onClick = { navController.navigate("form") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Report, contentDescription = "Reportar maltrato", modifier = Modifier.padding(end = 6.dp))
                    Text("Reportar maltrato")
                }

                FilledTonalButton(
                    onClick = { navController.navigate("list") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.FormatListBulleted, contentDescription = "Ver reportes", modifier = Modifier.padding(end = 6.dp))
                    Text("Ver reportes")
                }
            }
        }
    ) { padding ->
        GradientBackground(modifier = Modifier
            .padding(padding)
            .fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = initials, style = MaterialTheme.typography.titleLarge.copy(color = Color.White, fontWeight = FontWeight.Bold))
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(text = "Hola, $displayName 👋", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Bienvenido/a", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
                        }
                    }
                }
            }
        }
    }
}
