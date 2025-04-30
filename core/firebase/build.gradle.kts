plugins {
    id("earalarm.android.library")
    id("earalarm.android.compose")
}

android {
    namespace = "com.dev.firebase"
}

dependencies {
    api(platform(libs.firebase.bom))
    api(libs.firebase.analytics.ktx)
    api(libs.firebase.crashlytics.ktx)
}