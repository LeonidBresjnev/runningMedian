plugins {
    kotlin("jvm") version "2.1.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.0"
}

group = "wasm.project.demo"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:dataframe:0.15.0")
    implementation(dependencyNotation = "org.jetbrains.kotlinx:dataframe-jdbc:0.15.0")
    implementation("org.jetbrains.lets-plot:lets-plot-kotlin-jvm:4.10.0")
    implementation("org.jetbrains.lets-plot:lets-plot-batik:4.6.2")
    implementation("ch.qos.logback:logback-classic:1.5.18")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(20)
}