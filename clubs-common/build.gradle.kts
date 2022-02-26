plugins {
    kotlin("multiplatform")
    `clubs-publishing`
}

repositories {
    mavenCentral()
}

kotlin {
    jvm()
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Dependencies.KotlinVersion}")
            }
            explicitApi()
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
    }
}