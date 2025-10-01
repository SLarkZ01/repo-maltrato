package com.example.reportemaltrato.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.reportemaltrato.model.Report
import com.example.reportemaltrato.repo.ReportRepository
import com.example.reportemaltrato.datastore.UserPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.cancel

/**
 * ViewModel que gestiona la lógica de obtención y envío de reportes.
 * Expone:
 * - [reports]: lista reactiva de reportes ordenados.
 * - [isLoading]: estado de carga para operaciones de red.
 * - [errorMessage]: mensaje de error temporal cuando una petición falla.
 *
 * Flujo general:
 * UI -> sendReport() / fetchReports() -> Repository (HTTP) -> Actualiza flows internos -> UI (collectAsState).
 */
class ReportViewModel(application: Application) : AndroidViewModel(application) {
    private val reportRepository = ReportRepository()
    private val prefsRepository = UserPreferencesRepository(application)

    private val vmScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val _reports = MutableStateFlow<List<Report>>(emptyList())
    val reports: StateFlow<List<Report>> = _reports.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    /** Descarga (GET) la lista de reportes. Si ya está cargando y no se fuerza, ignora la petición. */
    fun fetchReports(force: Boolean = false) {
        if (_isLoading.value && !force) return
        vmScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val list = reportRepository.getAllReports()
                _reports.value = list
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error desconocido"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /** Envía un nuevo reporte (POST) completando el nickname según las preferencias actuales. */
    fun sendReport(report: Report, onResult: (Boolean) -> Unit) {
        vmScope.launch {
            try {
                val prefs = prefsRepository.userPreferencesFlow.first()
                val nickname = if (prefs.anonymous || prefs.nickname.isBlank()) "anónimo" else prefs.nickname
                val toSend = report.copy(nickname = nickname)
                val success = reportRepository.sendReport(toSend)
                onResult(success)
                if (success) fetchReports(force = true)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        vmScope.cancel()
    }
}
