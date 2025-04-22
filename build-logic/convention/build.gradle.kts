import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

group = "kr.ac.tukorea.android.earalarm.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {
    implementation(libs.android.gradlePlugin)
    implementation(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.compiler.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "earalarm.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }

        register("androidHilt") {
            id = "earalarm.android.hilt"
            implementationClass = "AndroidHiltConventionPlugin"
        }

        register("androidLibrary") {
            id = "earalarm.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }

        register("androidFeature") {
            id = "earalarm.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }

        register("androidCompose") {
            id = "earalarm.android.compose"
            implementationClass = "AndroidComposeConventionPlugin"
        }

        register("androidKotlinSerialization") {
            id = "earalarm.android.kotlin.serialization"
            implementationClass = "AndroidKotlinSerializationConventionPlugin"
        }
    }
}