package com.example.reportemaltrato.model

/**
 * Modelo de dominio que representa un reporte de maltrato almacenado en Firebase Realtime Database.
 * @param id Identificador auto-generado por Firebase (clave del nodo). Puede ser null al crear un reporte nuevo.
 * @param type Tipo de maltrato (ej: Físico, Psicológico, etc.).
 * @param description Descripción libre del caso reportado.
 * @param location Ubicación o contexto donde ocurre el caso.
 * @param imageUrl URL opcional de una imagen de evidencia (no validada en la app, se guarda en texto).
 * @param nickname Alias del usuario que reporta. Si el usuario eligió anonimato se usa "anónimo".
 * @param timestamp Marca de tiempo (epoch millis) en el momento de creación del reporte (lado cliente).
 */
data class Report(
    val id: String? = null,
    val type: String = "",
    val description: String = "",
    val location: String = "",
    val imageUrl: String? = null,
    val nickname: String = "anónimo",
    val timestamp: Long = System.currentTimeMillis()
)
