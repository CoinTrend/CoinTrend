plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.dagger.hilt)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-parcelize")
}

android {
    compileSdk = 36

    defaultConfig {
        minSdk = 23
        testOptions.targetSdk = 36
        lint.targetSdk = 36

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlinOptions {
        jvmTarget = "21"
        allWarningsAsErrors = true
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    namespace = "com.cointrend.presentation"
}

dependencies {

    implementation(project(":domain"))

    // davidepanidev
    implementation(libs.android.extensions)
    implementation(libs.kotlin.extensions)

    coreLibraryDesugaring(libs.desugar)

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.lifecycle.livedata)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.coroutines.android)
    implementation(libs.coroutines.core)

    implementation(libs.splashscreen)

    implementation(libs.timber)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.util)
    implementation(libs.compose.ui.preview)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons)
    implementation(libs.lifecycle.runtime)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.compose.runtime.livedata)
    implementation(libs.compose.activity)
    implementation(libs.compose.constraintlayout)
    implementation(libs.navigation.reimagined.hilt)
    implementation(libs.glide.compose) {
        exclude(group = "androidx.test")
    }

    implementation(libs.collections.immutable)

    // Accompanist
    implementation(libs.accompanist.swiperefresh)
    implementation(libs.accompanist.placeholder)

    implementation(libs.reorderable)

    implementation(libs.resultat)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Retrofit
    implementation(libs.retrofit)

    // Test
    testImplementation(libs.junit)
    testImplementation(libs.strikt)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.core.testing)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.compose.test.junit4)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.test.manifest)
}