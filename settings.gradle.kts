pluginManagement {
    includeBuild("build-logic")
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
rootProject.name = "oficinamecanica"

include(
    "api",
    "shared",
    "features:funcionario",
    "features:peca",
    "features:cliente",
    "features:veiculo",
    "features:servico",
    "features:ordemservico",
)
