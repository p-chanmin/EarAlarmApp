plugins {
    id("earalarm.android.feature")
}

android {
    namespace = "com.dev.earalarm.feature.timer"
}

dependencies {
    implementation(libs.play.review.ktx)
    implementation(libs.play.app.update.ktx)
    implementation(project(":core:alarm"))
}