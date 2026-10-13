plugins {
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"
    application
}

application {
    mainClass.set("com.travelwise.ApplicationKt")
}

dependencies {
    // Ktor Server Core & Netty Engine
    implementation("io.ktor:ktor-server-core-jvm:2.3.8")
    implementation("io.ktor:ktor-server-netty-jvm:2.3.8")
    
    // Ktor HTML DSL (untuk render UI Web)
    implementation("io.ktor:ktor-server-html-builder-jvm:2.3.8")
    
    // Ktor Client (Engine CIO & Plugins)
    implementation("io.ktor:ktor-client-core-jvm:2.3.8")
    implementation("io.ktor:ktor-client-cio-jvm:2.3.8")
    implementation("io.ktor:ktor-client-content-negotiation-jvm:2.3.8")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:2.3.8")

    // Kotlinx Serialization JSON
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.4.14")
    
    testImplementation(kotlin("test"))
}

tasks.jar {
    archiveFileName.set("travelwise.jar")
    manifest {
        attributes["Main-Class"] = "com.travelwise.ApplicationKt"
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
}

tasks.register("buildFatJar") {
    dependsOn(tasks.jar)
}