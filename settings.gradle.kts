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
    }
}

rootProject.name = "Prodacc"
include(":app")
include(":feature")
include(":core")
include(":core:auth")
include(":core:data")
include(":core:navigation")
include(":core:designsystem")
