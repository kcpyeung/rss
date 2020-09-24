plugins {
    kotlin("jvm") version "1.4.10"
}

repositories {
    mavenCentral()
}

dependencies {
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
