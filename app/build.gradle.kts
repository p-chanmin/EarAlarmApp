import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("earalarm.android.application")
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
}

android {
    namespace = "kr.ac.tukorea.android.earalarm"
    compileSdk = 35

    defaultConfig {
        applicationId = "kr.ac.tukorea.android.earalarm"
        minSdk = 26
        targetSdk = 34
        versionCode = 22
        versionName = "1.5.5"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    signingConfigs {
        getByName("debug") {
            keyAlias = getPropertyKey("SIGNED_KEY_ALIAS")
            keyPassword = getPropertyKey("SIGNED_KEY_PASSWORD")
            storePassword = getPropertyKey("SIGNED_STORE_PASSWORD")
            storeFile = file(getPropertyKey("SIGNED_STORE_FILE"))
        }
    }

    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("debug")
            isDebuggable = false
        }
    }
}

fun getPropertyKey(propertyKey: String): String {
    return gradleLocalProperties(rootDir, providers).getProperty(propertyKey)
}

dependencies {
    implementation(project(":feature:main"))
}