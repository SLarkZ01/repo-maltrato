package com.example.reportemaltrato.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.PermIdentity
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.reportemaltrato.model.Report
import com.example.reportemaltrato.ui.theme.ElevatedCard
import com.example.reportemaltrato.ui.theme.GradientBackground
import com.example.reportemaltrato.ui.theme.SectionTitle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay

/**
 * Pantalla que muestra el listado de reportes con Material3 y un layout cómodo para móvil.
 *
 * COMENTARIOS DIDÁCTICOS:
 * - Actualización en "tiempo real": esta pantalla no usa listeners push de Firebase. En su lugar
 *   implementa un polling simple dentro de `LaunchedEffect` que invoca `viewModel.fetchReports()`
 *   cada 2 segundos. Esto simula actualizaciones periódicas, pero NO es equivalente a un listener
 *   en tiempo real (SDK) que recibiría cambios inmediatamente.
 * - Alternativa: para verdadera actualización en tiempo real se debería usar el SDK de Firebase
 *   y un `ValueEventListener` o `addValueEventListener`/`ChildEventListener` sobre la referencia.
 * - UI: toda la interfaz está en Compose; no hay archivos XML con `<layout>` ni DataBinding.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportListScreen(navController: NavController, viewModel: ReportViewModel = viewModel()) {
    val reports by viewModel.reports.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchReports()
        while (true) {
            delay(2_000)
            viewModel.fetchReports()
        }
    }

    val dateFormatter = rememberDateFormatter()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Reportes", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) })
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilledTonalButton(
                    onClick = { navController.navigate("main") { popUpTo("list") { inclusive = true } } },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Volver al inicio") }
            }
        }
    ) { padding ->
        GradientBackground(modifier = Modifier
            .padding(padding)
            .fillMaxSize()) {
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)) {

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    SectionTitle("Listado (${reports.size})")
                    FilledTonalButton(onClick = { viewModel.fetchReports(force = true) }, enabled = !isLoading) {
                        Icon(Icons.Default.Cached, contentDescription = null, modifier = Modifier.padding(end = 6.dp))
                        Text("Refrescar")
                    }
                }

                Spacer(Modifier.height(12.dp))

                when {
                    isLoading && reports.isEmpty() -> {
                        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(Modifier.height(8.dp))
                            Text("Cargando reportes...")
                        }
                    }
                    errorMessage != null && reports.isEmpty() -> {
                        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.ReportProblem, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                            Spacer(Modifier.height(8.dp))
                            Text("Error: $errorMessage", color = MaterialTheme.colorScheme.error)
                            Spacer(Modifier.height(8.dp))
                            FilledTonalButton(onClick = { viewModel.fetchReports(force = true) }) { Text("Reintentar") }
                        }
                    }
                    reports.isEmpty() -> {
                        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("No hay reportes todavía", style = MaterialTheme.typography.bodyMedium)
                            Spacer(Modifier.height(6.dp))
                            Text("Sé el primero en reportar un caso.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
                            Spacer(Modifier.height(12.dp))
                            FilledTonalButton(onClick = { navController.navigate("form") }) { Text("Crear reporte") }
                        }
                    }
                    else -> {
                        LazyColumn {
                            items(reports) { report: Report ->
                                ReportItemCard(report = report, dateFormatter = dateFormatter)
                            }
                            item { Spacer(Modifier.height(80.dp)) }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

/** Tarjeta individual que presenta los datos de un [Report] con Material3. */
@Composable
private fun ReportItemCard(report: Report, dateFormatter: SimpleDateFormat) {
    ElevatedCard(modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 10.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = report.type, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(text = report.description, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(4.dp))
                Text(text = report.location, style = MaterialTheme.typography.labelSmall)
            }
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Schedule, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(4.dp))
                Text(text = dateFormatter.format(Date(report.timestamp)), style = MaterialTheme.typography.labelSmall)
            }
            if (report.nickname.isNotBlank() && report.nickname != "anónimo") {
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PermIdentity, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(4.dp))
                    Text(text = report.nickname, style = MaterialTheme.typography.labelSmall)
                }
            }
            if (!report.imageUrl.isNullOrBlank()) {
                Spacer(Modifier.height(8.dp))
                val uriHandler = LocalUriHandler.current
                val url = report.imageUrl
                // Mostrar imagen desde URL en lugar de la URL como texto
                AsyncImage(
                    model = url,
                    contentDescription = "Evidencia",
                    placeholder = ColorPainter(MaterialTheme.colorScheme.surfaceVariant),
                    error = ColorPainter(MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { url.let { runCatching { uriHandler.openUri(it) } } },
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
private fun rememberDateFormatter(): SimpleDateFormat = androidx.compose.runtime.remember {
    SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
}
