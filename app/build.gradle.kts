import org.jetbrains.kotlin.gradle.dsl.JvmTarget
plugins {

    alias(libs.plugins.android.application) // Android application plugin
    alias(libs.plugins.kotlin.android) // Kotlin Android plugin
    alias(libs.plugins.kotlin.compose) // Jetpack Compose support
    alias(libs.plugins.kotlin.serialization) // Kotlinx Serialization for type-safe navigation routes
    alias(libs.plugins.ksp) // Kotlin Symbol Processing (KSP) for annotation processing
    alias(libs.plugins.hilt.android) // Dagger Hilt for dependency injection
    alias(libs.plugins.room) // Room database for local storage
    id("kotlin-parcelize") // Parcelable support for Kotlin
    kotlin("kapt") // Kotlin Annotation Processing Tool

}

kotlin {
    // Keep Kotlin/JVM (incl. KAPT stubs) aligned with Android compileOptions.
    jvmToolchain(17)

    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

android {
    namespace = "com.puneet.marketplace"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.puneet.marketplace"
        minSdk = 25
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
}

dependencies {

    // Android Core Libraries
    implementation(libs.androidx.core.ktx) // Kotlin Extensions for Android
    implementation(libs.androidx.lifecycle.runtime.ktx) // Lifecycle runtime for ViewModels
    implementation(libs.androidx.lifecycle.runtime.compose) // collectAsStateWithLifecycle for Compose
    implementation(libs.androidx.activity.compose) // Jetpack Compose activity integration

    // Jetpack Compose
    implementation(platform(libs.androidx.compose.bom)) // Compose Bill of Materials
    implementation(libs.androidx.ui) // Compose UI components
    implementation(libs.androidx.ui.graphics) // Compose Graphics
    implementation(libs.androidx.ui.tooling.preview) // Preview support for Compose
    implementation(libs.androidx.material3) // Material Design 3 for Compose

    // Navigation + Kotlinx Serialization (type-safe routes)
    implementation(libs.androidx.navigation.compose) // Navigation for Compose
    implementation(libs.kotlinx.serialization.json) // Required for type-safe Navigation routes

    // Kotlin Coroutines
    implementation(libs.coroutine.android) // Coroutines for background tasks

    // Dependency Injection - Dagger Hilt
    implementation(libs.hilt)
    implementation(libs.androidx.hilt.common) // Dagger Hilt core library
    ksp(libs.hilt.compiler) // Hilt annotation processing via KSP
    implementation(libs.hilt.navigation.compose) // Hilt integration for Compose Navigation

    // Jetpack Compose - ViewModel
    implementation(libs.viewmodel.compose) // ViewModel support in Jetpack Compose

    // Material Icons Extended
    implementation(libs.androidx.material.icons.extended)
    // coil for images
    implementation(libs.coil.compose)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp.logging)

    //gson
    implementation(libs.gson)

    // Room Database
    implementation(libs.room.runtime) // Room database runtime
    implementation(libs.room.ktx) // Room Kotlin Extensions
    ksp(libs.room.compiler) // Room annotation processing via KSP

    // WorkManager
    implementation(libs.work.runtime.ktx)
    implementation(libs.hilt.work)
    ksp(libs.hilt.work.compiler)

    // Testing
    testImplementation(libs.junit) // JUnit testing framework
    androidTestImplementation(libs.androidx.junit) // AndroidX JUnit integration
    androidTestImplementation(libs.androidx.espresso.core) // UI testing with Espresso
    androidTestImplementation(platform(libs.androidx.compose.bom)) // Compose BOM for testing
    androidTestImplementation(libs.androidx.ui.test.junit4) // Compose UI testing
    debugImplementation(libs.androidx.ui.tooling) // Debugging tools for Compose UI
    debugImplementation(libs.androidx.ui.test.manifest) // Manifest testing in Compose

}

room {
    schemaDirectory("$projectDir/schemas")
}