plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.android.junit5)
    `maven-publish`
}

group = "network.xyo"

val majorVersion = rootProject.extra["majorVersion"] as Int
val minorVersion = rootProject.extra["minorVersion"] as Int
val patchVersion = rootProject.extra["patchVersion"] as Int

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
            freeCompilerArgs.add("-Xannotation-default-target=param-property")
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
                listOf("api", "implementation").forEach { configName ->
                    configurations.findByName(configName)?.dependencies?.forEach { dep ->
                        if (dep.name != "unspecified") {
                            val dependencyNode = (dependenciesNode as groovy.util.Node).appendNode("dependency")
                            if (dep is ProjectDependency) {
                                dependencyNode.appendNode("groupId", "com.github.xyoraclenetwork.sdk-xyo-client-android")
                                dependencyNode.appendNode("artifactId", "sdk-xyo-client-android-sdk")
                                dependencyNode.appendNode("version", verString)
                            } else {
                                dependencyNode.appendNode("groupId", dep.group)
                                dependencyNode.appendNode("artifactId", dep.name)
                                val version = if (dep.name == "kotlin-stdlib") libs.versions.kotlin.get() else dep.version
                                dependencyNode.appendNode("version", version)
                            }
                            dependencyNode.appendNode("scope", if (configName == "api") "compile" else "runtime")
                        }
                    }
                }
            }
        }
    }
}

dependencies {
    api(dependencyFactory.createProjectDependency(":sdk"))
    testImplementation(dependencyFactory.createProjectDependency(":sdk"))

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

// Run a real XL1 CLI chain from npm and exercise the Kotlin JSON-RPC client
// against it end-to-end.
tasks.register<Exec>("cliE2eTest") {
    group = "verification"
    description = "Starts @xyo-network/xl1-cli from npm and runs Kotlin protocol e2e tests against it."
    workingDir = rootProject.projectDir
    commandLine("bash", "scripts/run-e2e-chain.sh")
}
