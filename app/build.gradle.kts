import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("earalarm.android.application")
}

android {
    namespace = "kr.ac.tukorea.android.earalarm"
    compileSdk = 34

    defaultConfig {
        applicationId = "kr.ac.tukorea.android.earalarm"
        minSdk = 26
        targetSdk = 34
        versionCode = 6
        versionName = "1.4.1"

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
        getByName("debug") {
            buildConfigField("String", "ADMOB_APP_ID", "\"${getPropertyKey("TEST_ADMOB_APP_ID")}\"")
            manifestPlaceholders["ADMOB_APP_ID"] = getPropertyKey("TEST_ADMOB_APP_ID")
        }

        getByName("release") {
            signingConfig = signingConfigs.getByName("debug")
            buildConfigField("String", "ADMOB_APP_ID", "\"${getPropertyKey("RELEASE_ADMOB_APP_ID")}\"")
            manifestPlaceholders["ADMOB_APP_ID"] = getPropertyKey("RELEASE_ADMOB_APP_ID")
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