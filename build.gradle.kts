val majorVersion by extra(3)
val minorVersion by extra(1)
val patchVersion by extra(10)

plugins {
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.protobuf) apply false
    alias(libs.plugins.android.junit5) apply false
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}

tasks.register("test") {
    description = "Run tests for all modules"
    group = "verification"
    dependsOn(":sdk:test", ":protocol:test")
}

tasks.register("testProtocol") {
    description = "Run tests for the protocol module"
    group = "verification"
    dependsOn(":protocol:test")
}
