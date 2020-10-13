plugins {
    kotlin("jvm") version "1.4.10"
    application
}

application {
    mainClass.set("org.net.rss.ServerKt")
}
tasks.jar {
    manifest {
        attributes["Main-Class"] = "org.net.rss.ServerKt"
    }
    configurations["compileClasspath"].forEach { file: File ->
        from(zipTree(file.absoluteFile))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.11.3")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.11.3")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.11.3")

    implementation("org.http4k:http4k-core:3.262.0")
    implementation("org.http4k:http4k-server-netty:3.262.0")
    implementation("org.http4k:http4k-client-apache:3.262.0")
    testImplementation("io.mockk:mockk:1.10.0")
    testImplementation("org.hamcrest:hamcrest-all:1.3")
    testImplementation("org.http4k:http4k-testing-hamkrest:3.266.0")
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
