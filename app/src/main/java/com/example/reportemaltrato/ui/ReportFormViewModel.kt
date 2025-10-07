package com.example.reportemaltrato.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.reportemaltrato.datastore.ReportDraft
import com.example.reportemaltrato.datastore.ReportDraftRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * ViewModel responsable de exponer y actualizar el borrador del formulario de reporte.
 * - Expone `draftFlow` para que la UI pueda mostrar el borrador persistido.
 * - Provee operaciones no-suspend que lanzan corrutinas internamente para actualizar DataStore.
 */
class ReportFormViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ReportDraftRepository(application)

    val draftFlow: Flow<ReportDraft> = repository.draftFlow

    fun updateType(type: String) {
        viewModelScope.launch { repository.updateType(type) }
    }

    fun updateDescription(description: String) {
        viewModelScope.launch { repository.updateDescription(description) }
    }

    fun updateLocation(location: String) {
        viewModelScope.launch { repository.updateLocation(location) }
    }

    fun updateImageUrl(imageUrl: String) {
        viewModelScope.launch { repository.updateImageUrl(imageUrl) }
    }

    fun clearDraft() {
        viewModelScope.launch { repository.clearDraft() }
    }
}

