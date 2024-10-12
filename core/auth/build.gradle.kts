plugins {
    id("com.android.library") // or "com.android.application" for the app module
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.auth"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.2"
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(project(":core:designsystem"))


    implementation("androidx.navigation:navigation-runtime-ktx:2.7.7")
    val composeBom = platform("androidx.compose:compose-bom:2023.05.01")
    implementation(composeBom)
    androidTestImplementation(composeBom)
    implementation("androidx.compose.material3:material3")
    // or Material Design 2
    // implementation("androidx.compose.material:material")

    // Compose tools
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // Optional - Integration with activities
    implementation("androidx.activity:activity-compose:1.7.2")

    // Optional - Integration with ViewModels
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")

    // Optional - Integration with LiveData
    implementation("androidx.compose.runtime:runtime-livedata")
}
