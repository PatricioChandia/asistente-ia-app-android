plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.devst.voicegpt"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.devst.loginbasico"
        minSdk = 31
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    // ===================================================================
    // N U E V O: Librerías para llamadas API (Retrofit) y JSON (Gson)
    // La sintaxis es con paréntesis y comillas dobles en .kts
    // ===================================================================
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // ===================================================================
    // (NUEVO) Glide para cargar imágenes desde URL
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // (NUEVO) Para vistas de imagen circulares
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}