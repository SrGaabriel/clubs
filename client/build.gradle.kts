plugins {
    kotlin("jvm")
    `clubs-publishing`
}

repositories {
    mavenCentral()
    maven("https://jitpack.io/")
}

dependencies {
    implementation(kotlin("stdlib"))
    api(project(":common"))

    implementation("com.github.SrGaabriel.deck:deck-core:${Dependencies.DeckVersion}")
}