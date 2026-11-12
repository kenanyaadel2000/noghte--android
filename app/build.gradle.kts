plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}
android {
    namespace = "com.example.noghte"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.example.noghte"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}
dependencies {
    // --- Jetpack Compose Dependencies ---
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose:1.8.2")
    
    // برای آیکون‌های Material Design (مثل Search, Settings, User و...)
    implementation("androidx.compose.material:material-icons-extended")

    // --- Appwrite SDK ---
    implementation("io.appwrite:sdk-for-android:5.0.0")

    // --- OkHttp (حل مشکل تداخل نسخه) ---
    // به جای استفاده از platform، نسخه دقیق رو اینجا ست می‌کنیم تا ارور نده
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
}
