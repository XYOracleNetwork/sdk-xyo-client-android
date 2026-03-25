plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.android.junit5)
    `maven-publish`
}

group = "network.xyo"

val majorVersion: Int by rootProject.extra
val minorVersion: Int by rootProject.extra
val patchVersion: Int by rootProject.extra

val verString = "$majorVersion.$minorVersion(Build-$patchVersion)"

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

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    packaging {
        resources {
            excludes += "META-INF/versions/9/OSGI-INF/MANIFEST.MF"
        }
    }

    namespace = "network.xyo.chain.protocol"
}

publishing {
    publications {
        register<MavenPublication>("Production") {
            artifact("${layout.buildDirectory.get()}/outputs/aar/protocol-release.aar") {
                builtBy(tasks.named("assemble"))
            }
            groupId = "network.xyo"
            artifactId = "sdk-xyo-client-android-protocol"
            version = verString

            pom.withXml {
                val dependenciesNode = asNode().let { node ->
                    (node.children() as List<*>)
                        .filterIsInstance<groovy.util.Node>()
                        .find { it.name().toString().endsWith("dependencies") }
                        ?: node.appendNode("dependencies")
                }
                // Add the sdk module dependency with JitPack coordinates
                val sdkNode = (dependenciesNode as groovy.util.Node).appendNode("dependency")
                sdkNode.appendNode("groupId", "com.github.xyoraclenetwork.sdk-xyo-client-android")
                sdkNode.appendNode("artifactId", "sdk-xyo-client-android")
                sdkNode.appendNode("version", "$majorVersion.$minorVersion.$patchVersion")

                configurations.getByName("implementation").allDependencies.forEach { dep ->
                    if (dep.name != "unspecified") {
                        val dependencyNode = (dependenciesNode as groovy.util.Node).appendNode("dependency")
                        dependencyNode.appendNode("groupId", dep.group)
                        dependencyNode.appendNode("artifactId", dep.name)
                        dependencyNode.appendNode("version", dep.version)
                    }
                }
            }
        }
    }
}

dependencies {
    implementation(project(":sdk"))

    ksp(libs.moshi.codegen)

    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
    testImplementation(libs.org.json)
    testImplementation(libs.kotlinx.coroutines.core)
}
