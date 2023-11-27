val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val commons_codec_version: String by project
// Logger
val micrologging_version: String by project
val logbackclassic_version: String by project

plugins {
    kotlin("jvm") version "1.9.10"
    id("io.ktor.plugin") version "2.3.5"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.10"
}

group = "com.example"
version = "0.0.1"

application {
//    mainClass.set("com.example.ApplicationKt")
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
    implementation("io.ktor:ktor-server-call-logging-jvm")
    implementation("io.ktor:ktor-server-status-pages-jvm")
    // Auth JWT
    implementation("io.ktor:ktor-server-auth-jvm")
    implementation("io.ktor:ktor-server-auth-jwt-jvm")
    implementation("io.ktor:ktor-server-host-common-jvm")

    implementation("io.ktor:ktor-server-netty-jvm")

    // *** Others *** //
    // Logging
    // implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("ch.qos.logback:logback-classic:$logbackclassic_version")
    implementation("io.github.microutils:kotlin-logging-jvm:$micrologging_version")

    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")

    // ktrom
    implementation("org.ktorm:ktorm-core:3.4.1")
    implementation("org.ktorm:ktorm-support-mysql:3.4.1")
//    compile ("org.ktorm:ktorm-support-mysql:3.4.1")
    implementation("mysql:mysql-connector-java:8.0.27")

    //salt password
    implementation("commons-codec:commons-codec:$commons_codec_version")
}
ktor {
    fatJar {
        archiveFileName.set("mayorca-server-api.jar")
    }
}
