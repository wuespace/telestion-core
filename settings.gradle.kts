rootProject.name = "telestion-core"

// by default gradle loads dependency catalogs from gradle/${name}.versions.toml
// where ${name} is the name where the declared dependencies and bundles are available.
// You don't need to explicitly define the "dependencyResolutionManagement" closure.

// projects
include("telestion-api")
include("telestion-application")
include("telestion-services")
