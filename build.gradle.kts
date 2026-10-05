plugins {
    kotlin("jvm") version "2.4.20"
    id("org.jetbrains.compose") version "1.12.1"
    id("org.jetbrains.kotlin.plugin.compose") version "2.4.20"
    id("org.jlleitschuh.gradle.ktlint") version "14.2.0"
}

group = "guru.bisser"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.runtime)
    implementation(compose.material3)
    implementation("com.mysql:mysql-connector-j:9.4.0")
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(21)
}

compose.desktop {
    application {
        mainClass = "guru.bisser.MainKt"
    }
}

tasks.test {
    useJUnitPlatform()
}
