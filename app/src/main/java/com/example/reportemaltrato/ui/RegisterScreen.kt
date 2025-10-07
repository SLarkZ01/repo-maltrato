package com.example.reportemaltrato.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.reportemaltrato.datastore.UserPreferences
import com.example.reportemaltrato.ui.theme.ElevatedCard
import com.example.reportemaltrato.ui.theme.GradientBackground
import com.example.reportemaltrato.ui.theme.SectionTitle

/**
 * Pantalla de registro/configuración de identidad del usuario, rediseñada con Material3.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController, viewModel: RegisterViewModel = viewModel()) {
    val context = LocalContext.current
    val prefs by viewModel.userPreferences.collectAsState(initial = UserPreferences())

    var nickname by remember { mutableStateOf(prefs.nickname) }
    var anonymous by remember { mutableStateOf(prefs.anonymous) }

    LaunchedEffect(prefs) {
        nickname = prefs.nickname
        anonymous = prefs.anonymous
    }

    val isNicknameValid = anonymous || nickname.isNotBlank()
    val scrollState = rememberScrollState()

    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = { Text("Registro", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) })
    }) { padding ->
        GradientBackground(modifier = Modifier
            .padding(padding)
            .fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(20.dp),
                verticalArrangement = Arrangement.Top
            ) {
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        SectionTitle("Configura tu identidad")
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(
                            value = nickname,
                            onValueChange = { nickname = it },
                            label = { Text("Nickname") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = !isNicknameValid,
                            singleLine = true
                        )
                        if (!isNicknameValid) {
                            Text(
                                text = "Ingresa un nickname o activa el modo anónimo",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 6.dp)
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Guardar anónimamente", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                            Switch(checked = anonymous, onCheckedChange = { anonymous = it })
                        }
                        Spacer(Modifier.height(20.dp))
                        FilledTonalButton(
                            onClick = {
                                if (!isNicknameValid) {
                                    Toast.makeText(context, "Ingrese un nickname o active modo anónimo", Toast.LENGTH_SHORT).show()
                                    return@FilledTonalButton
                                }
                                viewModel.updateNickname(nickname)
                                viewModel.updateAnonymous(anonymous)
                                navController.navigate("main") { popUpTo("register") { inclusive = true } }
                            },
                            enabled = isNicknameValid,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Done, contentDescription = null, modifier = Modifier.padding(end = 6.dp))
                            Text("Guardar y continuar")
                        }
                    }
                }
            }
        }
    }
}
