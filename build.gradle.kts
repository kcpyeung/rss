plugins {
    kotlin("jvm") version "1.4.10"
}

repositories {
    mavenCentral()
}

dependencies {
    testCompile("org.hamcrest:hamcrest-all:1.3")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")
    //testRuntime("org.junit.platform:junit-platform-runner:1.7.0")
}

tasks.test {
    useJUnitPlatform {
        includeEngines("junit-jupiter")
    }
}

