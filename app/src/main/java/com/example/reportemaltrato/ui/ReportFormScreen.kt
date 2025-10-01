package com.example.reportemaltrato.ui

import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.reportemaltrato.R
import com.example.reportemaltrato.databinding.ReportFormBinding
import com.example.reportemaltrato.model.Report
import com.example.reportemaltrato.ui.theme.ElevatedCard
import com.example.reportemaltrato.ui.theme.GradientBackground

/**
 * Pantalla de formulario para crear un nuevo reporte.
 * Combina Compose con un layout tradicional mediante [AndroidView] y DataBinding (layout `report_form.xml`).
 * Al enviar, valida campos mínimos y delega en [ReportViewModel.sendReport]. Navega luego a la lista.
 */
@Composable
fun ReportFormScreen(navController: NavController, viewModel: ReportViewModel = viewModel()) {
    Scaffold(topBar = { TopAppBar(title = { Text("Reportar maltrato", style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold)) }) }) { padding ->
        GradientBackground(modifier = Modifier
            .padding(padding)
            .fillMaxSize()) {
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)) {

                Text(
                    text = "Completa la información para enviar el reporte. Los campos marcados son obligatorios.",
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onBackground.copy(alpha = 0.8f)
                )
                Spacer(Modifier.height(16.dp))

                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        AndroidView(factory = { ctx ->
                            val inflater = LayoutInflater.from(ctx)
                            val binding = DataBindingUtil.inflate<ReportFormBinding>(inflater, R.layout.report_form, null, false)
                            val root: View = binding.root

                            val spinner = root.findViewById<android.widget.Spinner>(R.id.spinner_type)
                            val editDescription = root.findViewById<android.widget.EditText>(R.id.edit_description)
                            val editLocation = root.findViewById<android.widget.EditText>(R.id.edit_location)
                            val editImage = root.findViewById<android.widget.EditText>(R.id.edit_image)
                            val buttonSend = root.findViewById<android.widget.Button>(R.id.button_send)

                            val adapter = android.widget.ArrayAdapter.createFromResource(ctx, R.array.maltrato_types, android.R.layout.simple_spinner_item)
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            spinner.adapter = adapter

                            buttonSend.setOnClickListener {
                                val tipo = spinner.selectedItem?.toString() ?: ""
                                val descripcion = editDescription.text.toString()
                                val ubicacion = editLocation.text.toString()
                                val image = editImage.text.toString().ifBlank { null }

                                if (tipo.isBlank() || descripcion.isBlank() || ubicacion.isBlank()) {
                                    Toast.makeText(ctx, "Por favor completa todos los campos requeridos", Toast.LENGTH_SHORT).show()
                                    return@setOnClickListener
                                }

                                val report = Report(type = tipo, description = descripcion, location = ubicacion, imageUrl = image)
                                viewModel.sendReport(report) { success ->
                                    if (success) {
                                        Toast.makeText(ctx, "Reporte enviado", Toast.LENGTH_SHORT).show()
                                        navController.navigate("list")
                                    } else {
                                        Toast.makeText(ctx, "Error al enviar reporte", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }

                            root
                        })
                    }
                }
            }
        }
    }
}
