plugins {
    id("earalarm.android.feature")
}

android {
    namespace = "com.dev.earalarm.feature.timer"
}

dependencies {
    implementation(project(":core:alarm"))
}