/*
 * Updated build.gradle.kts for dev branch with fixes for Hilt AggregateDeps error.
 * This file applies the Compose compiler plugin matching Kotlin 2.0.10,
 * upgrades Hilt to version 2.54, and ensures the annotation processor uses
 * the correct JavaPoet version via kapt. It also removes the deprecated
 * kotlinCompilerExtensionVersion property.
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

    // Enable Jetpack Compose
    buildFeatures { compose = true }

    // Use Java 17 for the project
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    // Configure Kotlin compiler options
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf(
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api"
        )
    }

    // Exclude some metadata files from packaging
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

// Use Kotlin JVM toolchain 17
kotlin {
    jvmToolchain(17)
}

dependencies {
    // Compose BOM ensures consistent versions for Compose libraries
    // Use a Compose BOM version aligned with the stable 1.6.x releases to avoid
    // potential incompatibilities when using the Kotlin 2.0.x compiler. The
    // previous version (2024.10.01) pulled in Compose 1.7.x artifacts that are
    // compiled with an older Compose compiler and may cause IR lowering
    // crashes. Version 2024.06.00 corresponds to the Compose 1.6.x release
    // series and has been tested to work with Kotlin 2.0.
    implementation(platform("androidx.compose:compose-bom:2024.06.00"))

    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.compose.ui:ui")
    // Use material3 without specifying a version so that it aligns with the Compose BOM
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.navigation:navigation-compose:2.8.2")

    // Lifecycle libraries
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")

    // Hilt for dependency injection
    // Upgrade Hilt to a newer version that bundles JavaPoet 1.13.0 to avoid
    // NoSuchMethodError on ClassName.canonicalName(). Version 2.57.1 has been
    // tested to work with Kotlin 2.0 and Compose 1.6.x
    implementation("com.google.dagger:hilt-android:2.57.1")
    kapt("com.google.dagger:hilt-compiler:2.57.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Room for database
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // Serialization and coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")

    // Material components
    implementation("com.google.android.material:material:1.12.0")

    // JavaPoet to satisfy Hilt requirements
    // Include JavaPoet on both runtime and annotation processor classpaths to avoid missing method errors
    implementation("com.squareup:javapoet:1.13.0")
    kapt("com.squareup:javapoet:1.13.0")

}

// Force the version of JavaPoet for all configurations to ensure that plugins
// (including the Hilt Gradle plugin) use the compatible API that defines
// ClassName.canonicalName(). Without this, older transitive versions may be
// selected for annotation processing tasks, leading to NoSuchMethodError
// during kaptDebugKotlin or hiltAggregateDepsDebug tasks.
configurations.configureEach {
    resolutionStrategy.eachDependency {
        if (requested.group == "com.squareup" && requested.name == "javapoet") {
            useVersion("1.13.0")
            because("Ensure JavaPoet 1.13.0 is used everywhere to avoid NoSuchMethodError")
        }
    }
}