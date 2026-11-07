plugins {
    `java-library`
}

// The version starts with the SDRplay API version pinned in native/versions.env:
// jsdrplay 3.15.x targets API 3.15. Workflows pass -Pversion for tagged,
// nightly and pull request builds.
val apiVersion = file("native/versions.env").readLines()
    .first { it.startsWith("SDRPLAY_API_VERSION=") }
    .substringAfter("=")
version = providers.gradleProperty("version").getOrElse("$apiVersion.0")

repositories {
    mavenCentral()
}

java {
    withSourcesJar()
    withJavadocJar()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.13.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<JavaCompile>().configureEach {
    // Java 23 is the newest Temurin release available on every CI platform.
    options.release = 23
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(listOf("-Xlint:all", "-Xlint:-restricted", "-Werror"))
}

tasks.javadoc {
    (options as StandardJavadocDocletOptions).addStringOption("Xdoclint:all,-missing", "-quiet")
}

tasks.jar {
    from(files("LICENSE", "THIRD-PARTY-NOTICES.md")) {
        into("META-INF")
    }
    manifest {
        attributes(
            "Implementation-Title" to "jsdrplay",
            "Implementation-Version" to project.version,
            "Automatic-Module-Name" to "io.github.dsheirer.sdrplay",
            "Main-Class" to "io.github.dsheirer.sdrplay.SDRplayInfo",
            "Enable-Native-Access" to "ALL-UNNAMED")
    }
}

tasks.test {
    useJUnitPlatform()
    jvmArgs("--enable-native-access=ALL-UNNAMED")
    // Written by native/probe-layouts.sh. Without it the layout check is skipped,
    // unless -PrequireLayoutProbe is set, as CI does.
    systemProperty("jsdrplay.layout.file",
        layout.buildDirectory.file("sdrplay-api/layout.properties").get().asFile.absolutePath)
    systemProperty("jsdrplay.layout.required", providers.gradleProperty("requireLayoutProbe").isPresent)
    systemProperty("jsdrplay.hardware.test", providers.gradleProperty("hardwareTest").getOrElse("false"))
    providers.gradleProperty("libraryPath").orNull?.let { systemProperty("jsdrplay.library.path", it) }
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
    }
}
