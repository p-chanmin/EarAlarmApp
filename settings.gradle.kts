pluginManagement {
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "Ear Alarm"
include(":app")

include(":feature:main")
include(":feature:timer")
include(":feature:setting")

include(":core:model")
include(":core:navigation")
include(":core:designsystem")
include(":core:data")
include(":core:alarm")
include(":core:admob")
include(":core:firebase")
include(":core:testing")
