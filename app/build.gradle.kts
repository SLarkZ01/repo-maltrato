plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // aplicar directamente el plugin del Compose Compiler
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.0"
    // Plugin de Google Services para Firebase
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.reportemaltrato"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.reportemaltrato"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
        // dataBinding desactivado (ya no se usa)
        dataBinding = false
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Jetpack Compose
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation("androidx.compose.ui:ui:1.5.0")
    implementation("androidx.compose.material:material:1.5.0")
    implementation("androidx.compose.ui:ui-tooling-preview:1.5.0")
    debugImplementation("androidx.compose.ui:ui-tooling:1.5.0")

    // Material 3 (nuevo diseño)
    implementation("androidx.compose.material3:material3:1.1.2")

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.7.0")

    // Material Icons
    implementation("androidx.compose.material:material-icons-extended:1.5.0")

    // Coil Compose for image loading
    implementation("io.coil-kt:coil-compose:2.4.0")

    // DataStore (preferences) - optional, can be removed if using SharedPreferences
    implementation("androidx.datastore:datastore-preferences:1.1.0")


    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")

    // Firebase BOM + Realtime Database KTX
    implementation(platform("com.google.firebase:firebase-bom:32.2.0"))
    implementation("com.google.firebase:firebase-database-ktx")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}