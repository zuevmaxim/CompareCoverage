plugins {
    java
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":fuzz"))
    implementation("org.ow2.asm:asm:6.0")
    implementation("org.ow2.asm:asm-commons:6.0")
    implementation("org.ow2.asm:asm-util:6.0")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

tasks.jar {
    manifest {
        attributes(
            "PreMain-Class" to "jwp.agent.AgentBootstrap",
            "Can-Redefine-Classes" to true,
            "Can-Retransform-Classes" to true
        )
    }
}