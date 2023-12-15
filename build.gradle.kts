val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val commons_codec_version: String by project
// Logger
val micrologging_version: String by project
val logbackclassic_version: String by project
// Koin
val koin_ktor_version: String by project
val koin_ksp_version: String by project

plugins {
    kotlin("jvm") version "1.9.10"
    id("io.ktor.plugin") version "2.3.5"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.10"
    // KSP for Koin Annotations
    id("com.google.devtools.ksp") version "1.9.10-1.0.13"
}

group = "com.example"
version = "0.0.1"

application {
//    mainClass.set("com.example.ApplicationKt")
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

//tasks.create("stage") {
//    dependsOn("installDist")
//}

repositories {
    mavenCentral()
    maven("https://jitpack.io") // For Swagger UI
}


val javaVersion = JavaVersion.VERSION_17

tasks.withType<JavaCompile> {
    sourceCompatibility = javaVersion.toString()
    targetCompatibility = javaVersion.toString()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = javaVersion.toString()
}

val sshAntTask = configurations.create("sshAntTask")


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
    // https://github.com/LukasForst/ktor-api-key
    implementation("dev.forst:ktor-api-key:1.1.0")
    //.env
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")
    //ssh
    sshAntTask("org.apache.ant:ant-jsch:1.10.13")
    // Koin for Dependency Injection
    implementation("io.insert-koin:koin-ktor:$koin_ktor_version") // Koit for Ktor
    implementation("io.insert-koin:koin-logger-slf4j:$koin_ktor_version") // Koin Logger
    implementation("io.insert-koin:koin-annotations:$koin_ksp_version") // Koin Annotations for KSP
    ksp("io.insert-koin:koin-ksp-compiler:$koin_ksp_version") // Koin KSP Compiler for KSP


}
// Java 17
// https://kotlinlang.org/docs/get-started-with-jvm-gradle-project.html#explore-the-build-script
kotlin { // Extension for easy setup
    jvmToolchain(17) // Target version of generated JVM bytecode
}

val buildingJarFileName = "temp-mayorca-server-api.jar"
val startingJarFileName = "mayorca-server-api.jar"

val serverUser = "m37moud"//mahmoud
val serverHost = "102.37.213.13"//192.168.1.6
val serverSshKey = file("keys/id_rsa")
val deleteLog = true
val lockFileName = ".serverLock"

val serviceName = "mayorca"
val serverFolderName = "mayorcaTestServer"

ktor {
    fatJar {
        archiveFileName.set(buildingJarFileName)
    }
//    docker {
//        jreVersion.set(JavaVersion.VERSION_17)
//        localImageName.set("mayorca-api-docker-image")
//        imageTag.set("0.0.1-preview")
//        portMappings.set(listOf(
//            io.ktor.plugin.features.DockerPortMapping(
//                8080,
//                8080,
//                io.ktor.plugin.features.DockerPortMappingProtocol.TCP
//            )
//        ))
//
//        externalRegistry.set(
//            io.ktor.plugin.features.DockerImageRegistry.dockerHub(
//                appName = provider { "ktor-app" },
//                username = providers.environmentVariable("DOCKER_HUB_USERNAME"),
//                password = providers.environmentVariable("DOCKER_HUB_PASSWORD")
//            )
//        )
//    }
}


ant.withGroovyBuilder {
    "taskdef"(
        "name" to "scp",
        "classname" to "org.apache.tools.ant.taskdefs.optional.ssh.Scp",
        "classpath" to configurations["sshAntTask"].asPath
    )
    "taskdef"(
        "name" to "ssh",
        "classname" to "org.apache.tools.ant.taskdefs.optional.ssh.SSHExec",
        "classpath" to configurations["sshAntTask"].asPath
    )
}

fun sudoIfNeeded(): String {
    if (serverUser.trim() == "root") {
        return ""
    }
    return "sudo "
}

fun sshCommand(command: String, knownHosts: File) = ant.withGroovyBuilder {
    "ssh"(
        "host" to serverHost,
        "username" to serverUser,
        "keyfile" to serverSshKey,
        "trust" to true,
        "knownhosts" to knownHosts,
        "command" to command
    )
}

task("cleanAndDeploy") {
    dependsOn("clean", "deploy")
}


task("deploy") {
    dependsOn("buildFatJar")
    ant.withGroovyBuilder {
        doLast {
            val knownHosts = File.createTempFile("knownhosts", "txt")
            try {
                println("Make sure the $serverFolderName folder exists if doesn't")
                sshCommand(
                    "mkdir -p \$HOME/$serverFolderName",
                    knownHosts
                )
                println("Lock the server requests...")
                sshCommand(
                    "touch \$HOME/$serverFolderName/$lockFileName",
                    knownHosts
                )
                println("Deleting the previous building jar file if exists...")
                sshCommand(
                    "rm \$HOME/$serverFolderName/$buildingJarFileName -f",
                    knownHosts
                )
                println("Uploading the new jar file...")
                val file = file("build/libs/$buildingJarFileName")
                "scp"(
                    "file" to file,
                    "todir" to "$serverUser@$serverHost:/\$HOME/$serverFolderName",
                    "keyfile" to serverSshKey,
                    "trust" to true,
                    "knownhosts" to knownHosts
                )
                println("Upload done, attempt to stop the current ktor server...")
                sshCommand(
                    "${sudoIfNeeded()}systemctl stop $serviceName",
                    knownHosts
                )
                println("Server stopped, attempt to delete the current ktor server jar...")
                sshCommand(
                    "rm \$HOME/$serverFolderName/$startingJarFileName -f",
                    knownHosts,
                )
                println("The old ktor server jar file has been deleted, now let's rename the new jar file")
                sshCommand(
                    "mv \$HOME/$serverFolderName/$buildingJarFileName \$HOME/$serverFolderName/$startingJarFileName",
                    knownHosts
                )
                if (deleteLog) {
                    sshCommand(
                        "rm /var/log/$serviceName.log -f",
                        knownHosts
                    )
                    println("The $serviceName log at /var/log/$serviceName.log has been removed")
                }
                println("Unlock the server requests...")
                sshCommand(
                    "rm \$HOME/$serverFolderName/$lockFileName -f",
                    knownHosts
                )
                println("Now let's start the ktor server service!")
                sshCommand(
                    "${sudoIfNeeded()}systemctl start $serviceName",
                    knownHosts
                )
                println("Done!")
            } catch (e: Exception) {
                println("Error: ${e.message}")
            } finally {
                knownHosts.delete()
            }
        }
    }
}

task("upgrade") {
    ant.withGroovyBuilder {
        doLast {
            val knownHosts = File.createTempFile("knownhosts", "txt")
            try {
                println("Update repositories...")
                sshCommand(
                    "${sudoIfNeeded()}apt update",
                    knownHosts
                )
                println("Update packages...")
                sshCommand(
                    "${sudoIfNeeded()}apt upgrade -y",
                    knownHosts
                )
                println("Done")
            } catch (e: Exception) {
                println("Error while upgrading server packages: ${e.message}")
            } finally {
                knownHosts.delete()
            }
        }
    }
}

abstract class ProjectNameTask : DefaultTask() {

    @TaskAction
    fun greet() = println("The project name is ${project.name}")
}

tasks.register<ProjectNameTask>("projectName")
