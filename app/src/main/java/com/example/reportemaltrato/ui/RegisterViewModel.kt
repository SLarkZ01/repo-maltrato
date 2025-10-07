package com.example.reportemaltrato.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.reportemaltrato.datastore.UserPreferences
import com.example.reportemaltrato.datastore.UserPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.cancel

/**
 * ViewModel responsable de exponer y actualizar las preferencias del usuario (nickname y modo anónimo).
 * Orquesta la escritura/lectura mediante [UserPreferencesRepository] y mantiene un estado con [StateFlow].
 *
 * Flujo de datos:
 * DataStore (Flow<UserPreferences>) -> collect -> _userPreferences (StateFlow) -> UI (Compose collectAsState).
 *
 * COMENTARIOS DIDÁCTICOS:
 * - Aquí se demuestra la integración típica de DataStore con la capa de presentación:
 *   1) El repositorio (`UserPreferencesRepository`) expone `userPreferencesFlow` (Flow).
 *   2) El ViewModel recoge ese Flow en `init` y lo publica en `_userPreferences` (MutableStateFlow).
 *   3) La UI (Compose) hace `collectAsState()` sobre `userPreferences` para recibir actualizaciones automáticamente.
 * - Las funciones `updateNickname` y `updateAnonymous` lanzan corrutinas en el scope del ViewModel
 *   para delegar la escritura al repositorio sin bloquear la UI.
 */
class RegisterViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = UserPreferencesRepository(application)

    private val vmScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val _userPreferences = MutableStateFlow(UserPreferences(nickname = "", anonymous = true))
    val userPreferences: StateFlow<UserPreferences> = _userPreferences.asStateFlow()

    init {
        vmScope.launch {
            repository.userPreferencesFlow.collect { prefs ->
                _userPreferences.value = prefs
            }
        }
    }

    /** Actualiza el nickname en DataStore. */
    fun updateNickname(nickname: String) {
        vmScope.launch {
            repository.updateNickname(nickname)
        }
    }

    /** Actualiza el valor del modo anónimo. */
    fun updateAnonymous(anonymous: Boolean) {
        vmScope.launch {
            repository.updateAnonymous(anonymous)
        }
    }

    override fun onCleared() {
        super.onCleared()
        vmScope.cancel()
    }
}
