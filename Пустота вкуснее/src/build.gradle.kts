 plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.figmaapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.figmaapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        // аналог "type": "module" — в Android модули это модули Gradle
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        // Аналог jvmTarget: какой байткод генерировать
        jvmTarget = "17"
        // В Kotlin строгая типизация включена по умолчанию (аналог strict: true)
        freeCompilerArgs += listOf("-Xjsr305=strict") // для интеропа с Java
    }
    
    // Ресурсы и исходники
    sourceSets["main"].java.srcDirs("src/main/kotlin")
}

dependencies {
    // Core Compose (аналог react + react-dom)
    implementation(platform("androidx.compose:compose-bom:2025.03.01"))
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui")

    // Lifecycle (часто нужен вместе с Compose)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.0")

    // App compat (если нужны старые компоненты)
    implementation("androidx.appcompat:appcompat:1.7.0")

    // Для навигации (если будет роутинг)
    implementation("androidx.navigation:navigation-compose:2.8.0")

    // Dev-зависимости (аналог devDependencies)
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    debugImplementation("androidx.compose.ui:ui-test-junit4")

    // Корутины (стандарт для асинхронности вместо промисов)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")
}
