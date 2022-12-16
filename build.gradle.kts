import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    kotlin("jvm")
    `maven-publish`
}

subprojects {
    group = Library.Group
    version = Library.Version
    apply<MavenPublishPlugin>()

    tasks.withType<KotlinCompile> {
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