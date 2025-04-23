plugins {
    id("earalarm.android.feature")
}

android {
    namespace = "com.dev.earalarm.feature.main"
}

dependencies {

    implementation(project(":feature:timer"))
    implementation(project(":feature:setting"))

    // Datastore
    implementation("androidx.datastore:datastore-preferences:1.1.4")

    // Gson
    implementation("com.google.code.gson:gson:2.10.1")

    // Google Admob
    implementation("com.google.android.gms:play-services-ads:23.1.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}