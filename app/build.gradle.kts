plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("kotlin-parcelize")
    id("kotlinx-serialization")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.timi.seulseul"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.timi.seulseul"
        minSdk = 26
        targetSdk = 33
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        dataBinding = true
    }
}

dependencies {

    // init
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${property("lifecycle_version")}")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:${property("lifecycle_version")}")
    implementation("androidx.lifecycle:lifecycle-reactivestreams-ktx:${property("lifecycle_version")}")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:${property("lifecycle_version")}")

    // retrofit
    implementation("com.squareup.retrofit2:retrofit:${property("retrofit_version")}")
    implementation("com.squareup.retrofit2:adapter-rxjava2:${property("retrofit_version")}")
    // implementation("com.squareup.retrofit2:converter-moshi:${property("retrofit_version")}")

    // coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${property("coroutine_version")}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${property("coroutine_version")}")

    // navigation
    implementation("androidx.navigation:navigation-fragment-ktx:${property("navigation_version")}")
    implementation("androidx.navigation:navigation-ui-ktx:${property("navigation_version")}")

    // hilt
    implementation("com.google.dagger:hilt-android:${property("hilt_version")}")
    kapt("com.google.dagger:hilt-android-compiler:${property("hilt_version")}")

    // okhttp
    implementation("com.squareup.okhttp3:okhttp:${property("okhttp_version")}")
    implementation("com.squareup.okhttp3:logging-interceptor:${property("okhttp_version")}")

    // serialization converter
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:${property("serialization_converter_version")}")

    // serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${property("serialization_version")}")

    // viewpager2
    implementation("androidx.viewpager2:viewpager2:${property("viewpager2_version")}")

    // glide
    implementation("com.github.bumptech.glide:glide:${property("glide_version")}")
    kapt("com.github.bumptech.glide:compiler:${property("glide_version")}")

    // Coil
    implementation("io.coil-kt:coil:${property("coil_version")}")
    implementation("io.coil-kt:coil-compose:${property("coil_version")}")

    // timber
    implementation("com.jakewharton.timber:timber:${property("timber_version")}")

    // splashscreen
    implementation("androidx.core:core-splashscreen:${property("splash_version")}")
}