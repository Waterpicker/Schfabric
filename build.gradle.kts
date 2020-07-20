plugins {
    id("fabric-loom")
    // TODO: Licenser
    //id("net.minecrell.licenser") version "0.4.1"
}

// Versioning stuff
val minecraftVersion: String by rootProject
val loaderVersion: String by rootProject
val yarnBuild: String by rootProject

val baseVersion: String by rootProject
val version: String = "$baseVersion+$minecraftVersion"

// Source sets
val main by sourceSets
val testmod by sourceSets.registering {
    this.compileClasspath += main.compileClasspath
    this.runtimeClasspath += main.runtimeClasspath
}

logger.lifecycle("""
Building XYSYDS TODO $baseVersion 
""")

repositories {
    gradlePluginPortal()
    mavenCentral()
    jcenter()
    fabric()
}

dependencies {
    minecraft(minecraftVersion)
    yarn(minecraftVersion, yarnBuild)
    `fabric-loader`(loaderVersion)
    implementation("org.jetbrains:annotations:19.0.0")

    afterEvaluate {
        testmodImplementation(main.output)
    }
}

configure<JavaPluginConvention> {
    this.sourceCompatibility = JavaVersion.VERSION_1_8
    this.targetCompatibility = JavaVersion.VERSION_1_8
}

/*license {

}*/
