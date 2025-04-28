import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("earalarm.android.library")
    id("earalarm.android.compose")
}

android {
    namespace = "com.dev.core.admob"

    buildTypes {
        getByName("debug") {
            buildConfigField("String", "ADMOB_APP_ID", "\"${getPropertyKey("TEST_ADMOB_APP_ID")}\"")
            manifestPlaceholders["ADMOB_APP_ID"] = getPropertyKey("TEST_ADMOB_APP_ID")

            buildConfigField("String", "ADMOB_BANNER_ID", "\"${getPropertyKey("TEST_ADMOB_BANNER_ID")}\"")
            buildConfigField("String", "ADMOB_INTERSTITIAL_ALARM_ID", "\"${getPropertyKey("TEST_ADMOB_INTERSTITIAL_ALARM_ID")}\"")
        }

        getByName("release") {
            buildConfigField("String", "ADMOB_APP_ID", "\"${getPropertyKey("RELEASE_ADMOB_APP_ID")}\"")
            manifestPlaceholders["ADMOB_APP_ID"] = getPropertyKey("RELEASE_ADMOB_APP_ID")

            buildConfigField("String", "ADMOB_BANNER_ID", "\"${getPropertyKey("RELEASE_ADMOB_BANNER_ID")}\"")
            buildConfigField("String", "ADMOB_INTERSTITIAL_ALARM_ID", "\"${getPropertyKey("RELEASE_ADMOB_INTERSTITIAL_ALARM_ID")}\"")
        }
    }
}

fun getPropertyKey(propertyKey: String): String {
    return gradleLocalProperties(rootDir, providers).getProperty(propertyKey)
}

dependencies {
    api(libs.play.services.ads)
}