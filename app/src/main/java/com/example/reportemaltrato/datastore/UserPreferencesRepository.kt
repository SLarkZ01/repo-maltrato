package com.example.reportemaltrato.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

/**
 * Representa las preferencias locales del usuario.
 * @param nickname Alias elegido para mostrar al enviar reportes (puede ser vacío si está en modo anónimo).
 * @param anonymous Indica si el usuario decidió operar de forma anónima.
 */
data class UserPreferences(
    val nickname: String = "",
    val anonymous: Boolean = true
)

/**
 * Repositorio que encapsula el acceso a DataStore (preferences) para leer y actualizar
 * las preferencias del usuario (nickname y modo anónimo). Provee un flujo reactivo [userPreferencesFlow]
 * que emite cada vez que se actualizan las preferencias.
 *
 * Conexión con los requisitos de la clase:
 * - Implementación del registro con DataStore: este repositorio es la pieza central que
 *   persiste la identidad del usuario (nickname/anonymous) usando Preferences DataStore.
 * - Flujo de datos hacia la UI: `userPreferencesFlow` es un Flow<UserPreferences> que el
 *   ViewModel (`RegisterViewModel`) recoge y expone a la UI mediante `collectAsState()` en Compose.
 * - Modo de uso: escribir se hace mediante `updateNickname` y `updateAnonymous` (operaciones
 *   asíncronas que llaman a `dataStore.edit {}`), lectura reactiva mediante el Flow.
 */
class UserPreferencesRepository(private val context: Context) {

    companion object {
        private val NICKNAME_KEY = stringPreferencesKey("nickname")
        private val ANONYMOUS_KEY = booleanPreferencesKey("anonymous")
    }

    // Flujo que expone la lectura de preferencias como dominio puro.
    // La UI/ViewModel pueden suscribirse para recibir actualizaciones en tiempo real.
    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data
        .map { prefs ->
            val nickname = prefs[NICKNAME_KEY] ?: ""
            val anonymous = prefs[ANONYMOUS_KEY] ?: true
            UserPreferences(nickname = nickname, anonymous = anonymous)
        }

    /** Actualiza el nickname persistido. */
    suspend fun updateNickname(nickname: String) {
        // Escritura atómica en DataStore
        context.dataStore.edit { prefs ->
            prefs[NICKNAME_KEY] = nickname
        }
    }

    /** Activa o desactiva el modo anónimo. */
    suspend fun updateAnonymous(anonymous: Boolean) {
        // Escritura atómica en DataStore
        context.dataStore.edit { prefs ->
            prefs[ANONYMOUS_KEY] = anonymous
        }
    }
}
