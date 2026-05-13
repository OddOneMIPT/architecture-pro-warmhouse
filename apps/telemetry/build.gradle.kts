plugins {
    kotlin("jvm") version "2.2.21"
    id("io.ktor.plugin") version "3.2.3"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.2.21"
    application
}

group = "com.example.telemetry"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-netty")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-serialization-kotlinx-json")

    implementation("ch.qos.logback:logback-classic:1.5.21")

    implementation("org.apache.kafka:kafka-clients:4.1.1")

    implementation("com.clickhouse:clickhouse-jdbc:0.9.4")
    implementation("com.zaxxer:HikariCP:7.0.2")
}

application {
    mainClass.set("com.example.telemetry.ApplicationKt")
}