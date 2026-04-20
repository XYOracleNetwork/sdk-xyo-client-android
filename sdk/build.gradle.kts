import com.google.protobuf.gradle.proto

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.android.junit5)
    alias(libs.plugins.protobuf)
    `maven-publish`
}

group = "network.xyo"

val majorVersion: Int by rootProject.extra
val minorVersion: Int by rootProject.extra
val patchVersion: Int by rootProject.extra

val verCode = majorVersion * 10000000 + minorVersion * 10000 + patchVersion
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

    namespace = "sdk.xyo.client.android"

    sourceSets {
        getByName("main") {
            manifest.srcFile("src/main/AndroidManifest.xml")
            proto {
                srcDir("src/main/java/network/xyo/client/proto")
            }
        }
    }
}

dependencies {
    api(libs.kotlin.reflect)
    api(libs.kotlinx.coroutines.core)
    api(libs.moshi.kotlin)
    ksp(libs.moshi.codegen)
    implementation(libs.androidx.core.ktx)
    implementation(libs.play.services.location)
    implementation(libs.hdwallet) {
        exclude(group = "org.bouncycastle", module = "bcprov-jdk15on")
    }
    api(libs.okhttp)
    implementation(libs.bouncycastle.prov)
    implementation(libs.bignum)
    implementation(libs.androidx.datastore)
    implementation(libs.protobuf.javalite)

    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.junit5.android.test.core)
    androidTestRuntimeOnly(libs.junit5.android.test.runner)
    androidTestImplementation(libs.junit.jupiter)

    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
    testImplementation(libs.org.json)
}

publishing {
    publications {
        register<MavenPublication>("Production") {
            artifact("${layout.buildDirectory.get()}/outputs/aar/sdk-release.aar") {
                builtBy(tasks.named("assemble"))
            }
            groupId = "com.github.xyoraclenetwork.sdk-xyo-client-android"
            artifactId = "sdk-xyo-client-android-sdk"
            version = verString

            pom.withXml {
                val dependenciesNode = asNode().let { node ->
                    (node.children() as List<*>)
                        .filterIsInstance<groovy.util.Node>()
                        .find { it.name().toString().endsWith("dependencies") }
                        ?: node.appendNode("dependencies")
                }
                configurations.getByName("implementation").allDependencies.forEach { dep ->
                    if (dep.name != "unspecified") {
                        val dependencyNode = (dependenciesNode as groovy.util.Node).appendNode("dependency")
                        dependencyNode.appendNode("groupId", dep.group)
                        dependencyNode.appendNode("artifactId", dep.name)
                        dependencyNode.appendNode("version", dep.version)

                        // Exclude old BouncyCastle from hdwallet transitive
                        if (dep.name == "hdwallet") {
                            val exclusionsNode = dependencyNode.appendNode("exclusions")
                            val exclusionNode = exclusionsNode.appendNode("exclusion")
                            exclusionNode.appendNode("groupId", "org.bouncycastle")
                            exclusionNode.appendNode("artifactId", "bcprov-jdk15on")
                        }
                    }
                }
            }
        }
    }
}

protobuf {
    protoc {
        artifact = libs.protobuf.protoc.get().toString()
    }
    generateProtoTasks {
        all().configureEach {
            builtins {
                create("java") {
                    option("lite")
                }
            }
        }
    }
}
