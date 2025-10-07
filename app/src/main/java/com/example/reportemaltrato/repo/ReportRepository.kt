package com.example.reportemaltrato.repo

import com.example.reportemaltrato.model.Report
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.json.JSONException
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * Repositorio responsable de la comunicación de bajo nivel con Firebase Realtime Database
 * usando llamadas HTTP simples (sin SDK oficial). Se encarga de serializar el modelo [Report].
 *
 * COMENTARIOS DIDÁCTICOS:
 * - Esta implementación usa la API REST de Realtime Database (URL + ".json"). No se usa
 *   el SDK oficial de Firebase (com.google.firebase:firebase-database), por lo que no hay
 *   listeners en tiempo real proporcionados por la SDK (p.ej. addValueEventListener).
 * - sendReport hace POST a `/reportes.json` para crear un nuevo nodo con clave autogenerada.
 * - getAllReports hace un GET a `/reportes.json` y parsea el JSON devuelto en una lista de Report.
 * - Implicación: para ver cambios en tiempo real en la app, es necesario implementar uno de los
 *   enfoques siguientes:
 *     1) Usar el SDK de Firebase y agregar un listener (p.ej. addValueEventListener) para recibir
 *        actualizaciones push.
 *     2) Implementar polling (como en `ReportListScreen`) que consulta periódicamente la API REST.
 *   En este proyecto se eligió la opción 2 (polling) por simplicidad.
 */
class ReportRepository {

    companion object {
        private const val FIREBASE_DB_URL = "https://tallerreportesdenuncia-default-rtdb.firebaseio.com/"
    }

    private fun toJson(report: Report): String {
        val obj = JSONObject()
        try {
            obj.put("type", report.type)
            obj.put("description", report.description)
            obj.put("location", report.location)
            if (report.imageUrl != null) obj.put("imageUrl", report.imageUrl)
            obj.put("nickname", report.nickname)
            obj.put("timestamp", report.timestamp)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return obj.toString()
    }

    suspend fun sendReport(report: Report): Boolean = withContext(Dispatchers.IO) {
        val url = URL(FIREBASE_DB_URL + "reportes.json")
        var conn: HttpURLConnection? = null
        try {
            conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                doOutput = true
                setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            }
            val json = toJson(report.copy(id = null))
            conn.outputStream.use { os ->
                os.write(json.toByteArray(Charsets.UTF_8))
            }
            val code = conn.responseCode
            return@withContext code in 200..299
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext false
        } finally {
            conn?.disconnect()
        }
    }

    suspend fun getAllReports(): List<Report> = withContext(Dispatchers.IO) {
        val url = URL(FIREBASE_DB_URL.trimEnd('/') + "/reportes.json")
        var conn: HttpURLConnection? = null
        try {
            conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                doInput = true
            }
            val code = conn.responseCode
            if (code !in 200..299) return@withContext emptyList()
            val reader = BufferedReader(InputStreamReader(conn.inputStream))
            val body = reader.use { it.readText() }
            if (body.isBlank() || body.trim() == "null") return@withContext emptyList()

            val json = JSONObject(body)
            val list = mutableListOf<Report>()
            val keys = json.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                val item = json.getJSONObject(key)
                val report = Report(
                    id = key,
                    type = item.optString("type", ""),
                    description = item.optString("description", ""),
                    location = item.optString("location", ""),
                    imageUrl = if (item.has("imageUrl")) item.optString("imageUrl") else null,
                    nickname = item.optString("nickname", "anónimo"),
                    timestamp = item.optLong("timestamp", System.currentTimeMillis())
                )
                list.add(report)
            }
            return@withContext list.sortedByDescending { it.timestamp }
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext emptyList()
        } finally {
            conn?.disconnect()
        }
    }
}
