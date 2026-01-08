plugins {
    kotlin("jvm") version "2.2.20"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.mongodb:mongodb-driver-sync:4.10.2")
    implementation("de.bwaldvogel:mongo-java-server:1.45.0")
    implementation("org.json:json:20231013")

    // Backend de logging para SLF4J
    //implementation("ch.qos.logback:logback-classic:1.5.6")

    //desactivar logs en consola
    implementation("org.slf4j:slf4j-nop:2.0.12")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}