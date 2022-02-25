plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
    maven("https://jitpack.io/")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":common"))

    implementation("com.github.SrGaabriel.deck:deck-core:0.0.4-BETA")
    implementation("com.github.SrGaabriel.deck:deck-extras:0.0.4-BETA")
}