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
