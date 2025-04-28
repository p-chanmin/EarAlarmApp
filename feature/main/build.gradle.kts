plugins {
    id("earalarm.android.feature")
}

android {
    namespace = "com.dev.earalarm.feature.main"
}

dependencies {
    implementation(project(":feature:timer"))
    implementation(project(":feature:setting"))
}