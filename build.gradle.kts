plugins {
    kotlin("jvm") version "1.3.72"
    id("me.champeau.gradle.jmh") version "0.5.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

configurations {
    all {
        resolutionStrategy {
            force("org.ow2.asm:asm:7.0")
        }
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    // jacoco
    implementation("org.jacoco:org.jacoco.core:0.8.5")
    implementation("com.google.guava:guava:28.2-jre")

    // jwp
    implementation(project(":fuzz"))
    implementation(project(":agent"))

    implementation("org.apache.commons:commons-compress:1.20")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

jmh {
    threads = 4
}
