plugins {
    kotlin("jvm") version "1.7.10"
    `maven-publish`
}

subprojects {
    group = "dev.gaabriel.clubs"
    version = Library.Version
    apply<MavenPublishPlugin>()

    tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class) {
        kotlinOptions {
            freeCompilerArgs = freeCompilerArgs + listOf("-Xexplicit-api=strict", "-opt-in=kotlin.RequiresOptIn")
            jvmTarget = "1.8"
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
}