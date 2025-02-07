pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io") // Add this line
    }
}

rootProject.name = "JobKeep"
include(":app")
include(":core")
include(":core:auth")
include(":core:designsystem")
include(":core:data")
