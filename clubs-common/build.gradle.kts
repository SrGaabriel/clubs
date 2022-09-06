plugins {
    kotlin("multiplatform")
    `clubs-publishing`
}

repositories {
    mavenCentral()
}

kotlin {
    jvm()
    explicitApi()
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                api(libs.cache4k)
                api(libs.kotlinx.coroutines)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.mockk)
            }
        }
    }
}