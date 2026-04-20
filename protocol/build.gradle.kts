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

val verString = "$majorVersion.$minorVersion.$patchVersion"

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
        unitTests.isReturnDefaultValues = true
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
            groupId = "com.github.xyoraclenetwork.sdk-xyo-client-android"
            artifactId = "sdk-xyo-client-android-protocol"
            version = verString

            pom.withXml {
                val dependenciesNode = asNode().let { node ->
                    (node.children() as List<*>)
                        .filterIsInstance<groovy.util.Node>()
                        .find { it.name().toString().endsWith("dependencies") }
                        ?: node.appendNode("dependencies")
                }
                configurations.getByName("implementation").allDependencies.forEach { dep ->
                    if (dep.name != "unspecified" && dep !is ProjectDependency) {
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
    api(project(":sdk"))
    testImplementation(project(":sdk"))

    ksp(libs.moshi.codegen)

    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
    testImplementation(libs.org.json)
    testImplementation(libs.kotlinx.coroutines.core)
}

// Run live JSON-RPC compatibility tests against the xl1-compat stub server.
// Delegates to scripts/run-live-compat.sh, which handles server lifecycle.
tasks.register<Exec>("liveCompatTest") {
    group = "verification"
    description = "Starts the xl1-compat stub server and runs Kotlin live RPC tests against it."
    workingDir = rootProject.projectDir
    commandLine("bash", "scripts/run-live-compat.sh")
}
