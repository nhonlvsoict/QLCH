pluginManagement {
  repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
  }
  plugins {
    id("com.android.application") version "8.6.1"
    kotlin("android") version "2.0.10"   // <- change from 2.0.20
    kotlin("kapt") version "2.0.10"      // <- change from 2.0.20
    // Updated Hilt plugin version to align with upgraded library version
    id("com.google.dagger.hilt.android") version "2.57.1"
    // Compose compiler plugin. This plugin replaces the deprecated
    // `kotlinCompilerExtensionVersion` property and aligns the Compose
    // compiler with the Kotlin version. See Compose compiler docs for
    // details.
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.10"
  }
}

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories { google(); mavenCentral() }
}

rootProject.name = "LeSonPOS"
include(":app")
