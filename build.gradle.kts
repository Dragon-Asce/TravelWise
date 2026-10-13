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
    
    // Logging
    implementation("ch.qos.logback:logback-classic:1.4.14")
    
    testImplementation(kotlin("test"))
}