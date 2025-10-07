/*
 * Modified build.gradle.kts for dev branch with fixed Hilt AggregateDeps error.
// trigger build

 * This file is based on the current dev branch build.gradle.kts but adds an explicit
 * kapt dependency for JavaPoet to ensure the annotation processor uses version 1.13.0.
 */

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.10"
}

android {
    namespace = "com.leson.pos"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.leson.pos"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
        vectorDrawables { useSupportLibrary = true }
    }

    buildFeatures { compose = true }
    // composeOptions { kotlinCompilerExtensionVersion = "1.6.10" }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        // isCoreLibraryDesugaringEnabled = true
    }
    // Make Kotlin bytecode target 17 (keeps Compose happy too)
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf(
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api"
        )
    }

    packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }
}

// Tell Kotlin to use the Java 17 toolchain
kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2024.10.01"))
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.compose.ui:ui")
    // Use material3 without specifying a version so that it aligns with the Compose BOM
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.navigation:navigation-compose:2.8.2")

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")

    // Upgrade Hilt to a newer version that depends on the appropriate JavaPoet
    implementation("com.google.dagger:hilt-android:2.54")
    kapt("com.google.dagger:hilt-compiler:2.54")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    implementation("com.google.android.material:material:1.12.0")
    // Add JavaPoet dependency for annotation processing to fix Hilt AggregateDeps error
        implementation("com.squareup:javapoet:1.13.0")
        kapt("com.squareup:javapoet:1.13.0")
   
}
