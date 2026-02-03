import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ksp)
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.compileKotlin {
    compilerOptions {
        allWarningsAsErrors = true
        jvmTarget = JvmTarget.JVM_21
    }
}

dependencies {
    implementation(libs.kotlin.extensions)

    // Flow
    implementation(libs.coroutines.core)

    // Hilt
    implementation(libs.dagger)
    ksp(libs.dagger.compiler)

    // Resultat - Kotlin Result with loading state support
    implementation(libs.resultat)

    // Test dependencies
    testImplementation(libs.junit)
    testImplementation(libs.strikt)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine) // For Flow testing
    testImplementation(libs.coroutines.test)
}