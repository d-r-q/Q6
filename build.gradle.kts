plugins {
    kotlin("jvm") version "1.8.0"
    id("application")
}

group = "pro.azhidkov.q6"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.postgresql:postgresql:42.5.1")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("org.flywaydb:flyway-core:9.15.1")
    implementation("org.jetbrains.exposed:exposed-core:0.41.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.41.1")

    implementation(platform("org.http4k:http4k-bom:4.40.0.0"))
    implementation("org.http4k:http4k-server-undertow")
    implementation("org.http4k:http4k-cloudnative")
    implementation("org.http4k:http4k-template-thymeleaf")

    implementation("ch.qos.logback:logback-classic:1.3.5")
    implementation("at.favre.lib:bcrypt:0.10.2")

    testImplementation(kotlin("test"))
    testImplementation("org.testcontainers:postgresql:1.17.4")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("org.jsoup:jsoup:1.15.3")
    testImplementation("io.github.ulfs:assertj-jsoup:0.1.4")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("pro.azhidkov.q6.app.Q6AppKt")
}