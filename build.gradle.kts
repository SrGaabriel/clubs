plugins {
    kotlin("jvm") version Dependencies.KotlinVersion
    `maven-publish`
}

subprojects {
    group = "dev.gaabriel.clubs"
    version = Dependencies.ProjectVersion
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