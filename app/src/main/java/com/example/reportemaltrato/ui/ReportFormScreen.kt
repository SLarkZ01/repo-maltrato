package com.example.reportemaltrato.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.reportemaltrato.R
import com.example.reportemaltrato.model.Report
import com.example.reportemaltrato.ui.theme.ElevatedCard
import com.example.reportemaltrato.ui.theme.GradientBackground
import com.example.reportemaltrato.ui.theme.SectionTitle

/**
 * Formulario de reporte implementado 100% en Compose Material3.
 *
 * COMENTARIOS DIDÁCTICOS:
 * - No se utiliza DataBinding en este proyecto: todas las pantallas de entrada/visualización
 *   están hechas con Jetpack Compose. DataBinding es una técnica basada en vistas XML y binding
 *   classes; aquí se demuestra la alternativa moderna (Compose) donde el estado se mantiene
 *   en variables Compose (`remember`, `mutableStateOf`) y se maneja directamente.
 * - Validación: la pantalla calcula `isValid` localmente (campos obligatorios) y activa/desactiva
 *   el botón de enviar según ese estado.
 * - Envío: al pulsar "Enviar reporte" se crea un objeto `Report` y se invoca `viewModel.sendReport`.
 *   El ViewModel se encarga de completar el `nickname` usando DataStore y realizar el POST.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportFormScreen(navController: NavController, viewModel: ReportViewModel = viewModel(), draftViewModel: ReportFormViewModel = viewModel()) {
    val context = LocalContext.current

    val types = stringArrayResource(id = R.array.maltrato_types).toList()
    var expanded by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf("") }

    var description by remember { mutableStateOf(TextFieldValue("")) }
    var location by remember { mutableStateOf(TextFieldValue("")) }
    var imageUrl by remember { mutableStateOf(TextFieldValue("")) }

    // Nuevo: consumir borrador persistido desde DataStore
    val draft by draftViewModel.draftFlow.collectAsState(initial = com.example.reportemaltrato.datastore.ReportDraft())

    LaunchedEffect(draft) {
        // Sincronizar UI con el borrador guardado cuando cambia
        if (selectedType != draft.type) selectedType = draft.type
        if (description.text != draft.description) description = TextFieldValue(draft.description)
        if (location.text != draft.location) location = TextFieldValue(draft.location)
        if (imageUrl.text != draft.imageUrl) imageUrl = TextFieldValue(draft.imageUrl)
    }

    val isValid = selectedType.isNotBlank() && description.text.isNotBlank() && location.text.isNotBlank()

    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = { Text("Reportar maltrato", style = MaterialTheme.typography.titleLarge) })
    }) { padding ->
        GradientBackground(modifier = Modifier
            .padding(padding)
            .fillMaxSize()) {
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)) {

                Text(
                    text = "Completa la información para enviar el reporte. Los campos marcados son obligatorios.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                )
                Spacer(Modifier.height(16.dp))

                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SectionTitle("Nuevo reporte")

                        // Tipo (dropdown)
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = selectedType,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Tipo de maltrato *") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                isError = selectedType.isBlank(),
                                colors = OutlinedTextFieldDefaults.colors()
                            )
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                types.forEach { item ->
                                    DropdownMenuItem(
                                        text = { Text(item) },
                                        onClick = {
                                            selectedType = item
                                            // Persistir cambio en DataStore
                                            draftViewModel.updateType(item)
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        // Descripción
                        OutlinedTextField(
                            value = description,
                            onValueChange = {
                                description = it
                                // Persistir cambio en DataStore
                                draftViewModel.updateDescription(it.text)
                            },
                            label = { Text("Descripción *") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            maxLines = 6,
                            isError = description.text.isBlank()
                        )

                        Spacer(Modifier.height(12.dp))

                        // Ubicación
                        OutlinedTextField(
                            value = location,
                            onValueChange = {
                                location = it
                                // Persistir cambio en DataStore
                                draftViewModel.updateLocation(it.text)
                            },
                            label = { Text("Ubicación *") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = location.text.isBlank()
                        )

                        Spacer(Modifier.height(12.dp))

                        // URL de imagen (opcional)
                        OutlinedTextField(
                            value = imageUrl,
                            onValueChange = {
                                imageUrl = it
                                // Persistir cambio en DataStore
                                draftViewModel.updateImageUrl(it.text)
                            },
                            label = { Text("URL de imagen (opcional)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(Modifier.height(16.dp))

                        FilledTonalButton(
                            onClick = {
                                if (!isValid) {
                                    Toast.makeText(context, "Por favor completa los campos requeridos", Toast.LENGTH_SHORT).show()
                                    return@FilledTonalButton
                                }
                                val report = com.example.reportemaltrato.model.Report(
                                    type = selectedType,
                                    description = description.text,
                                    location = location.text,
                                    imageUrl = imageUrl.text.ifBlank { null }
                                )
                                viewModel.sendReport(report) { success ->
                                    if (success) {
                                        // Limpiar borrador al enviar correctamente
                                        draftViewModel.clearDraft()
                                        Toast.makeText(context, "Reporte enviado correctamente.", Toast.LENGTH_SHORT).show()
                                        navController.navigate("list")
                                    } else {
                                        Toast.makeText(context, "Error al enviar reporte.", Toast.LENGTH_SHORT).show()
                                    }
                                }

                            },
                            enabled = isValid,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Enviar reporte")
                        }
                    }
                }
            }
        }
    }
}
