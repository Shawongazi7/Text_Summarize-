plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("kotlin-kapt")
//    id("com.google.android.libraries.mapsplatform.secrets.gradle.plugin")
}

android {
    namespace = "com.example.Text_Summarizer"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.Text_Summarizer"
        minSdk = 28
        targetSdk = 34
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
        viewBinding = true
    }

    dependencies {
        // Core Android libraries
        implementation("androidx.core:core-ktx:1.12.0")
        implementation("androidx.appcompat:appcompat:1.6.1")
        implementation("com.google.android.material:material:1.11.0")
        implementation("androidx.constraintlayout:constraintlayout:2.1.4")
        implementation("androidx.annotation:annotation:1.6.0")

        // Lifecycle components
        implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
        implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
        implementation("com.android.volley:volley:1.2.1")

        // CardView
        implementation("androidx.cardview:cardview:1.0.0")

        // JSON parsing
        implementation("com.google.code.gson:gson:2.10.1")

        // OkHttp for HTTP requests
        implementation("com.squareup.okhttp3:okhttp:4.11.0")

        // Glide for image loading
        implementation("com.github.bumptech.glide:glide:4.13.0")
        kapt("com.github.bumptech.glide:compiler:4.13.0")

        // Firebase
        implementation(platform("com.google.firebase:firebase-bom:33.8.0"))
        implementation("com.google.firebase:firebase-auth")
        implementation("com.google.firebase:firebase-database")
        implementation("com.google.firebase:firebase-firestore:25.1.1")
        implementation("com.google.firebase:firebase-storage")

        // Room database
        implementation("androidx.room:room-runtime:2.6.1")
        kapt("androidx.room:room-compiler:2.6.1")
        implementation("androidx.room:room-ktx:2.6.1")

        // CircleImageView
        implementation("de.hdodenhof:circleimageview:3.1.0")

        // Google Play Services for Authentication
        implementation("com.google.android.gms:play-services-auth:20.7.0")

        // Testing dependencies
        testImplementation("junit:junit:4.13.2")
        androidTestImplementation("androidx.test.ext:junit:1.1.5")
        androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
        testImplementation("com.squareup.okhttp3:mockwebserver:4.11.0")

        implementation("com.google.ai.client.generativeai:generativeai:0.9.0")
//    implementation("com.google.android.libraries.mapsplatform.secrets:secrets-gradle-plugin:2.0.0")

        // Required for one-shot operations (to use `ListenableFuture` from Reactive Streams)
        implementation("com.google.guava:guava:31.0.1-android")

        // Required for streaming operations (to use `Publisher` from Guava Android)
        implementation("org.reactivestreams:reactive-streams:1.0.4")

        implementation("androidx.fragment:fragment-ktx:1.6.1")
//        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")


        implementation ("com.squareup.retrofit2:retrofit:2.9.0")
        implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
        implementation ("com.squareup.okhttp3:logging-interceptor:4.11.0")
        implementation("androidx.work:work-runtime-ktx:2.10.0")
//
//        implementation("com.github.LottieFiles:dotlottie-android:0.4.1")
        implementation("com.airbnb.android:lottie:5.2.0")

    }
}
