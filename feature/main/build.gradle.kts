plugins {
    id("earalarm.android.feature")
}

android {
    namespace = "com.dev.earalarm.feature.main"
}

dependencies {
    implementation(libs.play.review.ktx)
    implementation(libs.play.app.update.ktx)
    implementation(project(":feature:timer"))
    implementation(project(":feature:setting"))
}