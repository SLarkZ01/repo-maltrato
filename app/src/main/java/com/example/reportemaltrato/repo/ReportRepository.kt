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
 * Seguridad: Este enfoque asume reglas abiertas o autenticación previa no implementada aquí.
 * Para producción se recomienda proteger la base de datos y firmar peticiones.
 */
class ReportRepository {

    companion object {
        private const val FIREBASE_DB_URL = "https://tallerreportesdenuncia-default-rtdb.firebaseio.com/"
    }

    /**
     * Convierte un [Report] a JSON plano para enviarlo por HTTP.
     * Ignora el campo id porque Firebase genera su propia clave.
     */
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

    /**
     * Envía un reporte a Firebase (operación POST) generando un nuevo nodo bajo `reportes`.
     * @return true si el código HTTP está en el rango 2xx, false en caso de error o excepción.
     */
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

    /**
     * Obtiene todos los reportes existentes bajo el nodo `reportes`.
     * @return Lista de reportes ordenada descendentemente por timestamp. Si falla o no hay datos, lista vacía.
     */
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
