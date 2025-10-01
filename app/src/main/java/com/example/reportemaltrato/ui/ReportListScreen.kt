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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.reportemaltrato.model.Report
import com.example.reportemaltrato.ui.theme.ElevatedCard
import com.example.reportemaltrato.ui.theme.GradientBackground
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay

/**
 * Pantalla que muestra el listado de reportes obtenidos desde Firebase.
 * Implementa polling periódico (cada 5 segundos) para refrescar la lista.
 * Estados UI: carga inicial, error, vacío o lista de resultados.
 * Usa [ReportViewModel] para orquestar llamadas y exponer flows.
 */
@Composable
fun ReportListScreen(navController: NavController, viewModel: ReportViewModel = viewModel()) {
    val reports by viewModel.reports.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchReports()
        while (true) {
            delay(5_000) // Polling cada 5 segundos
            viewModel.fetchReports()
        }
    }

    val dateFormatter = rememberDateFormatter()

    Scaffold(topBar = { TopAppBar(title = { Text("Reportes", style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold)) }) }) { padding ->
        GradientBackground(modifier = Modifier
            .padding(padding)
            .fillMaxSize()) {
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)) {

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Listado (${reports.size})", style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold))
                    Button(onClick = { viewModel.fetchReports(force = true) }, enabled = !isLoading) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.padding(end = 6.dp))
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
                            Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colors.error)
                            Spacer(Modifier.height(8.dp))
                            Text("Error: $errorMessage", color = MaterialTheme.colors.error)
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = { viewModel.fetchReports(force = true) }) { Text("Reintentar") }
                        }
                    }
                    reports.isEmpty() -> {
                        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("No hay reportes todavía", style = MaterialTheme.typography.body1)
                            Spacer(Modifier.height(6.dp))
                            Text("Sé el primero en reportar un caso.", style = MaterialTheme.typography.caption, color = MaterialTheme.colors.onBackground.copy(alpha = 0.7f))
                            Spacer(Modifier.height(12.dp))
                            Button(onClick = { navController.navigate("form") }) { Text("Crear reporte") }
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
                Button(
                    onClick = { navController.navigate("main") { popUpTo("list") { inclusive = true } } },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Volver al inicio") }
            }
        }
    }
}

/** Tarjeta individual que presenta los datos de un [Report]. */
@Composable
private fun ReportItemCard(report: Report, dateFormatter: SimpleDateFormat) {
    ElevatedCard(modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 10.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = report.type, style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold))
            Spacer(Modifier.height(4.dp))
            Text(text = report.description, style = MaterialTheme.typography.body2)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Place, contentDescription = null, tint = MaterialTheme.colors.primary)
                Spacer(Modifier.width(4.dp))
                Text(text = report.location, style = MaterialTheme.typography.caption)
            }
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AccessTime, contentDescription = null, tint = MaterialTheme.colors.primary)
                Spacer(Modifier.width(4.dp))
                Text(text = dateFormatter.format(Date(report.timestamp)), style = MaterialTheme.typography.caption)
            }
            if (!report.nickname.isNullOrBlank() && report.nickname != "anónimo") {
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colors.primary)
                    Spacer(Modifier.width(4.dp))
                    Text(text = report.nickname ?: "", style = MaterialTheme.typography.caption)
                }
            }
            // Nueva sección para mostrar enlace de evidencia si existe
            if (!report.imageUrl.isNullOrBlank()) {
                Spacer(Modifier.height(4.dp))
                val uriHandler = LocalUriHandler.current
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Image, contentDescription = null, tint = MaterialTheme.colors.primary)
                    Spacer(Modifier.width(4.dp))
                    val display = try { // acortar si es muy largo
                        val url = report.imageUrl!!
                        if (url.length > 40) url.take(37) + "..." else url
                    } catch (e: Exception) { "Evidencia" }
                    Text(
                        text = display.ifBlank { "Evidencia" },
                        color = MaterialTheme.colors.primary,
                        style = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.clickable { runCatching { uriHandler.openUri(report.imageUrl!!) } }
                    )
                }
            }
        }
    }
}

@Composable
private fun rememberDateFormatter(): SimpleDateFormat = androidx.compose.runtime.remember {
    SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
}
