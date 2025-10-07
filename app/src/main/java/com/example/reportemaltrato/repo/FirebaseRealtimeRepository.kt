package com.example.reportemaltrato.repo

import com.example.reportemaltrato.model.Report
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Repositorio que usa el SDK de Firebase Realtime Database y expone un Flow<List<Report>>
 * basado en un ValueEventListener (listener push en tiempo real).
 *
 * Comentarios explicativos añadidos para docencia:
 * - `dbRef` apunta a la rama "reportes" en la Realtime Database (definida en google-services.json).
 * - `reportsFlow` convierte el listener push (ValueEventListener) en un Flow usando `callbackFlow`.
 *   Esto permite a los consumidores (ViewModel/Compose) colectar los cambios de forma reactiva
 *   y recibir actualizaciones en tiempo real sin hacer polling.
 * - `sendReport` usa `push()` para generar una clave única en el nodo y `setValue(report)` para
 *   almacenar el objeto; se usa `suspendCancellableCoroutine` para suspender hasta el resultado.
 * - `getAllOnce` realiza una lectura puntual (`get()`) y transforma el snapshot en una lista de `Report`.
 */
class FirebaseRealtimeRepository {

    // Referencia al nodo donde se guardan los reportes
    private val dbRef = Firebase.database.getReference("reportes")

    /**
     * Flow que emite la lista completa de reportes cada vez que hay cambios en el nodo.
     * Implementación técnica:
     * - Se crea un `ValueEventListener` que en `onDataChange` recorre `snapshot.children` y mapea
     *   cada child a un `Report`.
     * - `callbackFlow` permite integrar la API de callback de Firebase con coroutines/Flow.
     * - `awaitClose` se encarga de remover el listener cuando el consumidor cancela la colección.
     *
     * Importante para la clase: esta estrategia proporciona actualización en 'tiempo real' en la UI.
     */
    val reportsFlow: Flow<List<Report>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val list = mutableListOf<Report>()
                    for (child in snapshot.children) {
                        val id = child.key
                        val type = child.child("type").getValue(String::class.java) ?: ""
                        val description = child.child("description").getValue(String::class.java) ?: ""
                        val location = child.child("location").getValue(String::class.java) ?: ""
                        val imageUrl = child.child("imageUrl").getValue(String::class.java)
                        val nickname = child.child("nickname").getValue(String::class.java) ?: "anónimo"
                        val timestamp = child.child("timestamp").getValue(Long::class.java) ?: System.currentTimeMillis()
                        val report = Report(
                            id = id,
                            type = type,
                            description = description,
                            location = location,
                            imageUrl = imageUrl,
                            nickname = nickname,
                            timestamp = timestamp
                        )
                        list.add(report)
                    }
                    // Emitir la lista (ordenada desc por timestamp)
                    trySend(list.sortedByDescending { it.timestamp })
                } catch (e: Exception) {
                    // En caso de parsing, cerrar el flow con excepción
                    close(e)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Si Firebase cancela el listener, se propaga la excepción cerrando el flow
                close(error.toException())
            }
        }

        // TODO: en producción podrías usar filtros/queries para limitar datos transferidos
        dbRef.addValueEventListener(listener)
        // awaitClose asegura que al cancelar la colección se remueva el listener y no haya fugas
        awaitClose { dbRef.removeEventListener(listener) }
    }

    /** Envía un nuevo reporte usando SDK (push + setValue). Retorna true si éxito. */
    suspend fun sendReport(report: Report): Boolean = withContext(Dispatchers.IO) {
        try {
            suspendCancellableCoroutine<Boolean> { cont ->
                val pushRef = dbRef.push() // Crea una nueva clave única en el nodo
                // setValue serializa el objeto Report y lo guarda en la base de datos
                pushRef.setValue(report)
                    .addOnSuccessListener { cont.resume(true) }
                    .addOnFailureListener { ex -> cont.resumeWithException(ex) }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /** Obtiene la lista de reportes una sola vez (one-shot) usando get(). */
    suspend fun getAllOnce(): List<Report> = withContext(Dispatchers.IO) {
        try {
            suspendCancellableCoroutine<List<Report>> { cont ->
                dbRef.get()
                    .addOnSuccessListener { snapshot ->
                        try {
                            val list = mutableListOf<Report>()
                            for (child in snapshot.children) {
                                val id = child.key
                                val type = child.child("type").getValue(String::class.java) ?: ""
                                val description = child.child("description").getValue(String::class.java) ?: ""
                                val location = child.child("location").getValue(String::class.java) ?: ""
                                val imageUrl = child.child("imageUrl").getValue(String::class.java)
                                val nickname = child.child("nickname").getValue(String::class.java) ?: "anónimo"
                                val timestamp = child.child("timestamp").getValue(Long::class.java) ?: System.currentTimeMillis()
                                val report = Report(
                                    id = id,
                                    type = type,
                                    description = description,
                                    location = location,
                                    imageUrl = imageUrl,
                                    nickname = nickname,
                                    timestamp = timestamp
                                )
                                list.add(report)
                            }
                            cont.resume(list.sortedByDescending { it.timestamp })
                        } catch (e: Exception) {
                            cont.resumeWithException(e)
                        }
                    }
                    .addOnFailureListener { ex -> cont.resumeWithException(ex) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
