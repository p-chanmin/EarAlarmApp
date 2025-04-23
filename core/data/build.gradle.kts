plugins {
    id("earalarm.android.library")
}

android {
    namespace = "com.dev.earalarm.core.data"
}

dependencies {
    implementation(libs.gson)
    implementation(libs.datastore.preferences)

    implementation(project(":core:model"))
}