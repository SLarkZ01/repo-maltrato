# ReporteMaltrato — Guía para explicar la implementación ✅

Este README resume, con ejemplos y emojis, cómo el proyecto cumple los requisitos que debes explicar en clase. Incluye: dónde buscar el código, qué mostrar en la presentación y comandos útiles para compilar/pruebas rápidas.

---

## Resumen rápido (1-liner)
La app usa Jetpack Compose para la UI, Preferences DataStore para persistencia local (registro y borradores) y Firebase Realtime Database para almacenar y recibir reportes en tiempo real. 📲🔥

---

## Checklist de requisitos (y dónde encontrarlos)
- 📝 Implementación del registro con DataStore — Hecho ✅
  - Archivo: `app/src/main/java/com/example/reportemaltrato/datastore/UserPreferencesRepository.kt`
  - ViewModel: `app/src/main/java/com/example/reportemaltrato/ui/RegisterViewModel.kt`
  - UI: `app/src/main/java/com/example/reportemaltrato/ui/RegisterScreen.kt`

- 💾 Reportes enviados y guardados en Firebase — Hecho ✅
  - Repo: `app/src/main/java/com/example/reportemaltrato/repo/FirebaseRealtimeRepository.kt`
  - ViewModel: `app/src/main/java/com/example/reportemaltrato/ui/ReportViewModel.kt`
  - UI: `app/src/main/java/com/example/reportemaltrato/ui/ReportFormScreen.kt`

- 🔄 Listado de reportes actualizado en tiempo real — Hecho ✅
  - `FirebaseRealtimeRepository.reportsFlow` (ValueEventListener → Flow)
  - `ReportViewModel` se suscribe y la UI (`ReportListScreen.kt`) usa `collectAsState()`.

- 🧾 Uso de DataBinding en formulario de reportes — No usado (migrado a Compose) ℹ️
  - Archivo XML residual: `app/src/main/res/layout/report_form.xml` (vacío / no usado).
  - UI actual: Compose (`ReportFormScreen.kt`).

- 🎨 Diseño de interfaces en Jetpack Compose — Hecho ✅
  - Pantallas: `ReportFormScreen.kt`, `ReportListScreen.kt`, `RegisterScreen.kt`, `MainScreen.kt`.
  - Tema y componentes reutilizables: `app/src/main/java/com/example/reportemaltrato/ui/theme`.

- 📁 Organización del código — Clara y modular ✅
  - `repo/` para acceso a datos remotos
  - `datastore/` para DataStore
  - `ui/` para composables y ViewModels
  - `model/` para DTOs (p. ej. `Report`)

---

## Qué mostrar en la clase (guion con emojis) 🎤
1. 🔑 Registro con DataStore (mostrar flujo de datos)
   - Abre `UserPreferencesRepository.kt` y muestra `userPreferencesFlow`.
   - Explica que `RegisterViewModel` recoge ese Flow y la UI hace `collectAsState()`.
   - Muestra `updateNickname()` y `updateAnonymous()` (llaman `dataStore.edit{}`).

2. 🧾 Borrador del formulario (persistencia local con DataStore)
   - Abre `ReportDraftRepository.kt` y `ReportFormViewModel.kt`.
   - En `ReportFormScreen.kt` muestra cómo en cada `onValueChange` se llama a `draftViewModel.update*()` y cómo se usa `draftFlow` para poblar los campos.
   - Explica `clearDraft()` después de enviar.

3. 🔁 Firebase Realtime (envío y listado en tiempo real)
   - Abre `FirebaseRealtimeRepository.kt`:
     - Explica `dbRef` → `ValueEventListener` → parseo del snapshot → `callbackFlow` (trySend).
     - Muestra `sendReport()` (push + setValue) y `getAllOnce()`.
   - Abre `ReportViewModel.kt`: muestra la suscripción en `init` a `reportsFlow` y `sendReport()` que obtiene nickname desde DataStore antes de enviar.
   - Abre `ReportListScreen.kt`: enseña cómo la UI hace `collectAsState()` y se actualiza sin polling.

4. 🎨 Jetpack Compose (UI y navegación)
   - Muestra `MainActivity.kt`: `FirebaseApp.initializeApp(this)` y `setContent { NavHost... }`.
   - Explica que la app es single-activity y que el ViewModel `RegisterViewModel` se comparte a nivel de actividad.

5. ❌ DataBinding vs Compose
   - Señala `res/layout/report_form.xml` y explica que es un archivo residual; la implementación actual está en Compose.

---

## Fragmentos clave para mostrar (citas rápidas)
- DataStore Flow (UserPreferences):

```kotlin
val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data
    .map { prefs ->
        UserPreferences(nickname = prefs[NICKNAME_KEY] ?: "", anonymous = prefs[ANONYMOUS_KEY] ?: true)
    }
```

- Firebase Flow (listener -> Flow):

```kotlin
val reportsFlow: Flow<List<Report>> = callbackFlow {
  val listener = object : ValueEventListener { ... onDataChange -> trySend(list) }
  dbRef.addValueEventListener(listener)
  awaitClose { dbRef.removeEventListener(listener) }
}
```

- Envío de reporte (push + setValue):

```kotlin
val pushRef = dbRef.push()
pushRef.setValue(report)
    .addOnSuccessListener { /* éxito */ }
    .addOnFailureListener { /* fallo */ }
```

- Uso en Compose (collectAsState):

```kotlin
val reports by viewModel.reports.collectAsState()
```

---

## Comandos útiles (ejecutar en `cmd.exe` desde la raíz del proyecto)
- Compilar debug:

```bat
gradlew assembleDebug
```

- Ejecutar tests (si hubiera tests):

```bat
gradlew test
```

---

## Sugerencias para la presentación en clase
- Proyecta los archivos mencionados y sigue el guion: DataStore → Borrador → Firebase → UI.
- Muestra en vivo (si puedes) cómo al enviar un reporte en un dispositivo/emulador aparece en la lista de otro (demostración en tiempo real).
- Si no puedes ejecutar en vivo, usa capturas de pantalla o explica el flujo mostrando las líneas de código que añadí con comentarios.

---

## Próximos pasos (opcional)
- Puedo generar un archivo `slides.md` con puntos por diapositiva.
- Puedo añadir pruebas unitarias para `ReportViewModel` y `ReportFormViewModel`.

---

Si quieres, ahora genero un `slides.md` para la presentación o añado pruebas pequeñas — dime qué prefieres y lo hago. 😊
