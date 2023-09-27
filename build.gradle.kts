plugins {
    kotlin("jvm") version "1.9.0"
    application
}

group = "com.thabnir"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.formdev:flatlaf:2.6")
    implementation("com.formdev:flatlaf-intellij-themes:2.6")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("MainKt")
}
