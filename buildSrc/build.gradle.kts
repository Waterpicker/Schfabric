plugins {
    `kotlin-dsl`
    kotlin("jvm") version "1.3.72"
}

repositories {
    jcenter()
    mavenCentral()
    gradlePluginPortal()
    maven(url = "https://maven.fabricmc.net") {
        this.name = "Fabric"
    }
}

dependencies {
    implementation("net.fabricmc:fabric-loom:0.4-SNAPSHOT")
}
