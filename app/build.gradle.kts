plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.mynotebook"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.mynotebook"
        minSdk = 24
        targetSdk = 35
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

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10" // Dopasuj do wersji Compose
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    // Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation (libs.jetbrains.kotlinx.coroutines.core)
    implementation (libs.kotlinx.coroutines.android)


    // Jetpack Compose
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui.tooling.preview)

    // Compose Navigation
    implementation(libs.androidx.navigation.compose)

    // ViewModel + Lifecycle + Flow
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Kotlin Coroutines
    implementation(libs.jetbrains.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android.v1101)

    // Retrofit + Gson
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // Compose Debugging
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation (libs.accompanist.permissions)
    implementation (libs.androidx.runtime)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.lifecycle.runtime.ktx.v262)
    implementation(libs.coil.compose)
    implementation (libs.play.services.maps.v1820)
    implementation (libs.maps.compose.v2114)
    implementation(libs.androidx.material.icons.extended)

}