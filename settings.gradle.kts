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
include(":feature:jobcards")
include(":feature:vehicles")
include(":feature:clients")
include(":feature:employees")
include(":core")
include(":core:auth")
include(":core:database")
include(":core:navigation")
include(":core:designsystem")
