plugins {
    id("earalarm.android.library")
}

android {
    namespace = "com.dev.earalarm.core.alarm"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:data"))
    implementation(project(":core:navigation"))
    implementation(project(":core:firebase"))
}