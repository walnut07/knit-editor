plugins {
    kotlin("jvm") version "2.1.0"
    application
    id("com.gradleup.shadow") version "9.0.0-beta8"
}

group = "com.mtkrm"
version = "1.0"

application {
    mainClass.set("MainKt")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(kotlin("stdlib"))
    // https://mvnrepository.com/artifact/net.java.dev.jna/jna
    implementation("net.java.dev.jna:jna:5.16.0")
    implementation("org.jline:jline:3.21.0")
    implementation("net.java.dev.jna:jna:5.13.0")
    // https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-test
    implementation("org.jetbrains.kotlin:kotlin-test:2.1.10")
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
