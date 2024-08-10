pluginManagement {
    repositories {
        includeBuild("build-logic")
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


rootProject.name = "MyBudget"
include(":app")
include(":core:designsystem")
include(":features:login")
include(":core:common")
include(":features:setup")
include(":core:database")
include(":core:model")
include(":core:data")
