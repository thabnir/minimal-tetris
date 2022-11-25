import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.0"
    application
}

group = "com.thabnir"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

sourceSets {
    main {
        java {
            srcDirs("src/main/kotlin")
        }
    }
}

application {
    mainClass.set("MainKt")
}

dependencies {
    implementation("com.formdev:flatlaf:2.6")
    implementation("com.formdev:flatlaf-intellij-themes:2.6")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}