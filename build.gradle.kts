plugins {
    kotlin("jvm") version "2.1.0"
    application
    id("com.gradleup.shadow") version "9.0.0-beta8"
}

group = "com.mtkrm"
version = "0.1"

application {
    mainClass.set("MainKt")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    // https://mvnrepository.com/artifact/io.mockk/mockk
    testImplementation("io.mockk:mockk:1.13.16")

    implementation(kotlin("stdlib"))
    implementation("org.jline:jline:3.25.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest.attributes["Main-Class"] = "MainKt"
}

tasks.withType<JavaExec> {
    standardInput = System.`in`
}

// TODO: Create a task to compile the C program "raw_mode.c".

kotlin {
    jvmToolchain(17)
}
