plugins {
    id("earalarm.android.library")
    id("earalarm.android.kotlin.serialization")
}

android {
    namespace = "com.dev.earalarm.core.data"
}

dependencies {
    implementation(libs.datastore.preferences)

    implementation(project(":core:model"))
}