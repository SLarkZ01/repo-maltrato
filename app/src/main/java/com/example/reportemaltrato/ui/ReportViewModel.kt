package com.example.reportemaltrato.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.reportemaltrato.model.Report
import com.example.reportemaltrato.repo.FirebaseRealtimeRepository
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
 * Relación con los requisitos:
 * - Implementación del registro con DataStore: el ViewModel consulta `UserPreferencesRepository`
 *   para obtener el nickname actual antes de enviar (`sendReport`). Esto demuestra lectura puntual
 *   de DataStore y su integración con la capa de red.
 * - Reportes enviados y guardados en Firebase: `sendReport` delega en `FirebaseRealtimeRepository.sendReport`.
 * - Listado de reportes actualizado en tiempo real: en `init` el ViewModel se suscribe a
 *   `firebaseRepository.reportsFlow`, que proviene de un `ValueEventListener` (push) en Firebase.
 */
class ReportViewModel(application: Application) : AndroidViewModel(application) {
    // Ahora usamos el SDK de Firebase para listeners en tiempo real
    private val firebaseRepository = FirebaseRealtimeRepository()
    private val prefsRepository = UserPreferencesRepository(application)

    // COMENTARIOS DIDÁCTICOS:
    // - Este ViewModel integra dos responsabilidades principales:
    //   1) Acceder a datos remotos (ReportRepository) para enviar y obtener reportes.
    //   2) Consultar preferencias locales (UserPreferencesRepository) para determinar el nickname
    //      que se añadirá al reporte antes de enviarlo.
    // - Observa que `sendReport` obtiene las preferencias actuales usando `userPreferencesFlow.first()`
    //   (lectura puntual) para completar el campo `nickname` del reporte; esto demuestra la
    //   interacción entre DataStore (local) y la capa de red antes de una operación de POST.
    // - Tras enviar con éxito, el ViewModel no fuerza un fetch porque el listener en tiempo real
    //   actualizará automáticamente `reports` (estrategia push basada en Flow).

    private val vmScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val _reports = MutableStateFlow<List<Report>>(emptyList())
    val reports: StateFlow<List<Report>> = _reports.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        // Suscribirse al Flow proporcionado por FirebaseRealtimeRepository (ValueEventListener)
        // Esto implementa el requisito: "Listado de reportes actualizado en tiempo real"
        vmScope.launch {
            try {
                firebaseRepository.reportsFlow.collect { list ->
                    // Al recibir una emisión, actualizamos el StateFlow que expone la UI
                    _reports.value = list
                }
            } catch (e: Exception) {
                // Cualquier error de escucha se refleja en errorMessage para que la UI lo muestre
                _errorMessage.value = e.message ?: "Error de conexión"
            }
        }
    }

    /** Descarga manual (one-shot) usando SDK, útil para refresco manual. */
    fun fetchReports(force: Boolean = false) {
        if (_isLoading.value && !force) return
        vmScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val list = firebaseRepository.getAllOnce()
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
                // Leer las preferencias actuales desde DataStore (lectura puntual)
                val prefs = prefsRepository.userPreferencesFlow.first()
                val nickname = if (prefs.anonymous || prefs.nickname.isBlank()) "anónimo" else prefs.nickname
                val toSend = report.copy(nickname = nickname)

                // Delegar el envío al repositorio Firebase (push + setValue)
                val success = firebaseRepository.sendReport(toSend)
                onResult(success)
                // No necesitamos forzar fetch: el listener en tiempo real actualizará la lista
            } catch (_: Exception) {
                onResult(false)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        vmScope.cancel()
    }
}
