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
                api(libs.cache4k)
                api(libs.kotlinx.coroutines)
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