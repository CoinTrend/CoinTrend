import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.dagger.hilt)
    alias(libs.plugins.room)
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_21
        allWarningsAsErrors = true
    }
}

android {
    compileSdk = 37

    defaultConfig {
        minSdk = 23
        testOptions.targetSdk = 37
        lint.targetSdk = 37

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    // Room schema export to allow DB auto migrations between versions
    room {
        schemaDirectory("$projectDir/database/roomSchemas")
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        buildConfig = true
    }

    namespace = "com.cointrend.data"
}


dependencies {

    implementation(project(":domain"))

    // Kotlin extensions
    implementation(libs.kotlin.extensions)

    coreLibraryDesugaring(libs.desugar)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Timber
    implementation(libs.timber)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)

    // Data Store
    implementation(libs.datastore.preferences)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Test dependencies
    testImplementation(libs.junit)
    testImplementation(libs.strikt)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine) // For Flow testing
    testImplementation(libs.coroutines.test)

    // Android Test dependencies
    androidTestImplementation(libs.androidx.junit)

}