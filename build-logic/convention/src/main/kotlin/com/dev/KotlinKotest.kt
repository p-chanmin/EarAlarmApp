package com.dev

import com.dev.philo.libs
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

internal fun Project.configureKotest() {
    configureJUnit()
    dependencies {
        "testImplementation"(libs.findLibrary("kotest.runner").get())
        "testImplementation"(libs.findLibrary("kotest.assertions").get())
    }
}

internal fun Project.configureJUnit() {
    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }
}