// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Declara explícitamente las versiones para que el IDE y Gradle las reconozcan
    id("com.android.application") version "8.13.0" apply false
    id("org.jetbrains.kotlin.android") version "2.2.0" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.0" apply false
    // Plugin para integrar Google Services (Firebase)
    id("com.google.gms.google-services") version "4.3.15" apply false
}