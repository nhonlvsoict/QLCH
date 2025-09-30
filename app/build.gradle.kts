plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("kotlin-kapt") // cho room-compiler
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.0" // cho kotlinx.serialization
}

android {
    namespace = "com.example.qlch"
    compileSdk = 36 // hoặc 36 nếu bạn dùng SDK preview

    defaultConfig {
        applicationId = "com.example.qlch"
        minSdk = 26
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
    }
}

dependencies {
    // AndroidX cơ bản
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")

    // Jetpack Compose BOM (quản lý version tập trung)
    implementation(platform("androidx.compose:compose-bom:2024.09.01"))

    // Các thư viện Compose
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    // Activity Compose (để setContent {})
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation(libs.androidx.navigation.compose)

    // Debug & Test
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    // Unit test
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    // Room Core
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

// Room + Kotlin Coroutines support
    implementation("androidx.room:room-ktx:2.6.1")

// Room + Kotlin Serialization support
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")


}