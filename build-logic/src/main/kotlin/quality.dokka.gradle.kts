import org.jetbrains.dokka.gradle.DokkaExtension
import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier

plugins {
    id("org.jetbrains.dokka")
}

val dokkaVisibility: Set<VisibilityModifier> =
    setOf(
        VisibilityModifier.Public,
        VisibilityModifier.Protected,
        VisibilityModifier.Private,
        VisibilityModifier.Package,
        VisibilityModifier.Internal,
    )

extensions.configure<DokkaExtension> {
    dokkaSourceSets.configureEach {
        documentedVisibilities.set(dokkaVisibility)
        perPackageOption {
            matchingRegex.set(".*internal.*")
            suppress.set(true)
        }
    }
}

configurations.matching { it.name.startsWith("dokka") }.configureEach {
    resolutionStrategy.eachDependency {
        if (requested.group.startsWith("com.fasterxml.jackson")) {
            useVersion("2.15.3")
        }
    }
}
