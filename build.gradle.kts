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

kotlin {
    jvmToolchain(17)
}

/**
 * Builds the C programs (`raw_mode.c` and `disable_raw_mode.c`).
 */
tasks.register<Exec>("compileCPrograms") {
    val scriptDir = file("./script")
    val binDir = file("./script/bin").apply { mkdirs() }

    val cFiles = listOf("raw_mode.c", "disable_raw_mode.c").map { scriptDir.resolve(it) }
    val binaries = cFiles.map { binDir.resolve(it.nameWithoutExtension) }

    println("------ Compiling C scripts ------")

    val osName = System.getProperty("os.name").lowercase()

    val compileCommand =
        if (osName.contains("win")) {
            // Windows: Use MSVC `cl`
            listOf("cmd", "/c", "cl") + cFiles.map { it.absolutePath } + "/Fe:${binDir.absolutePath}/"
        } else {
            // macOS / Linux: Use `gcc`
            listOf(
                "sh",
                "-c",
                cFiles.joinToString(" && ") { "gcc -o ${binDir.resolve(it.nameWithoutExtension).absolutePath} ${it.absolutePath}" },
            )
        }

    commandLine(compileCommand)

    outputs.files(binaries)
}

/**
 * Bundles all the dependencies into a jar file.
 */
tasks.named("shadowJar") {
    dependsOn("compileCPrograms")
}
