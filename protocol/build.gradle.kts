plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.android.junit5)
}

group = "network.xyo"

android {
    compileSdk = 36

    defaultConfig {
        minSdk = 23

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["runnerBuilder"] = "de.mannodermaus.junit5.AndroidJUnit5Builder"
        consumerProguardFiles("consumer-rules.pro")
    }

    testOptions {
        targetSdk = 36
    }

    lint {
        targetSdk = 36
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    namespace = "network.xyo.chain.protocol"
}

dependencies {
    implementation(project(":sdk"))

    ksp(libs.moshi.codegen)

    testImplementation(libs.junit.jupiter)
    testImplementation(libs.org.json)
    testImplementation(libs.kotlinx.coroutines.core)
}
