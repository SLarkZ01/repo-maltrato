package com.example.reportemaltrato.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "report_draft_prefs")

/**
 * DTO ligero para representar un borrador de reporte en DataStore.
 */
data class ReportDraft(
    val type: String = "",
    val description: String = "",
    val location: String = "",
    val imageUrl: String = ""
)

/**
 * Repositorio que usa Preferences DataStore para persistir un borrador del formulario de reporte.
 * - Expondrá un Flow<ReportDraft> para que la UI pueda mostrar el borrador.
 * - Permite actualizar campos parciales y limpiar el borrador después de enviar.
 */
class ReportDraftRepository(private val context: Context) {

    companion object {
        private val TYPE_KEY = stringPreferencesKey("draft_type")
        private val DESCRIPTION_KEY = stringPreferencesKey("draft_description")
        private val LOCATION_KEY = stringPreferencesKey("draft_location")
        private val IMAGE_URL_KEY = stringPreferencesKey("draft_image_url")
    }

    val draftFlow: Flow<ReportDraft> = context.dataStore.data
        .map { prefs ->
            ReportDraft(
                type = prefs[TYPE_KEY] ?: "",
                description = prefs[DESCRIPTION_KEY] ?: "",
                location = prefs[LOCATION_KEY] ?: "",
                imageUrl = prefs[IMAGE_URL_KEY] ?: ""
            )
        }

    suspend fun updateType(type: String) {
        context.dataStore.edit { prefs ->
            prefs[TYPE_KEY] = type
        }
    }

    suspend fun updateDescription(description: String) {
        context.dataStore.edit { prefs ->
            prefs[DESCRIPTION_KEY] = description
        }
    }

    suspend fun updateLocation(location: String) {
        context.dataStore.edit { prefs ->
            prefs[LOCATION_KEY] = location
        }
    }

    suspend fun updateImageUrl(imageUrl: String) {
        context.dataStore.edit { prefs ->
            prefs[IMAGE_URL_KEY] = imageUrl
        }
    }

    suspend fun clearDraft() {
        context.dataStore.edit { prefs ->
            prefs.remove(TYPE_KEY)
            prefs.remove(DESCRIPTION_KEY)
            prefs.remove(LOCATION_KEY)
            prefs.remove(IMAGE_URL_KEY)
        }
    }
}
