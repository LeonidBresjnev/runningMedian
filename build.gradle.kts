plugins {
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.dataframe") version "2.2.10"
}

group = "wasm.project.demo"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:dataframe:1.0.0-Beta2")
    implementation("org.jetbrains.lets-plot:lets-plot-kotlin-jvm:4.10.0")
    runtimeOnly("org.jetbrains.lets-plot:lets-plot-batik:4.6.2")
    runtimeOnly("ch.qos.logback:logback-classic:1.5.18")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(20)
}