// Nội dung file app/build.gradle.kts của project tuan_5
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.gms.google-services")       // đọc google-services.json
    id("com.google.firebase.crashlytics")      // Crashlytics
}

android {
    namespace = "com.example.tuan_5"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.example.tuan_5"   // phải trùng package name đăng ký trên Firebase
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures { compose = true }
}

dependencies {
    // Compose
    implementation(platform("androidx.compose:compose-bom:2026.02.01"))
    implementation("androidx.activity:activity-compose:1.13.0")
    implementation("androidx.core:core-ktx:1.19.1")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.navigation:navigation-compose:2.8.9")

    // Firebase (BoM tự chọn phiên bản khớp nhau cho các thư viện bên dưới)
    implementation(platform("com.google.firebase:firebase-bom:34.4.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-config")
    implementation("com.google.firebase:firebase-crashlytics")

    // Đăng nhập Google bằng Credential Manager
    implementation("androidx.credentials:credentials:1.5.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.5.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")

    // Dùng await() cho Task của Firebase trong coroutine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.10.2")
}
