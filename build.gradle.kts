plugins {
    kotlin("jvm") version "1.4.10"
    application
}

application {
    mainClass.set("org.net.rss.ReaderKt")
}
tasks.jar {
    manifest {
        attributes["Main-Class"] = "org.net.rss.ReaderKt"
    }
    configurations["compileClasspath"].forEach { file: File ->
        from(zipTree(file.absoluteFile))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("io.mockk:mockk:1.10.0")
    testImplementation("org.hamcrest:hamcrest-all:1.3")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")
}

tasks.test {
    useJUnitPlatform {
        includeEngines("junit-jupiter")
    }
}

tasks.compileKotlin {
    kotlinOptions {
        apiVersion = "1.4"
        languageVersion = "1.4"
        jvmTarget = "11"
    }
}
