pluginManagement {
  repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
  }
  plugins {
    id("com.android.application") version "8.6.1"
    kotlin("android") version "2.0.20"
    kotlin("kapt") version "2.0.20"
    id("com.google.dagger.hilt.android") version "2.52"
  }
}

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories { google(); mavenCentral() }
}

rootProject.name = "LeSonPOS"
include(":app")
