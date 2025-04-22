plugins {
    id("earalarm.android.library")
}

android {
    namespace = "com.dev.earalarm.core.data"
}

dependencies {
    implementation(project(":core:model"))
}